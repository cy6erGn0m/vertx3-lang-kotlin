package core.eventbus.core.http.sendfile

import io.vertx.core.Vertx
import io.vertx.kotlin.lang.*
import java.io.File

fun server(vertx: Vertx) {
    vertx.httpServer(8080, block = Route {
        GET_g(glob("/*.html")) { request ->
            val file = File(request.path().substringAfterLast("/", request.path()))

            if (file.exists()) {
                contentType("text/html")
                sendFile(file.getPath())
            } else {
                setStatusCode(404)
                end()
            }
        }
    })
}
