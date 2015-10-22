package core.http.sendfile

import io.vertx.core.Vertx
import io.vertx.kotlin.lang.*
import java.io.File

fun server(vertx: Vertx) {
    vertx.httpServer(8080, block = Route {
        GET(glob("/*.html")) { request ->
            val file = File(request.path().substringAfterLast("/", request.path()))

            if (file.exists()) {
                contentType("text/html")
                sendFile(file.path)
            } else {
                setStatusCode(404)
                end()
            }
        }
    })
}
