package com.example.transformer

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

data class Transformation(val type: String, val input: String, val transformedTo: String)

fun transform(input: String): List<Transformation> {
    val document: Document = Jsoup.parse(input)

    return emptyList()
}
