package com.example.ai

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Serializable
data class Message(
    val role: String = "user",
    val content: List<AnthropicContent>,
)

@Serializable
data class AnthropicRequest(
    val messages: List<Message>,
    val model: String = "claude-3-haiku-20240307",
    @SerialName("max_tokens") val maxTokens: Int = 1024,
)

@Serializable
sealed interface AnthropicContent

// First type of message
@Serializable
@SerialName("text")
data class TextContent(
    val text: String,
) : AnthropicContent

// Second type of message
@Serializable
@SerialName("image")
data class ImageContent(
    val source: AnthropicSource,
) : AnthropicContent

@Serializable
data class AnthropicResponse(
    val id: String,
    val type: String,
    val role: String,
    val model: String,
    val content: List<AnthropicContent>,
)

@Serializable
data class AnthropicSource(
    val type: String,
    @SerialName("media_type") val mediaType: String,
    val data: String,
)

object AnthropicAPI {
    private val apiKey = System.getenv("ANTHROPIC_TOKEN")
    private val client = HttpClient(OkHttp)

    private val module =
        SerializersModule {
            polymorphic(AnthropicContent::class) {
                subclass(TextContent::class)
                subclass(ImageContent::class)
            }
            contextual(AnthropicRequest.serializer())
            contextual(AnthropicResponse.serializer())
        }

    private val json =
        Json {
            ignoreUnknownKeys = true
            serializersModule = module
            encodeDefaults = true
        }

    suspend fun makeRequest(request: AnthropicRequest): AnthropicResponse {
        try {
            val response: HttpResponse =
                client.post("https://api.anthropic.com/v1/messages") {
                    header("x-api-key", apiKey)
                    header("anthropic-version", "2023-06-01")
                    contentType(ContentType.Application.Json)
                    setBody(json.encodeToString(request))
                }
            println(response.bodyAsText())
            return (json.decodeFromString<AnthropicResponse>(response.bodyAsText()))
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    suspend fun toImageContent(url: String): ImageContent {
        val req = client.get(url)
        val bytes: ByteArray = req.body()
        val x = Base64.encode(bytes)
        val retType = req.contentType()
        return ImageContent(AnthropicSource(type = "base64", mediaType = "${retType?.contentType}/${retType?.contentSubtype}", x))
    }
}
