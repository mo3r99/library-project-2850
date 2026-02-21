package leeds.compsci.routes

import book.BookService
import common.LayoutTemplate
import copy.Status
import io.github.allangomes.kotlinwind.css.kw
import io.ktor.htmx.html.HxAttributes
import io.ktor.htmx.html.hx
import io.ktor.server.application.ApplicationCall
import io.ktor.server.html.respondHtmlTemplate
import kotlinx.html.a
import kotlinx.html.h1
import kotlinx.html.h3
import kotlinx.html.h4
import kotlinx.html.style
import kotlinx.html.table
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.tr
import io.ktor.htmx.html.hx
import io.ktor.utils.io.ExperimentalKtorApi
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.img
import kotlinx.html.span

@OptIn(ExperimentalKtorApi::class)
suspend fun ApplicationCall.bookDetailRoute() {
    val id = parameters["id"]?.toIntOrNull() ?: throw IllegalArgumentException("Invalid book ID")

    val bookService = BookService()
    val bookDetail = bookService.getBookById(id)

    respondHtmlTemplate(LayoutTemplate()) {
        titleText { bookDetail.first.title }

        content {
            a {
                href = "/"
                +"Go back home"
            }

            h1 {
                style = kw.inline {
                    margin.top[8]
                }

                +bookDetail.first.title }
            h4 { +"By ${bookDetail.second.name}" }

            div {
                classes = setOf("copies")
                h3 {
                    style = kw.inline {
                        margin.top[8]
                    }

                    +"Available copies:"
                }
                table {
                    style = kw.inline {
                        margin.top[4]
                    }
                    tr {
                        th {
                            style = kw.inline {
                                font.bold
                            }
                            +"Location Code"
                        }
                        th {
                            style = kw.inline {
                                font.bold
                            }
                            +"Format Code"
                        }

                        th {
                            style = kw.inline {
                                font.bold
                            }
                            +"Notes"
                        }

                        th {
                            style = kw.inline {
                                font.bold
                            }
                            +"Reserve"
                        }
                    }

                    bookDetail.third.forEach { copy ->
                        tr {
                            td { +copy.locationCode }
                            td { +copy.formatCode }
                            td { +copy.notes }
                            td {
                                if (copy.status == Status.AVAILABLE) {
                                    a {
                                        classes = setOf("reserve-button-${copy.id}")
                                        attributes.hx {
                                            post = "/reserve/${bookDetail.first.id}/${copy.id}"
                                            target = ".copies"
                                            trigger = "click once"
                                        }
                                        href = "/reserve/${bookDetail.first.id}/${copy.id}"
                                        +"Reserve"

                                        img {
                                            classes = setOf("htmx-indicator")
                                            style = kw.inline { margin.left[4] } + "; filter: invert(1)"
                                            src = "/assets/loader.svg"
                                            width = "20px"
                                            height = "20px"
                                        }
                                    }
                                } else {
                                    span { +"Unavailable" }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}