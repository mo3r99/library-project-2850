package routes

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
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
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
import uk.co.almasjid.server.authentication.sessions.UserSession
import user.Role

suspend fun ApplicationCall.homeRoute() {
    val bookService = BookService()
    val books = bookService.getAllBooksWithAuthors()

    val isStaff = sessions.get<UserSession>()?.role == Role.STAFF

    renderHomePage(books, isStaff)
}

suspend fun ApplicationCall.renderHomePage(
    books: Map<Book, String>,
    isStaff: Boolean,
) {
    val isLoggedIn = sessions.get<UserSession>() != null

    respondHtmlTemplate(LayoutTemplate(isLoggedIn)) {
        titleText { +"Library" }

        content {
            h1 {
                style =
                    kw.inline {
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

                    style =
                        kw.inline {
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

            if (isStaff) {
                a {
                    style =
                        kw.inline {
                            font.sm
                        }
                    href = "/add"
                    +"Add a book"
                }
            }

            div {
                classes = setOf("container")
                id = "book-list"
                style =
                    kw.inline {
                        flex
                        flex.row
                        flex.wrap
                    }

                books.forEach { book, author ->
                    div(classes = "book-${book.id}") {
                        style =
                            kw.inline {
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
                            style =
                                kw.inline {
                                    font.sm
                                }
                            href = "/book/${book.id}"
                            +"See More\n"
                        }
                        if (isStaff) {
                            a {
                                style =
                                    kw.inline {
                                        font.sm
                                    }

                                attributes.hx {
                                    get = "/delete/${book.id}"
                                    swap = "delete"
                                    target = ".book-${book.id}"
                                }
                                href = "#"
                                +"Delete book"
                            }
                        }
                    }
                }
            }
        }
    }
}
