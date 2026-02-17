package leeds.compsci

import io.ktor.server.application.Application
import common.configureErrorHandling
import common.seedDatabases

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    seedDatabases()
    configureErrorHandling()
    configureRouting()
}
