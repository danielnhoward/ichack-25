package com.example.ai

import kotlinx.coroutines.runBlocking
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

private val getImageAltPrompt =
    """
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

class AnthropicAI : AI {
    val anthropicRequestCache: ConcurrentMap<AnthropicRequest, AnthropicResponse> = ConcurrentHashMap()

    override fun getImageAltText(url: String): String {
        val req =
            AnthropicRequest(
                listOf(
                    Message(
                        content =
                            listOf(
                                runBlocking { AnthropicAPI.toImageContent(url) },
                                TextContent(getImageAltPrompt),
                            ),
                    ),
                ),
            )

        val cachedRes: AnthropicResponse? = anthropicRequestCache[req]

        val res: AnthropicResponse =
            if (cachedRes != null) {
                cachedRes
            } else {
                val computedRes: AnthropicResponse = runBlocking { AnthropicAPI.makeRequest(req) }
                anthropicRequestCache[req] = computedRes
                computedRes
            }

        res.content.first { it is TextContent }.let {
            return (it as TextContent).text
        }
    }

    override fun getUrlDescription(
        url: String,
        text: String,
    ): String {
        TODO("Not yet implemented")
    }
}
