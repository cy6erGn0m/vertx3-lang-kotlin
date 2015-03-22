package examples.route

import io.vertx.kotlin.lang.*
import kotlinx.util.with
import java.io.File

fun main(args: Array<String>) {
    DefaultVertx {
        httpServer(8084, "0.0.0.0", Route {
            GET("/path") { request ->
                bodyJson {
                    "field1" to "test me not"
                }
            }
            GET_g(glob("*.html", "*.htm")) { request ->
                contentType("text/html")
                body {
                    write("""<!doctype html>
                    <html>
                        <head>
                            <title>${"[^/]+$".toRegex().matcher(request.path()).with{find()}.group()}</title>
                        </head>
                        <body>
                            <h1>Dynamic page</h1>
                            <p>The requested path is ${request.path()}</p>
                        </body>
                    </html>
                    """)
                }
            }
            serve("/files", File("src/main/kotlin/io/vertx/kotlin/lang"))
            webSocket("/ws") {
                handler {
                    val text = it.toString("UTF-8")
                    if (text == "exit") {
                        close()
                    } else {
                        eventBus().send(textHandlerID(), text)
                    }
                }
            }

            otherwise {
                setStatus(404, "Resource not found")
                body {
                    write("The requested resource was not found\n")
                }
            }
        })
    }
}