package examples.route

import io.vertx.kotlin.lang.*

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
                            <title>${request.path()}</title>
                        </head>
                        <body>
                            <h1>Dynamic page</h1>
                        </body>
                    </html>
                    """)
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