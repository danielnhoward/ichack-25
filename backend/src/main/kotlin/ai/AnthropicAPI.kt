package com.example.ai

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.statement.readRawBytes
import io.ktor.http.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import java.util.UUID
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Serializable
data class Message(
    val role: String = "user",
    val content: List<AnthropicContent>
)

@Serializable
data class AnthropicRequest(
    val messages: List<Message>,
    val model: String = "claude-3-haiku-20240307",
    @SerialName("max_tokens") val maxTokens: Int = 1024
)

@Serializable
sealed interface AnthropicContent {
}

// First type of message
@Serializable
@SerialName("text")
data class TextContent(
    val text: String
) : AnthropicContent

// Second type of message
@Serializable
@SerialName("image")
data class ImageContent(
    val source: AnthropicSource
) : AnthropicContent

@Serializable
data class AnthropicResponse(
    val id : String,
    val type : String,
    val role : String,
    val model : String,
    val content : List<AnthropicContent>
)

@Serializable
data class AnthropicSource(
    val type : String,
    @SerialName("media_type") val mediaType: String,
    val data: String
)

@Serializable
data class MessageBatchRequest(
    @SerialName("custom_id") val customId: String,
    val params: AnthropicRequest
)

@Serializable
data class MessageBatch(
    val requests: List<MessageBatchRequest>
)

@Serializable
data class MessageBatchInfoResponse(
    val id: String,
    @SerialName("results_url") val resultsUrl: String,
    @SerialName("processing_status") val processingStatus: String
)

@Serializable
data class SingleMessageResult(
    val customId: String,
    val result: SingleMessageActualResult
)

@Serializable
data class SingleMessageActualResult(
    val message: AnthropicResponse
)

object AnthropicAPI {
    private val apiKey = System.getenv("ANTHROPIC_TOKEN")
    private val client = HttpClient(CIO)

    private val queuer = AsyncQueuer<AnthropicRequest, AnthropicResponse> { stuff ->
        val messageBatchIds = stuff.associateBy { UUID.randomUUID().toString() }
        val batchRequest = MessageBatch(messageBatchIds.entries.map { (id, req) ->
            MessageBatchRequest(id, req)
        })
        var response = makeRequest(batchRequest)
        while (response.processingStatus != "ended") {
            response = getBatchInfo(response.id)
        }

        var results = getBatchResult(response.resultsUrl)

        return@AsyncQueuer results.associate { messageBatchIds[it.customId]!! to it.result.message }
    }

    private val module = SerializersModule {
        polymorphic(AnthropicContent::class) {
            subclass(TextContent::class)
            subclass(ImageContent::class)
        }
        contextual(AnthropicRequest.serializer())
        contextual(AnthropicResponse.serializer())
    }

    private val json = Json {
        ignoreUnknownKeys = true
        serializersModule = module
        encodeDefaults = true
    }

    suspend fun request(request: AnthropicRequest): AnthropicResponse {
        return queuer.addRequest(request)
    }

    suspend fun makeRequest(request: MessageBatch): MessageBatchInfoResponse {
        try {
            val response: HttpResponse = client.post("https://api.anthropic.com/v1/messages") {
                header("x-api-key", apiKey)
                header("anthropic-version", "2023-06-01")
                contentType(ContentType.Application.Json)
                setBody(json.encodeToString(request))
            }
            println(response.bodyAsText())
            return (json.decodeFromString<MessageBatchInfoResponse>(response.bodyAsText()))
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    suspend fun getBatchInfo(id: String): MessageBatchInfoResponse {
        try {
            val response: HttpResponse = client.post("https://api.anthropic.com/v1/messages/batches/$id") {
                header("x-api-key", apiKey)
                header("anthropic-version", "2023-06-01")
                contentType(ContentType.Application.Json)
            }
            println(response.bodyAsText())
            return (json.decodeFromString<MessageBatchInfoResponse>(response.bodyAsText()))
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    suspend fun getBatchResult(url: String): List<SingleMessageResult> {
        try {
            val response: HttpResponse = client.get(url) {
                header("x-api-key", apiKey)
                header("anthropic-version", "2023-06-01")
                contentType(ContentType.Application.Json)
            }
            return response.bodyAsText().lines().map {
                json.decodeFromString(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    suspend fun toImageContent(url: String): ImageContent {
        try {
            val req = client.get(url)
            return req.readRawBytes().let {
                val type = with(req.contentType()!!) { "$contentType/$contentSubtype" }
                ImageContent(AnthropicSource("base64", type, Base64.encode(it)))
            }
        } catch (e: Exception) {
            println("Error: ${e.message}")
            throw e
        }
    }
}