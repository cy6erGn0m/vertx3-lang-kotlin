package io.vertx.kotlin.lang

import io.vertx.core.DeploymentOptions
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.lang.json.Json
import kotlinx.util.with

public inline fun DeploymentOptions.withConfig(body: Json.() -> JsonObject): DeploymentOptions =
        with { setConfig(Json().body()) }