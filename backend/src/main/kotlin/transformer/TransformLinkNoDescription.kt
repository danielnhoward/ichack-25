package com.example.transformer

import com.example.ai.AIApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.util.logging.Level
import java.util.logging.Logger

@Serializable
@SerialName("link")
data class HrefDescriptor(val id: String, val alt: String) : Transformation()

class TransformLinkNoDescription(
    private val logger: Logger,
    private val ai: AIApi,
) : Transformer {
    override suspend fun transformAll(document: Document): List<Transformation> {
        val images: List<Element> =
            document
                .select("a")

        return images
            .filter { it.hasAttr("href") }
            .filter { !it.hasAttr("aria-label") }
            .asyncMap { transform(it) }
    }

    override suspend fun transform(element: Element): Transformation {
        require(element.tagName() == "a") { "Must enter a a tag" }
        require(element.hasAttr("href")) { "Must have a href" }
        require(!element.hasAttr("aria-label")) { "Must not have an aria-label" }

        val imageLink: String = element.attr("href")
        val text: String = element.text()

        logger.log(Level.INFO, "Image link: $imageLink")
        println("test1")
        val imageAltText: String = ai.getUrlDescription(imageLink, text)

        val imageId: String = element.attr("data-ichack-id")

        return HrefDescriptor(imageId, imageAltText)
    }
}
