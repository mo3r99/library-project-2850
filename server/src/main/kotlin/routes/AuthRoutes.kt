package routes

import common.LayoutTemplate
import io.ktor.htmx.html.hx
import io.ktor.server.application.ApplicationCall
import io.ktor.server.html.respondHtmlTemplate
import kotlinx.html.ButtonType
import kotlinx.html.InputType
import kotlinx.html.a
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.p

suspend fun ApplicationCall.registerRoute() {
    respondHtmlTemplate(LayoutTemplate(false)) {
        titleText { +"Register" }

        content {
            h1 { +"Register" }
            form {
                action = "/register"
                method = kotlinx.html.FormMethod.post

                attributes.hx {
                    post = "/register"
                    target = ".message"
                }

                label {
                    +"Username"
                }
                input {
                    type = InputType.text
                    name = "username"
                    placeholder = "Username"
                }

                label {
                    +"Password"
                }
                input {
                    type = InputType.password
                    name = "password"
                    placeholder = "Password"
                }

                button {
                    type = ButtonType.submit
                    +"Register"
                }

                p {
                    classes = setOf("message")
                }
            }
        }
    }
}

suspend fun ApplicationCall.loginRoute() {
    respondHtmlTemplate(LayoutTemplate(false)) {
        titleText { +"Log in" }

        content {
            h1 { +"Log in" }
            form {
                action = "/login"
                method = kotlinx.html.FormMethod.post

                attributes.hx {
                    post = "/login"
                    target = ".message"
                }

                label {
                    +"Username"
                }
                input {
                    type = InputType.text
                    name = "username"
                    placeholder = "Username"
                }

                label {
                    +"Password"
                }
                input {
                    type = InputType.password
                    name = "password"
                    placeholder = "Password"
                }

                button {
                    type = ButtonType.submit
                    +"Log in"
                }

                p {
                    classes = setOf("message")
                }
            }

            p {
                +"Don't have an account? "
                a {
                    href = "/register"
                    +"Create one."
                }
            }
        }
    }
}
