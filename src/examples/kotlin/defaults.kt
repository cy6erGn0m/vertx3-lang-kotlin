package examples.defaults

import io.vertx.core.impl.VertxFactoryImpl
import io.vertx.core.spi.VertxFactory
import io.vertx.kotlin.lang.*
import io.vertx.kotlin.lang.json.object_

fun main(args: Array<String>) {
    DefaultVertx {
        httpServer(8084) { request ->
            setChunked(true)

            replyJson {
                object_(
                        "title" to "Hello, my remote peer",
                        "message" to "You address is ${request.remoteAddress().host()}"
                )
            }

            end()
        }
    }
}