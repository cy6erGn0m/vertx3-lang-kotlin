package core.eventbus.core.http.https

import io.vertx.core.Vertx
import io.vertx.core.http.HttpClientOptions

fun client(vertx: Vertx) {
    // Note! in real-life you wouldn't often set trust all to true as it could leave you open to man in the middle attacks.

    val options = HttpClientOptions()
            .setSsl(true)
            .setTrustAll(true)

    vertx.createHttpClient(options).getNow(4443, "localhost", "/") { response ->
        println("Got response with status ${response.statusCode()}")

        response.bodyHandler { bytes ->
            System.out.write(bytes.getBytes())
        }
    }
}