package com.example.transformer

import com.example.ai.AIApi
import kotlinx.coroutines.runBlocking
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.util.logging.Level
import java.util.logging.Logger

const val NO_ALT_IMAGE_TYPE = "no-alt-image"

class TransformNoAltImage(
    private val logger: Logger,
    private val ai: AIApi,
) : Transformer {
    override suspend fun transformAll(document: Document): List<Transformation> {
        val images: List<Element> =
            document
                .select("img")

        return images
            .filter { !it.hasAttr("alt") }
            .asyncMap { transform(it) }
    }

    override suspend fun transform(element: Element): Transformation {
        require(element.tagName() == "img") { "Must enter a img tag" }
        require(!element.hasAttr("alt")) { "Must not have an alt" }

        val imageLink: String = element.attr("src")

        logger.log(Level.INFO, "Image link: $imageLink")

        val imageAltText: String = ai.getImageAltText(imageLink)

        val newElement: Element = element.clone()

        newElement.attr("alt", imageAltText)

        return Transformation(NO_ALT_IMAGE_TYPE, element.toString(), newElement.toString())
    }
}
