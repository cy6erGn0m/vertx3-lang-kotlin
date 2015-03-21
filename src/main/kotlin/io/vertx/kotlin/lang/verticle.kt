package io.vertx.kotlin.lang

import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Future
import io.vertx.core.Verticle
import io.vertx.core.json
import io.vertx.kotlin.lang.json.Json
import io.vertx.kotlin.lang.json.array_
import io.vertx.kotlin.lang.json.object_

public inline val AbstractVerticle.config : json.JsonObject
    get() = config()

public fun Verticle.verticle(name : String, options : DeploymentOptions = DeploymentOptions(), handler : (AsyncResult<String>) -> Unit) {
    getVertx().deployVerticle(name, options) { result -> handler(result.toAsyncResultK())}
}