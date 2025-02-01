package com.example.ai

import java.util.Collections.synchronizedList
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicBoolean

const val WAIT_TIME: Long = 1000

class Queuer<T, R>(private val op: (List<T>) -> Map<T, R>) {
    private var latch: CountDownLatch? = null
    private var responses = ConcurrentHashMap<T, R>()
    private val requests = synchronizedList<T>(mutableListOf())
    private val startedProcessing = AtomicBoolean(false)

    init {
        // very hacky solution
        latch = CountDownLatch(1)
        Thread.sleep(WAIT_TIME)
        startedProcessing.set(true)
        serviceAll()
    }

    fun addRequest(r: T): R {
        if (!responses.containsKey(r)) {
            requests.add(r)
        }

        latch?.await()
        return responses[r]!!
    }

    private fun serviceAll() {
        val req = requests.toList()
        requests.clear()
        responses = ConcurrentHashMap(op(req))

        latch?.countDown()
    }
}