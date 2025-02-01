package com.example.transformer

import kotlinx.serialization.Serializable
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.logging.Logger

@Serializable
data class Transformation(
    val type: String,
    val input: String,
    val transformedTo: String,
)

class Transform(
    logger: Logger,
) {
    private val transformNoAltImage: Transformer = TransformNoAltImage(logger)

    fun transform(input: String): List<Transformation> {
        val document: Document = Jsoup.parse(input)

        return TODO() //transformNoAltImage.transformAll(document)
    }
}
