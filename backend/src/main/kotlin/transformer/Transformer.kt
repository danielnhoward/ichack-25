package com.example.transformer

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

interface Transformer {
    fun transformAll(document: Document): List<Transformation>

    fun transform(element: Element): Transformation
}
