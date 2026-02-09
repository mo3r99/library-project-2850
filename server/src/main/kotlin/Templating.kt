package leeds.compsci

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.random.Random
import kotlinx.html.*
import org.jetbrains.exposed.sql.*

fun Application.configureTemplating() {
    routing {
        val random = Random(System.currentTimeMillis())

        get("/") {
            call.respondHtml {
                leaderboardPage(random)
            }
        }

        get("/more-rows") {
            call.respondHtml {
                body {
                    table {
                        tbody {
                            randomRows(random)
                        }
                    }
                }
            }
        }
    }
}
