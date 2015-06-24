package core.http.https

import io.vertx.core.Vertx
import io.vertx.core.net.JksOptions
import io.vertx.kotlin.lang.HttpServerOptions
import io.vertx.kotlin.lang.contentType
import io.vertx.kotlin.lang.httpServerWithOptions
import io.vertx.kotlin.lang.replyText


fun server(vertx: Vertx) {
    val options = HttpServerOptions(4443)
            .setSsl(true)
            .setKeyStoreOptions(
                    JksOptions()
                        .setPath("server-keystore.jks")
                        .setPassword("wibble")
            )

    vertx.httpServerWithOptions(options) { request ->
        contentType("text/html", "utf-8")
        replyText("<html><body><h1>Hello from vert.x!</h1></body></html>")
    }
}
