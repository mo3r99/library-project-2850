package leeds.compsci

import io.ktor.server.application.Application
import io.ktor.server.html.respondHtml
import io.ktor.server.http.content.staticResources
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.html.head
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.title
import kotlinx.html.ul
import kotlinx.html.td
import kotlinx.html.tr
import kotlinx.html.table
import kotlinx.html.li
import common.csvDataToDb
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.io.read

fun Application.configureRouting() {
    routing {
        staticResources("/", "/web")

        get("/") {
            val dir = "/Users/muhammadrauf/Documents/Dev Projects/library-project-2850/server/src/"
            val bookData = DataFrame.read("${dir}main/resources/booklist.csv")
            call.respondHtml {
                head {
                    title("Library")
                }

                body {
                    h1 { +"Welcome to the Library" }
                    ul { bookData.schema().columns.forEach { li { +"${it.key}" } } }
                    table {
                        val data = csvDataToDb()

                        tr {
                            td { +"Author" }
                        }

                        data.forEach {
                            tr { td { +"${it.toString()}" } }
                        }
                    }
                }
            }
        }
    }
}
