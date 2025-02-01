package com.example

import com.example.transformer.Transform
import com.example.transformer.Transformation
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import java.util.logging.Logger

@Serializable
data class PostRequest(
    val html: String,
)

fun Application.configureRouting(logger: Logger) {
    val transform = Transform(logger)

    routing {
        post {
            val html = call.receive<PostRequest>().html

            val transformations: List<Transformation> = transform.transform(html)

            call.respond(transformations)
        }
    }
}
