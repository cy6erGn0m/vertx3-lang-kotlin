package io.vertx.kotlin.lang

import io.vertx.core.*
import io.vertx.core.json.JsonObject as vJsonObject
import kotlinx.util.with

public val AbstractVerticle.config: vJsonObject
    get() = config()

public fun Verticle.verticle(name: String, options: DeploymentOptions = DeploymentOptions(), handler: (AsyncResult<String>) -> Unit) {
    vertx.deployVerticle(name, options) { result -> handler(result.toAsyncResultK()) }
}

public inline fun DefaultVertx(options: VertxOptions = VertxOptions(), block: Vertx.() -> Unit): Unit {
    Vertx.vertx(options).with(block)
}

fun VertxOptions(block: VertxOptions.() -> Unit): VertxOptions = VertxOptions().with(block)
