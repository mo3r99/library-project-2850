package leeds.compsci

import common.configureDatabases
import io.ktor.server.application.Application
import common.configureErrorHandling

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

suspend fun Application.module() {
    configureDatabases()
    configureErrorHandling()
    configureRouting()
}
