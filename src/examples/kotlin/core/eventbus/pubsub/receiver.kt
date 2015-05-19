package core.eventbus.pubsub

import io.vertx.core.Vertx

fun receiver(vertx: Vertx) {
    vertx.eventBus().consumer<String>("news-feed") { message ->
        println("Received news: ${message.body()}")
    }

    println("Receiver ready")
}
