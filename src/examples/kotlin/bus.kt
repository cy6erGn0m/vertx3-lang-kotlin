package io.vertx.kotlin.lang

import io.vertx.core.Handler
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.EventBus
import io.vertx.core.eventbus.Message

public fun <T> EventBus.send(address: String, message: Any, options: DeliveryOptions = DeliveryOptions(), replyHandler: (AsyncResult<Message<T>>) -> Unit): EventBus {
    return send<T>(address, message, options, Handler { e -> replyHandler(e.toAsyncResultK()) })
}

