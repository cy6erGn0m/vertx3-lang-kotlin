package io.vertx.kotlin.lang

import io.vertx.core.buffer.Buffer
import io.vertx.core.json.impl
import io.vertx.kotlin.lang.json.Json
import kotlinx.util.with
import java.nio.charset.Charset


public fun bufferOf(s: String, charset: Charset = Charsets.UTF_8) : Buffer = Buffer.buffer(s, charset.name())
public fun bufferOf(bytes: ByteArray) : Buffer = Buffer.buffer(bytes)
public fun ByteArray.toBuffer() : Buffer = Buffer.buffer(this)

public inline fun Buffer(initialSize : Int = 8192, block : Buffer.() -> Unit) : Buffer = Buffer.buffer(initialSize).with {block()}
public fun Buffer.appendCrLf() : Buffer = appendString("\r\n")
public fun Buffer.appendLf() : Buffer = appendString("\n")

public inline fun Buffer.appendJson(block : Json.() -> Any) : Buffer =
        Json().block().let { json -> impl.Json.encode(json) }.let { encoded -> appendString(encoded) }