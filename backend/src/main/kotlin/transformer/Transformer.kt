package com.example.transformer

import kotlinx.coroutines.coroutineScope
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

interface Transformer {
    suspend fun transformAll(document: Document): List<Transformation>

    suspend fun transform(element: Element): Transformation
}

suspend fun <T, R> List<T>.asyncMap(transform: suspend (T) -> R): List<R> {
    return coroutineScope {
        map{ element ->
            async { transform(element )}
        }.awaitAll()
    }
}