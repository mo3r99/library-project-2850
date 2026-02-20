package leeds.compsci.routes

import book.Book
import book.BookService
import common.LayoutTemplate
import io.github.allangomes.kotlinwind.css.LG
import io.github.allangomes.kotlinwind.css.SM
import io.github.allangomes.kotlinwind.css.XL5
import io.github.allangomes.kotlinwind.css.kw
import io.ktor.htmx.html.hx
import io.ktor.server.application.ApplicationCall
import io.ktor.server.html.respondHtmlTemplate
import kotlinx.html.a
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.h3
import kotlinx.html.id
import kotlinx.html.p
import kotlinx.html.style
import kotlinx.html.textInput

suspend fun ApplicationCall.homeRoute() {
    val bookService = BookService()
    val books = bookService.getAllBooksWithAuthors()

    renderHomePage(books)
}

suspend fun ApplicationCall.renderHomePage(books: Map<Book, String>) {
    respondHtmlTemplate(LayoutTemplate()) {
        titleText { +"Library" }

        content {
            h1 {
                style = kw.inline {
                    font.bold
                    margin.top[LG]
                    font.size[XL5]
                }
                +"Welcome to the Library"
            }

            div {
                form {
                    attributes.hx {
                        get = "/search"
                        target = "#book-list"
                        swap = "innerHTML"
                        trigger = "submit changed"
                    }

                    style = kw.inline {
                        margin.bottom[LG]
                        flex
                        flex.row
                        flex.justify_between
                        flex.gap[4]
                        height["57px"]
                    }

                    textInput(name = "search") {
                        attributes.hx {
                            get = "/search"
                            target = "#book-list"
                            swap = "innerHTML"
                            trigger = "keyup changed delay:200ms"
                        }
                        placeholder = "Search for books"
                    }
                    button { +"Search" }
                }
            }
            div {
                classes = setOf("container")
                id = "book-list"
                style = kw.inline {
                    flex
                    flex.row
                    flex.wrap
                }

                books.forEach { book, author ->
                    div {
                        style = kw.inline {
                            background.color["aliceblue"]
                            padding.all[LG]
                            max_width["350px"]
                            min_width["200px"]
                            margin.all[SM]
                            border.rounded[SM]
                        }
                        h3 { +book.title }
                        p { +author }
                        a {
                            style = kw.inline {
                                font.sm
                            }
                            href = "/book/${book.id}"
                            +"See More"
                        }
                    }
                }
            }
        }
    }
}
