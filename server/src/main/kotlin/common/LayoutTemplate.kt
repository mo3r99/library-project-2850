package common

import io.github.allangomes.kotlinwind.css.kw
import io.ktor.server.html.Placeholder
import io.ktor.server.html.Template
import io.ktor.server.html.insert
import kotlinx.html.FlowContent
import kotlinx.html.HTML
import kotlinx.html.ScriptType
import kotlinx.html.TITLE
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.h4
import kotlinx.html.head
import kotlinx.html.link
import kotlinx.html.main
import kotlinx.html.meta
import kotlinx.html.nav
import kotlinx.html.script
import kotlinx.html.style
import kotlinx.html.title

class LayoutTemplate(
    val loggedIn: Boolean,
) : Template<HTML> {
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
                nav {
                    style =
                        kw.inline {
                            flex.row.justify_between
                        }
                    h4 { +"2850 Library" }

                    if (!loggedIn) {
                        a {
                            href = "/login"
                            +"Log in"
                        }
                    }
                }
                insert(content)
            }
        }
    }
}
