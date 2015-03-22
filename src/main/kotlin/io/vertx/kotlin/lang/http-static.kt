package io.vertx.kotlin.lang

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.http.HttpHeaders
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import java.io.File
import java.lang.ref.ReferenceQueue
import java.util.Date

private fun Long.toHexString() = java.lang.Long.toHexString(this)

public fun HttpServerResponse.serve(request : HttpServerRequest, f : File, mostTop : File? = null) {
    setChunked(false)

    if (request.method() !in listOf(HttpMethod.GET, HttpMethod.HEAD)) {
        setStatus(HttpResponseStatus.METHOD_NOT_ALLOWED.code(), "Method ${request.method()} not allowed")
        body {
            write("<html><body><h1>The requested method ${request.method()} is not allowed</h1></body></html>")
        }
        return
    }

    if (!f.exists()) {
        setStatus(404, "File not found")
        body {
            write("<html><body><h1>The requested file doesn't exist</h1></body></html>")
        }
        return
    }

    if (!f.canRead() || (mostTop != null && !f.isDescendant(mostTop))) {
        setStatus(HttpResponseStatus.FORBIDDEN.code(), "Access denied")
        body {
            write("<html><body><h1>The requested file couldn't be read</h1></body></html>")
        }
        return
    }

    val etag = "E/${f.length().toHexString()}/${f.lastModified().toHexString()}"
    val ifMatchEtags = request.headers().getAll(HttpHeaders.IF_MATCH).flatMap { it.split("\\s*,\\s*").toList() }
    if (ifMatchEtags.isNotEmpty()) {
        if (etag !in ifMatchEtags && "*" !in ifMatchEtags) {
            setStatusCode(HttpResponseStatus.NOT_MODIFIED.code())
            end()
            return // TODO If-Match need testing
        }
    }

    val ifNotMatchEtags = request.headers().getAll(HttpHeaders.IF_NONE_MATCH).flatMap { it.split("\\s*,\\s*").toList() }
    if (ifNotMatchEtags.isNotEmpty()) {
        if (etag in ifNotMatchEtags || "*" in ifNotMatchEtags) {
            setStatusCode(HttpResponseStatus.NOT_MODIFIED.code())
            end()
            return
        }
    }

    val modifiedSinceDates = request.headers().getAll(HttpHeaders.IF_MODIFIED_SINCE).map { dateFormatLocal.get().parse(it) }
    modifiedSinceDates.min()?.let { modifiedSince ->
        val lastModified = dateFormatLocal.get().parse(dateFormatLocal.get().format(Date(f.lastModified())))

        if (modifiedSince >= lastModified) {
            setStatusCode(HttpResponseStatus.NOT_MODIFIED.code())
            end()
            return@serve
        }
    }

    header(HttpHeaders.LAST_MODIFIED.toString(), Date(f.lastModified()))
    header(HttpHeaders.ETAG.toString(), etag)

    if (request.method() == HttpMethod.GET) {
        sendFile(f.getAbsolutePath())
    } else {
        end()
    }
}