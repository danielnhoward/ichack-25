package com.example.ai

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Collections.synchronizedList
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicBoolean

const val WAIT_TIME: Long = 10000

class Queuer<T, R>(private val op: (List<T>) -> Map<T, R>) {
    private var latch: CountDownLatch? = null
    private var responses = ConcurrentHashMap<T, R>()
    private val requests = synchronizedList<T>(mutableListOf())
    private val startedProcessing = AtomicBoolean(false)

    init {
        latch = CountDownLatch(1)
        // very hacky solution#

    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun initialise() {
        println("before")
        GlobalScope.launch {
                println("start waiting")
                delay(WAIT_TIME)
                println("done waiting")
                startedProcessing.set(true)
                serviceAll()
        }
        println("after")
    }

    fun addRequest(r: T): R {
        if (!responses.containsKey(r)) {
            requests.add(r)
        }

        latch?.await()
        println(responses.size)
        println(responses[r])
        return responses[r]!!
    }

    private fun serviceAll() {
        val req = requests.toList()
        requests.clear()
        responses = ConcurrentHashMap(op(req))

        latch?.countDown()
    }
}