package com.example

import io.ktor.server.application.*
import java.util.logging.Logger

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain
        .main(args)
}

fun Application.module() {
    val logger = Logger.getLogger("Application")

    configureHTTP()
    configureSerialization()
    configureRouting(logger)
}
