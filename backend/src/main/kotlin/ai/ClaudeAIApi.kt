package com.example.ai

import com.example.ai.AnthropicAPI.getBatchInfo
import com.example.ai.AnthropicAPI.getBatchResult
import com.example.ai.AnthropicAPI.makeRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.UUID

private val getImageAltPrompt = """
    You will be writing alt text for an image based on a provided description. The image description is as follows:

    Your task is to create concise, informative alt text that accurately describes the essential elements of the image for users who cannot see it. This alt text will be used by screen readers and other assistive technologies to convey the image's content and purpose.

    Guidelines for effective alt text:
    1. Be concise: Aim for 125 characters or less, but use up to 250 if necessary for complex images.
    2. Be specific: Describe the important details without being overly verbose.
    3. Convey the purpose: Focus on why the image is important in its context.
    4. Avoid redundancy: Don't repeat information already present in surrounding text.
    5. Don't use phrases like "image of" or "picture of" - screen readers already indicate that it's an image.

    To write the alt text:
    1. Carefully read and analyze the provided image description.
    2. Identify the key elements and purpose of the image.
    3. Summarize these elements concisely, focusing on what's most important.
    4. Ensure your alt text follows the guidelines mentioned above.
    5. Review and refine your alt text for clarity and brevity.

    Only return your final alt text
""".trimIndent()

class ClaudeAIApi : AIApi {

    public suspend fun init(): ClaudeAIApi {
        queuer.initialise()
        return this
    }

    private val queuer = Queuer<AnthropicRequest, AnthropicResponse> { stuff ->
        return@Queuer runBlocking {
            println("Requesting")
            val messageBatchIds = stuff.associateBy { UUID.randomUUID().toString() }
            val batchRequest = MessageBatch(messageBatchIds.entries.map { (id, req) ->
                MessageBatchRequest(id, req)
            })
            var response = makeRequest(batchRequest)
            while (response.processingStatus != "ended") {
                response = getBatchInfo(response.id)
                println("Retrying")
                delay(30000)
            }

            var results = getBatchResult(response.resultsUrl!!)

            results.associate { messageBatchIds[it.customId]!! to it.result.message }
        }
    }


    fun request(request: AnthropicRequest): AnthropicResponse {
        return queuer.addRequest(request)
    }

    override suspend fun getImageAltText(url: String): String {
        val req = AnthropicRequest(listOf(Message(content=listOf(
            AnthropicAPI.toImageContent(url),
            TextContent(getImageAltPrompt)
        ))))
        val res = request(req)
        res.content.first { it is TextContent }.let {
            return (it as TextContent).text
        }
    }
}