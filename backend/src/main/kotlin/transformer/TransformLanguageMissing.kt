package com.example.transformer

import com.example.ai.AI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.util.logging.Level
import java.util.logging.Logger

@Serializable
@SerialName("language")
data class LanguageChange(
    val id: String,
    val lang: String,
) : Transformation()

class TransformLanguageMissing(
    private val logger: Logger,
    private val ai: AI,
) : Transformer {
    override fun transformAll(document: Document): List<Transformation> {
        logger.log(Level.INFO, "language ${document.selectFirst("html")?.hasAttr("lang")}")
        if (document.selectFirst("html")?.hasAttr("lang") != true) {
            logger.log(Level.INFO, "Changing language")
            return listOf(LanguageChange("0", ai.getLanguage(document.text())))
        }
        return emptyList()
    }

    override fun transform(element: Element): Transformation {
        TODO("Not yet implemented")
    }
}
