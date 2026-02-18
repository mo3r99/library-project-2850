package leeds.compsci

import book.BookService
import common.LayoutTemplate
import io.ktor.server.application.Application
import io.ktor.server.http.content.staticResources
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.html.h1
import kotlinx.html.td
import kotlinx.html.tr
import kotlinx.html.table
import io.ktor.server.html.respondHtmlTemplate
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

suspend fun Application.configureRouting() {
    val bookService = BookService()
    val books = bookService.getAllBooks()

    routing {
        staticResources("/", "/web")

        get("/") {
            call.respondHtmlTemplate(LayoutTemplate()) {
                titleText { +"Library" }

                content {
                    h1 { +"Welcome to the Library" }
                    table {
                        tr {
                            td { +"Title" }
                            td { +"Isbn"}
                            td { +"Author" }
                        }

                        books.forEach { book ->
                            tr {
                                td { +book.key.title }
                                td { if (book.key.isbn == "0") +"N/A" else +book.key.isbn }
                                transaction {
                                    td { +book.value.name }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
