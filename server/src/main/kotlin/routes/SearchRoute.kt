package leeds.compsci.routes

import book.Book
import book.BookService
import io.github.allangomes.kotlinwind.css.LG
import io.github.allangomes.kotlinwind.css.SM
import io.github.allangomes.kotlinwind.css.kw
import io.ktor.server.application.ApplicationCall
import io.ktor.server.html.respondHtml
import io.ktor.server.html.respondHtmlFragment
import kotlinx.html.HTML
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.h3
import kotlinx.html.p
import kotlinx.html.stream.appendHTML
import kotlinx.html.style

suspend fun ApplicationCall.searchRoute() {
    val query = parameters["search"].toString()

    val booksService = BookService()

    var books = emptyMap<Book, String>()
    if (query.isEmpty()) {
        books = booksService.getAllBooksWithAuthors()
    } else {
        books = booksService.searchBooks(query)
    }

    if (books.isEmpty()) {
        respondHtmlFragment {
            h1 {
                +"No books found containing '$query'"
            }
        }
    } else {
        respondHtmlFragment {
            books.forEach { book ->
                div {
                    style = kw.inline {
                        background.color["aliceblue"]
                        padding.all[LG]
                        max_width["350px"]
                        min_width["200px"]
                        margin.all[SM]
                        border.rounded[SM]
                    }
                    h3 { +book.key.title }
                    p { +book.value }
                    a {
                        style = kw.inline {
                            font.sm
                        }
                        href = "/book/${book.key.id}"
                        +"See More"
                    }
                }
            }
        }
    }
}