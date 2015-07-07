package io.vertx.kotlin.lang.http

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import io.vertx.kotlin.lang.setStatus

fun HttpServerRequest.withEtagCustom(mapper: (Any?) -> String, vararg items: Any?, block: HttpServerResponse.() -> Unit) {
    val response = response()

    val givenNoneMatchEtags = headers().getAll("If-None-Match").flatMap(String::parseMatchTag).toSet()
    val givenMatchEtags = headers().getAll("If-Match").flatMap(String::parseMatchTag).toSet()
    val currentEtag = items.map(mapper).joinToString("")

    if (currentEtag in givenNoneMatchEtags && "*" !in givenNoneMatchEtags) {
        response.setStatus(HttpResponseStatus.NOT_MODIFIED, "Not modified for etag")
        return
    }
    if (givenMatchEtags.isNotEmpty() && currentEtag !in givenMatchEtags && "*" !in givenMatchEtags) {
        response.setStatus(HttpResponseStatus.PRECONDITION_FAILED, "Got etag ${currentEtag}")
        return
    }

    response.putHeader("ETag", currentEtag)

    response.block()
}

fun HttpServerRequest.withHashEtag(vararg items: Any?, block: HttpServerResponse.() -> Unit) =
    withEtagCustom({ it.etag().toHexString() }, *items, block = block)

fun HttpServerRequest.withStringifiedEtag(vararg items: Any?, block: HttpServerResponse.() -> Unit) =
    withEtagCustom({ it.toString() }, *items, block = block)

private fun Int.toHexString() = Integer.toHexString(this).padStart(8, '0')
private fun Any?.etag() = this?.hashCode() ?: 0
private fun String.parseMatchTag() = split("\\s*,\\s*".toRegex()).map { it.removePrefix("W/") }.filter { it.isNotEmpty() }
