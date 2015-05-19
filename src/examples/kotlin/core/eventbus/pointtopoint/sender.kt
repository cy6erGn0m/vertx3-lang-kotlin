package core.eventbus.pointtopoint

import io.vertx.kotlin.lang.*
import io.vertx.core.Vertx

fun sender(vertx: Vertx) {
    val bus = vertx.eventBus()

    vertx.setPeriodic(1000) {
        bus.send<String>("ping-address", "ping") { reply ->
            when (reply) {
                is AsyncSuccessResult -> println("Received reply: ${reply}")
                else -> println("No reply")
            }
        }
    }
}