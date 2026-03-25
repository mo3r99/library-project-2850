package leeds.compsci

import author.Author
import author.AuthorTable
import book.Book
import book.BookService
import common.LayoutTemplate
import io.ktor.htmx.html.hx
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.UserPasswordCredential
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.html.respondHtmlFragment
import io.ktor.server.html.respondHtmlTemplate
import io.ktor.server.http.content.staticResources
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import io.ktor.server.util.getOrFail
import kotlinx.html.ButtonType
import kotlinx.html.InputType
import kotlinx.html.a
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.p
import kotlinx.html.style
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import routes.bookDetailRoute
import routes.homeRoute
import routes.loginRoute
import routes.registerRoute
import routes.reserveRoute
import routes.searchRoute
import uk.co.almasjid.server.authentication.sessions.UserSession
import user.Role
import user.UserRepository

fun Application.configureRouting() {
    routing {
        staticResources("/", "content")

        get("/") { call.homeRoute() }
        get("/search") { call.searchRoute() }
        get("/book/{id}") { call.bookDetailRoute() }

        post("/reserve/{bookId}/{copyId}") { call.reserveRoute() }

        get("/register") { call.registerRoute() }
        get("/login") { call.loginRoute() }

        post("/register") { call.handleRegistration() }

        authenticate("auth-form") {
            post("/login") { call.handleLogin() }
        }

        authenticate("auth-session") {
            get("/delete/{bookId}") { call.deleteBook() }

            get("/edit/{bookId}") { call.editBook() }
            post("/edit") {
                val formData = call.receiveParameters()
                val bookName = formData["name"] ?: throw IllegalArgumentException("Book title required")
                val bookAuthor = formData["author"] ?: throw IllegalArgumentException("Author title required")
                val bookIsbn = formData["isbn"] ?: throw IllegalArgumentException("ISBN required")
                val bookId = formData["bookId"]?.toIntOrNull() ?: throw IllegalArgumentException("Book ID invalid or missing")

                suspendTransaction {
                    try {
                        val book = BookService().getBookById(bookId)
                        book.first.title = bookName
                        book.first.isbn = bookIsbn
                        call.respond(HttpStatusCode.OK, "Book saved!")
                    } catch (e: Error) {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }
            }

            get("/add") { call.addBook() }
            post("/add") {
                val formData = call.receiveParameters()
                val bookName = formData["name"] ?: throw IllegalArgumentException("Book title required")
                val bookAuthor = formData["author"] ?: throw IllegalArgumentException("Author title required")
                val bookIsbn = formData["isbn"] ?: throw IllegalArgumentException("ISBN required")

                suspendTransaction {
                    val existingAuthor =
                        Author
                            .find {
                                AuthorTable.name eq bookAuthor
                            }.firstOrNull()

                    if (existingAuthor == null) {
                        val newAuthor =
                            Author.new {
                                name = bookAuthor
                                bio = ""
                            }
                        Book.new {
                            title = bookName
                            author = newAuthor
                            isbn = bookIsbn
                        }

                        call.respond(HttpStatusCode.OK, "Book created, with a new author ${newAuthor.name}")
                    } else {
                        Book.new {
                            title = bookName
                            author = existingAuthor
                            isbn = bookIsbn
                        }
                        call.respond(HttpStatusCode.OK, "Book created, with existing author ${existingAuthor.name}")
                    }
                }
            }
        }
    }
}

private suspend fun ApplicationCall.handleLogin() {
    val userRepository = UserRepository()
    val result =
        runCatching {
            val username =
                principal<UserIdPrincipal>()?.name ?: throw IllegalStateException("Something went wrong: cannot get UserIdPrincipal")
            val role = userRepository.getUserRole(username)

            sessions.set(UserSession(username, role))
        }

    if (result.isSuccess) {
        response.header("Hx-Redirect", "/")
    } else {
        respondHtmlFragment {
            +"${result.exceptionOrNull()?.message ?: "Something went wrong"}"
        }
    }
}

private suspend fun ApplicationCall.handleRegistration() {
    val credentials = getCredentials()
    val userRepository = UserRepository()
    val result =
        runCatching {
            userRepository.addUser(credentials, Role.STAFF)
        }

    println("Created new user! ${credentials.name}, ${credentials.password}, ${result.isSuccess}")

    if (result.isSuccess) {
        response.header("Hx-Redirect", "/")
    } else {
        respondHtmlFragment {
            +"${result.exceptionOrNull()?.message ?: "Error: Something went wrong"}"

            if (result.exceptionOrNull()?.message == "Username already exists") {
                div(classes = "mt-4") {
                    a(href = "/login", classes = "text-blue-500") {
                        +"Login instead?"
                    }
                }
            }
        }
    }
}

private suspend fun ApplicationCall.getCredentials(): UserPasswordCredential {
    val formParams = receiveParameters()
    val username = formParams.getOrFail<String>("username")
    val password = formParams.getOrFail<String>("password")

    return UserPasswordCredential(username, password)
}

suspend fun ApplicationCall.deleteBook() {
    val id = parameters["bookId"]?.toIntOrNull() ?: throw IllegalArgumentException("Book ID Required")

    val isStaff = sessions.get<UserSession>()?.role == Role.STAFF
    if (!isStaff) {
        respond(HttpStatusCode.Unauthorized)
        return
    }

    val bookService = BookService()

    try {
        val book = bookService.getBookById(id)
        val bookTitle = book.first.title
        suspendTransaction {
            book.third.forEach {
                it.delete()
            }
            book.first.delete()
        }

        respondHtmlTemplate(LayoutTemplate(true)) {
            titleText { +"Delete $bookTitle" }

            content {
                h1 {
                    +"${book.first.title} has been deleted, along with its copies."
                }
            }
        }
    } catch (err: Error) {
        respond(HttpStatusCode.NotFound)
    }
}

suspend fun ApplicationCall.addBook() {
    respondHtmlTemplate(LayoutTemplate(true)) {
        titleText { +"Add a new book" }
        content {
            h1 { +"Add a new book" }
            form {
                classes = setOf("form-book")
                action = "/add"
                method = kotlinx.html.FormMethod.post
                attributes.hx {
                    post = "/add"
                    target = ".form-message"
                    swap = "innerHtml"
                }

                label {
                    htmlFor = "name"
                    +"Book Name"
                }
                input {
                    type = InputType.text
                    name = "name"
                    placeholder = "E.g. Harry Potter"
                }

                label {
                    htmlFor = "author"
                    +"Author Name"
                }
                input {
                    type = InputType.text
                    name = "author"
                    placeholder = "E.g. J.K. Rowling"
                }

                label {
                    htmlFor = "isbn"
                    +"ISBN"
                }
                input {
                    type = InputType.text
                    name = "ISBN"
                    placeholder = "E.g. 978-0747532743"
                }

                button {
                    type = ButtonType.submit
                    +"Add book"
                }
                p {
                    classes = setOf("form-message")
                }
            }
        }
    }
}

suspend fun ApplicationCall.editBook() {
    val bookId = parameters["bookId"]?.toIntOrNull() ?: throw IllegalArgumentException("Invalid book ID")

    try {
        val book = BookService().getBookById(bookId)
        respondHtmlTemplate(LayoutTemplate(true)) {
            titleText { +"Edit ${book.first.title}" }
            content {
                h1 { +"Edit ${book.first.title}" }
                form {
                    action = "/edit"
                    method = kotlinx.html.FormMethod.post
                    attributes.hx {
                        post = "/edit"
                        target = ".form-message"
                        swap = "innerHtml"
                    }

                    label {
                        htmlFor = "name"
                        +"Book Name"
                    }
                    input {
                        type = InputType.text
                        name = "name"
                        value = book.first.title
                    }

                    label {
                        htmlFor = "author"
                        +"Author Name"
                    }
                    input {
                        type = InputType.text
                        name = "author"
                        value = book.second.name
                    }

                    label {
                        htmlFor = "isbn"
                        +"ISBN"
                    }
                    input {
                        type = InputType.text
                        name = "ISBN"
                        value = book.first.isbn
                    }

                    input {
                        type = InputType.text
                        name = "bookId"
                        value = book.first.id.toString()
                        style = "display: none; opacity: 0;"
                    }
                    button {
                        type = ButtonType.submit
                        +"Save Book"
                    }
                    p {
                        classes = setOf("form-message")
                    }
                }
            }
        }
    } catch (error: Error) {
        respond(HttpStatusCode.NotFound)
    }
}
