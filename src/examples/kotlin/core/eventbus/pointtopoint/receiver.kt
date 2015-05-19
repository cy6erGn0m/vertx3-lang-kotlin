package core.eventbus.pointtopoint

import io.vertx.core.Vertx

fun receiver(vertx: Vertx) {
    val eb = vertx.eventBus()

    eb.consumer<String>("ping-address") { message ->
        println("Received message: ${message.body()}")
        message.reply("pong")
    }

    println("receiver ready")
}


