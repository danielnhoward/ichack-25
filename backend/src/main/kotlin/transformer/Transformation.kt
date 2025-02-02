package com.example.transformer

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import com.example.ai.AIApi
import com.example.ai.ClaudeAIApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.logging.Logger

@Serializable
sealed class Transformation

class Transform(
    private val logger: Logger,
) {
    fun transform(input: String): List<Transformation> {


        return runBlocking {
            val document: Document = Jsoup.parse(input)
            println("before")
            val ai: AIApi = ClaudeAIApi().init()
            println("After")
            val transformNoAltImage: Transformer = TransformNoAltImage(logger, ai)
            transformNoAltImage.transformAll(document)
        }
    }
}
