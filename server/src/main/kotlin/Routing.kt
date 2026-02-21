package leeds.compsci

import io.ktor.server.application.Application
import io.ktor.server.http.content.staticResources
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import leeds.compsci.routes.bookDetailRoute
import leeds.compsci.routes.homeRoute
import leeds.compsci.routes.reserveRoute
import leeds.compsci.routes.searchRoute

fun Application.configureRouting() {
    routing {
        staticResources("/", "content")

        get("/") { call.homeRoute() }
        get("/search") { call.searchRoute() }
        get("/book/{id}") { call.bookDetailRoute() }

        post("/reserve/{bookId}/{copyId}") { call.reserveRoute() }
    }
}
