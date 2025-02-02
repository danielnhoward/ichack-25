package com.example.transformer

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

interface Transformer {
    fun transformAll(document: Document): List<Transformation>

    fun transform(element: Element): Transformation
}
