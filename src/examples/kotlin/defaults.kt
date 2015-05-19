package examples.defaults

import examples.LoggerVerticle
import io.vertx.core.logging.impl.LoggerFactory
import io.vertx.kotlin.lang.*
import io.vertx.kotlin.lang.json.object_

fun main(args: Array<String>) {
    DefaultVertx {
        deploy(LoggerVerticle()) {
            when (it) {
                is AsyncSuccessResult -> LoggerFactory.getLogger("temp").info("Deployed verticle ${it.result}")
                is AsyncErrorResult -> LoggerFactory.getLogger("temp").error("Failed to deploy verticle", it.error)
            }
        }

        httpServer(8084) { request ->
            bodyJson {
                object_(
                        "title" to "Hello, my remote peer",
                        "message" to "You address is ${request.remoteAddress().host()}"
                )
            }
        }
    }
}