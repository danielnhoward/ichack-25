package com.example.transformer

import com.example.ai.AI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.util.logging.Level
import java.util.logging.Logger

@Serializable
@SerialName("button")
data class DivToButton(
    val id: String,
) : Transformation()

class TransformDivWithClick(
    private val logger: Logger,
    private val ai: AI,
) : Transformer {
    override fun transformAll(document: Document): List<Transformation> {
        val images: List<Element> =
            document
                .select("div")

        return images
            .filter { it.hasAttr("onClick") }
            .map { transform(it) }
    }

    override fun transform(element: Element): Transformation {
        require(element.hasAttr("onClick")) { "invalid" }

        val imageId: String = element.attr("data-ichack-id")

        return DivToButton(imageId)
    }
}
