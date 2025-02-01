package com.example.ai

import com.example.transformer.asyncMap
import java.util.Collections.synchronizedList
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch

class AsyncQueuer<T, R>(private val op: suspend (List<T>) -> Map<T, R>) {
    private var latch: CountDownLatch? = null
    private var responses = ConcurrentHashMap<T, R>()
    private val requests = synchronizedList<T>(mutableListOf())

    suspend fun addRequest(r: T): R {
        if (responses.containsKey(r)) {
            return responses[r]!!
        }
        requests.add(r)
        if (latch == null) {
            latch = CountDownLatch(1)
        }
        latch?.await()
        return responses[r]!!
    }

    suspend fun serviceAll() {
        val req = requests.toList()
        requests.clear()
        responses = ConcurrentHashMap(op(req))

        latch?.countDown()
    }
}

suspend fun main() {
    val queue = AsyncQueuer<Int, Int> {
        println(it)
        it.associateWith { it }
    }
    listOf(1,2,3, 4).asyncMap {
        println("test")
        if (it == 4) {
            queue.serviceAll()
        } else {
            queue.addRequest(it)
        }
    }
}