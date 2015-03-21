package io.vertx.kotlin.lang

import io.vertx.core.*
import io.vertx.core.impl.VertxFactoryImpl
import io.vertx.core.impl.VertxImpl
import io.vertx.core.json
import io.vertx.kotlin.lang.json.Json
import io.vertx.kotlin.lang.json.array_
import io.vertx.kotlin.lang.json.object_
import kotlinx.util.with

public inline val AbstractVerticle.config : json.JsonObject
    get() = config()

public fun Verticle.verticle(name : String, options : DeploymentOptions = DeploymentOptions(), handler : (AsyncResult<String>) -> Unit) {
    getVertx().deployVerticle(name, options) { result -> handler(result.toAsyncResultK())}
}

public inline fun DefaultVertx(block : Vertx.() -> Unit) : Unit {
    VertxFactoryImpl().vertx().with(block)
}