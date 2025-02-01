package com.example.transformer

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import com.example.ai.AIApi
import com.example.ai.ClaudeAIApi
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
    private val logger: Logger,
) {
    fun transform(input: String): List<Transformation> {
        val document: Document = Jsoup.parse(input)

        val ai: AIApi = ClaudeAIApi()
        val transformNoAltImage: Transformer = TransformNoAltImage(logger, ai)

        return TODO() //transformNoAltImage.transformAll(document)
    }
}
