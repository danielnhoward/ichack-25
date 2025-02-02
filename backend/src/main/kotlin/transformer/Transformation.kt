package com.example.transformer

import com.example.ai.AI
import com.example.ai.AnthropicAI
import kotlinx.serialization.Serializable
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.logging.Logger

@Serializable
sealed class Transformation

class Transform(
    private val logger: Logger,
) {
    private val ai: AI = AnthropicAI(logger)

    fun transform(input: String): List<Transformation> {
        val document: Document = Jsoup.parse(input)
        val transformNoAltImage: Transformer = TransformNoAltImage(logger, ai)
        val transformNoUrlDesc: Transformer = TransformLinkNoDescription(logger, ai)
        return transformNoAltImage.transformAll(document) + transformNoUrlDesc.transformAll(document)
    }
}
