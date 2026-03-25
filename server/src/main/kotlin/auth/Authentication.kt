package auth

import io.ktor.server.application.Application
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authentication
import io.ktor.server.auth.form
import io.ktor.server.auth.session
import io.ktor.server.html.respondHtmlFragment
import io.ktor.server.response.respondRedirect
import uk.co.almasjid.server.authentication.sessions.UserSession
import user.UserRepository

fun Application.configureAuthentication() {
    val userRepository = UserRepository()

    authentication {
        form(name = "auth-form") {
            userParamName = "username"
            passwordParamName = "password"

            validate { credentials ->
                when (userRepository.checkCredentials(credentials)) {
                    true -> UserIdPrincipal(credentials.name)
                    else -> null
                }
            }

            challenge {
                call.respondHtmlFragment {
                    +"Invalid username or password\n"
                }
            }
        }

        session<UserSession>("auth-session") {
            validate { session ->
                UserIdPrincipal(session.username)
            }
            challenge {
                call.respondRedirect("/login")
            }
        }
    }
}
