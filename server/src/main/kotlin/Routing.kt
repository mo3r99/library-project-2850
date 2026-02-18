package leeds.compsci

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import leeds.compsci.routes.homeRoute

fun Application.configureRouting() {
    routing {
        staticResources("/", "content")

        get("/") { call.homeRoute() }
    }
}
