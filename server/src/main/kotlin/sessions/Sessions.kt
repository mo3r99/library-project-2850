package uk.co.almasjid.server.authentication.sessions

import io.ktor.server.application.*
import io.ktor.server.sessions.*
import kotlinx.serialization.Serializable
import user.Role

@Serializable
data class UserSession(
    val username: String,
    val role: Role,
)

fun Application.configureSessions() {
    install(Sessions) {
        cookie<UserSession>("user_session", SessionStorageMemory()) {
            cookie.path = "/"
            // cookie.maxAgeInSeconds = 30L * 24 * 60 * 60 // 30 Days in seconds
            // cookie.secure = true
            // cookie.httpOnly = true
        }
    }
}
