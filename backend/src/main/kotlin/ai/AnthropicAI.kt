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

private val getLabelTextPrompt = """
    You are tasked with creating a descriptive aria-label for an unlabelled link. The aria-label should be short but descriptive, providing context for users relying on screen readers. You will be given the URL and the visible text of the link.
    Here is the URL of the link:
    <url>{{URL}}</url>
    Here is the visible text of the link:
    <link_text>{{LINK_TEXT}}</link_text>
    Follow these guidelines to create an appropriate aria-label:
    1. Combine information from both the URL and link text to create a concise but informative description.
    2. If the link text is already descriptive, you may use it as a base and add additional context from the URL.
    3. If the link text is vague (e.g., "click here"), rely more heavily on information from the URL.
    4. Aim for a length of 3-7 words.
    5. Include the action or destination of the link if it's clear from the context.
    6. Avoid redundant words like "link to" or "button for" as screen readers already announce these elements.
    7. Ignore the proxy in the URL.
    Output only the aria-label description, without any additional text or explanation.
    Here are some examples:
    URL: https://www.example.com/products/smartphones
    Link text: "View our selection"
    Output: Smartphone product catalog
    URL: https://www.example.com/blog/2023/05/new-privacy-policy
    Link text: "Read more"
    Output: New privacy policy announcement
    URL: https://www.example.com/contact
    Link text: "Get in touch"
    Output: Contact Example Company
    Now, create an appropriate aria-label for the given URL and link text.
""".trimIndent()

val identifyLanguagePrompt = """
    Identify the language of the following text: {{TEXT}}
    
    Please output as ONLY a html language code, for example en.
""".trimIndent()

class AnthropicAI : AI {
    private val anthropicRequestCache: ConcurrentMap<AnthropicRequest, AnthropicResponse> = ConcurrentHashMap()
    private val imageContentCache: ConcurrentMap<String, ImageContent> = ConcurrentHashMap()

    override fun getImageAltText(url: String): String {
        val content = cachedImageContent(url)
        val req =
            AnthropicRequest(
                listOf(
                    Message(
                        content =
                            listOf(
                                content,
                                TextContent(getImageAltPrompt),
                            ),
                    ),
                ),
            )

        if (content.source.mediaType !in listOf("image/jpeg", "image/png", "image/gif", "image/webp")) {
            return ""
        }

        val res: AnthropicResponse = cachedReq(req)

        res.content.first { it is TextContent }.let {
            return (it as TextContent).text
        }
    }

    override fun getUrlDescription(
        url: String,
        text: String,
    ): String {
        val req = AnthropicRequest(
            listOf(Message(
                content= listOf(TextContent(getLabelTextPrompt.replace("{{URL}}", url).replace("{{TEXT}}", text)))
            ))
        )
        val res = cachedReq(req)
        res.content.first { it is TextContent }.let {
            return (it as TextContent).text
        }
    }

    override fun getLanguage(
        text: String
    ): String {
        val req = AnthropicRequest(
            listOf(Message(
                content=listOf(TextContent(identifyLanguagePrompt.replace("{{TEXT}}", text)))
            ))
        )
        val res = cachedReq(req)
        return res.content.first { it is TextContent }.let {
            return (it as TextContent).text
        }
    }

    private fun cachedReq(req: AnthropicRequest): AnthropicResponse {
        val cachedRes: AnthropicResponse? = anthropicRequestCache[req]

        return if (cachedRes != null) {
            cachedRes
        } else {
            val computedRes: AnthropicResponse = runBlocking { AnthropicAPI.makeRequest(req) }
            anthropicRequestCache[req] = computedRes
            computedRes
        }
    }

    private fun cachedImageContent(url: String): ImageContent {
        val cachedImageContent: ImageContent? = imageContentCache[url]

        return if (cachedImageContent != null) {
            cachedImageContent
        } else {
            val computedImageContent: ImageContent = runBlocking { AnthropicAPI.toImageContent(url) }
            imageContentCache[url] = computedImageContent
            computedImageContent
        }
    }
}
