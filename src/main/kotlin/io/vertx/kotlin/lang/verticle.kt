package io.vertx.kotlin.lang

import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Future
import io.vertx.core.Verticle
import io.vertx.core.json
import io.vertx.kotlin.lang.json.Json
import io.vertx.kotlin.lang.json.array_
import io.vertx.kotlin.lang.json.object_

class KVerticle : AbstractVerticle() {
    override fun start() {
        super.start()

        httpServer(9090, "0.0.0.0") {
            Buffer {
                appendString("Hello")
                appendLf()
                appendString("World!")
                appendLf()
                appendString(it.getParam("q"))
            }
        }

        httpServer(9091, "0.0.0.0") {
            Json {
                object_(
                        "a" to 1,
                        "b" to array_("x", "y", "z"),
                        "params" to object_(it.params().map { it.getKey() to it.getValue() })
                )
            }
        }

        httpServer(9092, "0.0.0.0") {
            it.startPumpTo(it.response())
        }

    }
}

class K2Verticle : AbstractVerticle() {
    override fun start(startFuture: Future<Void>) {
        verticle("yo") { r -> r.sendToFutureVoid(startFuture) }
    }
}

class K3Verticle : AbstractVerticle() {
    override fun start(startFuture: Future<Void>) {
        val deploymentOptions = DeploymentOptions().withConfig {
            object_(
                    "myOption" to "1"
            )
        }
        verticle("yo", deploymentOptions) { r -> r.sendToFutureVoid(startFuture) }
    }
}

public inline val AbstractVerticle.config : json.JsonObject
    get() = config()


public fun Verticle.verticle(name : String, options : DeploymentOptions = DeploymentOptions(), handler : (AsyncResult<String>) -> Unit) {
    getVertx().deployVerticle(name, options) { result -> handler(result.toAsyncResultK())}
}