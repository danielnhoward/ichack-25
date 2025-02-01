package com.example.transformer

import com.example.ai.getImageAltText
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.util.logging.Level
import java.util.logging.Logger

@Serializable
@SerialName("image")
data class ImageAlt(val id: String, val alt: String) : Transformation()

class TransformNoAltImage(
    private val logger: Logger,
) : Transformer {
    override fun transformAll(document: Document): List<Transformation> {
        val images: List<Element> =
            document
                .select("img")

        return images
            .filter { !it.hasAttr("alt") }
            .map { transform(it) }
    }

    override fun transform(element: Element): Transformation {
        require(element.tagName() == "img") { "Must enter a img tag" }
        require(!element.hasAttr("alt")) { "Must not have an alt" }

        val imageLink: String = element.attr("src")

        logger.log(Level.INFO, "Image link: $imageLink")

        val imageAltText: String = runBlocking { getImageAltText(imageLink) }

        val imageId: String = element.attr("data-ichack-id")

        return ImageAlt(imageId, imageAltText)
    }
}
