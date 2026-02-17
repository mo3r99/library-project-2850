package common

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.html.respondHtml
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondText
import kotlinx.html.head
import kotlinx.html.title
import kotlinx.html.body
import kotlinx.html.main
import kotlinx.html.h1
import kotlinx.html.p

fun Application.configureErrorHandling() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause" , status = HttpStatusCode.InternalServerError)
        }

        status(HttpStatusCode.NotFound) {
            call.respondHtml(HttpStatusCode.NotFound) {
                head {
                    title {
                        +"404: Not Found"
                    }
                }
                body {
                    main(classes = "container") {
                        h1 { +"404: Not Found" }
                        p { +"The requested resource could not be found." }
                    }
                }
            }
        }
    }
}
