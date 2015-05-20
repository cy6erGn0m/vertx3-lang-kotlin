package io.vertx.kotlin.lang

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.http.HttpHeaders
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import java.io.File
import java.util.ArrayList
import java.util.Date

private fun Long.toHexString() = java.lang.Long.toHexString(this)

@suppress("NOTHING_TO_INLINE")
public inline fun HttpServerResponse.serveDirectory(request: HttpServerRequest, dir: File) {
    contentType("text/html")
    body {
        write("<!DOCTYPE html>\n<html>\n<head>\n\t<title>${request.path()} directory listing</title>\n")
        write("""
        <style type="text/css">
            p {
                margin: 3px 15px
            }
        </style>
        </head><body>
        """)

        write("<h1>Directory listing ${request.path()}</h1>\n")

        dir.listFiles()?.forEach {
            writeBuffer {
                appendString("<p>")

                appendString(when {
                    it.isDirectory() -> "[D]"
                    it.isFile() -> "[F]"
                    else -> "[?]"
                })

                appendString(" <a href=\"")
                appendString(request.path())
                if (!request.path().endsWith("/")) {
                    appendString("/")
                }
                appendString(it.getName()) // TODO better resolve
                appendString("\">")
                appendString(it.getName())
                appendString("</a>")

                appendString("</p>\n")
            }
        }

        write("</body></html>")
    }
}

private fun File.parents(): List<File> {
    val parents = ArrayList<File>()
    var current = this

    do {
        val parent = current.parent
        if (parent == null || parent == current) {
            break
        }

        parents.add(parent)
    } while (true)

    return parents
}

public fun HttpServerResponse.serve(request: HttpServerRequest, f: File, mostTop: File, directoryListingEnabled: Boolean = true) {
    setChunked(false)

    if (request.method() !in listOf(HttpMethod.GET, HttpMethod.HEAD)) {
        setStatus(HttpResponseStatus.METHOD_NOT_ALLOWED, "Method ${request.method()} not allowed")
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

    if ((f.isFile() && !f.canRead()) || mostTop !in f.parents()) {
        setStatus(HttpResponseStatus.FORBIDDEN, "Access denied")
        body {
            write("<html><body><h1>The requested file couldn't be read</h1></body></html>")
        }
        return
    }

    if (f.isDirectory()) {
        if (directoryListingEnabled) {
            serveDirectory(request, f)
        } else {
            setStatus(HttpResponseStatus.FORBIDDEN, "Access denied")
            body {
                write("<html><body><h1>Directory listing is disabled</h1></body></html>")
            }
        }
        return
    }

    val etag = "E/${f.length().toHexString()}/${f.lastModified().toHexString()}"
    val ifMatchEtags = request.headers().getAll(HttpHeaders.IF_MATCH).flatMap { it.split("\\s*,\\s*".toRegex()).toList() }
    if (ifMatchEtags.isNotEmpty()) {
        if (etag !in ifMatchEtags && "*" !in ifMatchEtags) {
            setStatusCode(HttpResponseStatus.NOT_MODIFIED)
            end()
            return // TODO If-Match need testing
        }
    }

    val ifNotMatchEtags = request.headers().getAll(HttpHeaders.IF_NONE_MATCH).flatMap { it.split("\\s*,\\s*".toRegex()).toList() }
    if (ifNotMatchEtags.isNotEmpty()) {
        if (etag in ifNotMatchEtags || "*" in ifNotMatchEtags) {
            setStatusCode(HttpResponseStatus.NOT_MODIFIED)
            end()
            return
        }
    }

    val modifiedSinceDates = request.headers().getAll(HttpHeaders.IF_MODIFIED_SINCE).map { dateFormatLocal.get().parse(it) }
    modifiedSinceDates.min()?.let { modifiedSince ->
        val lastModified = dateFormatLocal.get().parse(dateFormatLocal.get().format(Date(f.lastModified())))

        if (modifiedSince >= lastModified) {
            setStatusCode(HttpResponseStatus.NOT_MODIFIED)
            end()
            return@serve
        }
    }

    header(HttpHeaders.LAST_MODIFIED, Date(f.lastModified()))
    header(HttpHeaders.ETAG, etag)

    if (request.method() == HttpMethod.GET) {
        sendFile(f.getAbsolutePath())
    } else {
        end()
    }
}