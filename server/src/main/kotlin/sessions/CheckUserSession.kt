package uk.co.almasjid.server.authentication.sessions

import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respondRedirect
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions

suspend fun ApplicationCall.checkUserSession(
    check: ((UserSession) -> Boolean)? = null,
    redirectRoute: String = "/unauthorised",
): Boolean {
    val session = sessions.get<UserSession>()

    if (session == null || (check != null && !check(session))) {
        respondRedirect(redirectRoute)
        return false
    }
    return true
}
