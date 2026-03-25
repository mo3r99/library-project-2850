package leeds.compsci

import auth.configureAuthentication
import common.configureDatabases
import common.configureErrorHandling
import io.ktor.server.application.Application
import uk.co.almasjid.server.authentication.sessions.configureSessions

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain
        .main(args)
}

suspend fun Application.module() {
    configureDatabases()
    configureErrorHandling()
    configureAuthentication()
    configureSessions()
    configureRouting()
}
