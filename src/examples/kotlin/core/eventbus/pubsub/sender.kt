package core.eventbus.pubsub

import io.vertx.core.Vertx

fun sender(vertx: Vertx) {
    val bus = vertx.eventBus()

    vertx.setPeriodic(1000) {
        bus.publish("news-feed", "Some news!")
    }
}
