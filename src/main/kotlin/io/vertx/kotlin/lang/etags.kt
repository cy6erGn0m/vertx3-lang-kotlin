package io.vertx.kotlin.lang.http

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import io.vertx.kotlin.lang.*

public fun HttpServerRequest.withEtagCustom(mapper: (Any?) -> String, vararg items: Any?, block: HttpServerResponse.() -> Unit) {
    val response = response()

    val givenNoneMatchEtags = headers().getAll("If-None-Match").flatMap(String::parseMatchTag).toSet()
    val givenMatchEtags = headers().getAll("If-Match").flatMap(String::parseMatchTag).toSet()
    val currentEtag = items.map(mapper).joinToString("")

    if (currentEtag in givenNoneMatchEtags && "*" !in givenNoneMatchEtags) {
        response.setStatus(HttpResponseStatus.NOT_MODIFIED, "Not modified for etag")
        response.end()
        return
    }
    if (givenMatchEtags.isNotEmpty() && currentEtag !in givenMatchEtags && "*" !in givenMatchEtags) {
        response.setStatus(HttpResponseStatus.PRECONDITION_FAILED, "Got etag $currentEtag")
        response.end()
        return
    }

    response.putHeader("ETag", currentEtag)

    response.block()
}

public fun HttpServerRequest.withHashEtag(vararg items: Any?, block: HttpServerResponse.() -> Unit): Unit =
    withEtagCustom({ it.etag().toHexString() }, *items, block = block)

public fun HttpServerRequest.withStringifiedEtag(vararg items: Any?, block: HttpServerResponse.() -> Unit): Unit =
    withEtagCustom({ it?.toStringEx() ?: "null" }, *items, block = block)

internal fun Int.toHexString() = Integer.toHexString(this).padStart(8, '0')
private fun Any?.etag() = this?.hashCodeEx() ?: 0
private fun String.parseMatchTag() = split("\\s*,\\s*".toRegex()).map { it.removePrefix("W/") }.filter { it.isNotEmpty() }
