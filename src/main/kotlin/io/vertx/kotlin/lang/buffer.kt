package io.vertx.kotlin.lang

import io.vertx.core.buffer.Buffer
import io.vertx.core.json.impl
import io.vertx.kotlin.lang.json.Json
import kotlinx.util.with

public inline fun Buffer(initialSize : Int = 8192, block : Buffer.() -> Unit) : Buffer = Buffer.buffer(initialSize).with {block()}
public fun Buffer.appendCrLf() : Buffer = with {appendString("\r\n")}
public fun Buffer.appendLf() : Buffer = with {appendString("\n")}

public inline fun Buffer.appendJson(block : Json.() -> Any) : Buffer =
        Json().block().let { json -> impl.Json.encode(json) }.let { encoded -> appendString(encoded) }