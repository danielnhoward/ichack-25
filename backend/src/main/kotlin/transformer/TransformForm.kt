package com.example.transformer

import com.example.ai.getAccessibleForm
import com.example.ai.getLabelForInput
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.util.logging.Level
import java.util.logging.Logger

const val CHANGE_WHOLE_FORM = "change-whole-form"

class TransformForm(
    private val logger: Logger,
) : Transformer {
    override fun transformAll(document: Document): List<Transformation> =
        document
            .select("form")
            .map { transform(it) }

    override fun transform(element: Element): Transformation {
        val newAccessibleForm: String = getAccessibleForm(element.toString())

        logger.log(Level.INFO, newAccessibleForm)

        return Transformation(CHANGE_WHOLE_FORM, element.toString(), newAccessibleForm)
    }
}

const val NO_INPUT_LABEL = "no-input-label"

class TransformNoInputLabel(
    private val logger: Logger,
) : Transformer {
    override fun transformAll(document: Document): List<Transformation> =
        document
            .select("form")
            .flatMap { form ->
                val inputs: List<Element> =
                    form
                        .select("input")

                inputs.filter {
                    val id = it.id()
                    form.select("label[for=$id]").isEmpty()
                }
            }.map { transform(it) }

    // Precondition: passed in input element does not have a label in the form
    // Response transformation returns the label plus the input
    override fun transform(element: Element): Transformation {
        require(element.tagName() == "input") { "element is not a input element" }

        val inputId: String = element.attr("id")
        val inputType: String = element.attr("type")
        val inputName: String = element.attr("name")
        val inputValue: String = element.attr("value")

        val inputLabel: String =
            getLabelForInput(
                id = inputId,
                type = inputType,
                name = inputName,
                value = inputValue,
            )

        logger.log(Level.INFO, "InputLabel: $inputLabel")

        val newElementString: String = inputLabel + "\n" + element.text()

        return Transformation(NO_INPUT_LABEL, element.toString(), newElementString)
    }
}
