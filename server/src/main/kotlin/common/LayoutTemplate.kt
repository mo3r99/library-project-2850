package common

import io.ktor.server.html.Placeholder
import io.ktor.server.html.Template
import io.ktor.server.html.insert
import kotlinx.html.HTML
import kotlinx.html.TITLE
import kotlinx.html.FlowContent
import kotlinx.html.head
import kotlinx.html.meta
import kotlinx.html.link
import kotlinx.html.script
import kotlinx.html.ScriptType
import kotlinx.html.title
import kotlinx.html.body
import kotlinx.html.main
class LayoutTemplate : Template<HTML> {
    val titleText = Placeholder<TITLE>()
    val content = Placeholder<FlowContent>()

    override fun HTML.apply() {
        head {
            meta(charset = "utf-8")
            meta(name = "viewport", content = "width=device-width, initial-scale=1")
            meta(name = "color-scheme", content = "light dark")
            link(rel = "stylesheet", href = "https://cdn.jsdelivr.net/npm/@picocss/pico@2/css/pico.jade.min.css")
            script(src = "/htmx/htmx2_0_8.js", type = ScriptType.textJavaScript) {}

            title {
                insert(titleText)
            }
        }
        body {
            main(classes = "container") {
                insert(content)
            }
        }
    }
}
