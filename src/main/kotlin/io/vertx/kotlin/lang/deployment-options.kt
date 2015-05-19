package io.vertx.kotlin.lang

import io.vertx.core.DeploymentOptions
import io.vertx.core.Handler
import io.vertx.core.Verticle
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.lang.json.Json

public inline fun DeploymentOptions.withConfig(body: Json.() -> JsonObject): DeploymentOptions =
        with(this) { setConfig(Json().body()) }

public fun Vertx.deploy(verticle: Verticle, handler: (AsyncResult<String>) -> Unit) {
    deployVerticle(verticle, Handler { handler(it.toAsyncResultK()) })
}

public fun Vertx.deployVerticle(name: String, handler: (AsyncResult<String>) -> Unit) {
    deployVerticle(name, Handler { handler(it.toAsyncResultK()) })
}

public fun Vertx.undeploy(deploymentID: String, handler: (AsyncResult<Void>) -> Unit) {
    undeploy(deploymentID, Handler { handler(it.toAsyncResultK()) })
}