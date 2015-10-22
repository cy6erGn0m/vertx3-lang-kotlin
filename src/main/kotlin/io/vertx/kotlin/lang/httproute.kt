package io.vertx.kotlin.lang

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.http.ServerWebSocket
import kotlinx.util.with
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern

public val patternCache: MutableMap<String, Pattern> = ConcurrentHashMap()

public class Route(request: HttpServerRequest,
                   response: HttpServerResponse
) {
    public val request: HttpServerRequest = request
    public val response: HttpServerResponse = response
    public var completed: Boolean = false
}

public inline fun Route(crossinline block: Route.() -> Unit): HttpServerResponse.(HttpServerRequest) -> Unit = {
    Route(it, this).with {
        block()
        if (!completed) {
            setStatus(HttpResponseStatus.SERVICE_UNAVAILABLE, "Route not configured")
            contentType("text/html")
            body {
                write("""<!DOCTYPE html>
                <html><head><title>Route not configured</title></head>
                <body>
                    <h1>Route not configured for ${it.path()}</h1>
                </body>
                </html>
                """)
            }
        }
    }
}

public inline fun Route.handle(block: HttpServerResponse.(HttpServerRequest) -> Unit, predicate: (HttpServerRequest) -> Boolean) {
    if (!completed && predicate(request)) {
        response.block(request)
        completed = true
    }
}

public inline fun Route.GET(path: String, block: HttpServerResponse.(HttpServerRequest) -> Unit) {
    handle(block) {
        it.method() == HttpMethod.GET && it.path() == path
    }
}

public inline fun Route.POST(path: String, block: HttpServerResponse.(HttpServerRequest) -> Unit) {
    handle(block) {
        it.method() == HttpMethod.POST && it.path() == path
    }
}

public inline fun Route.method(method: HttpMethod, path: String, block: HttpServerResponse.(HttpServerRequest) -> Unit) {
    handle(block) {
        it.method() == method && it.path() == path
    }
}

public inline fun Route.method_g(method: HttpMethod, globs: List<Pattern>, block: HttpServerResponse.(HttpServerRequest) -> Unit) {
    handle(block) { request ->
        request.method() == method && globs.any { it.matcher(request.path()).find() }
    }
}

public inline fun Route.GET(globs: List<Pattern>, block: HttpServerResponse.(HttpServerRequest) -> Unit) {
    handle(block) { request ->
        request.method() == HttpMethod.GET && globs.any { it.matcher(request.path()).find() }
    }
}

public inline fun Route.otherwise(block: HttpServerResponse.(HttpServerRequest) -> Unit) {
    handle(block) { true }
}


private val globPattern = Pattern.compile("""\?|\*+|(\[[^\]]+\])""")
private fun String.smartQuote() = if (isEmpty()) "" else Pattern.quote(this)
public fun String.globToPattern(): Pattern = globPattern.matcher(this).let { m ->
    Pattern.compile(StringBuilder().apply {
        if (this@globToPattern.startsWith("/")) {
            append("^")
        }

        var idx = 0
        while (m.find()) {
            append(this@globToPattern.substring(idx, m.start()).smartQuote())

            m.group().let { glob ->
                when {
                    glob == "*" -> append("""[^\\\/]*""")
                    glob == "**" -> append(".*")
                    glob == "?" -> append(".")
                    glob.startsWith("[!") && glob.endsWith("]") -> append(glob.replace("[!", "[^"))
                    glob.startsWith("[") && glob.endsWith("]") -> append(glob)
                    else -> append(glob.smartQuote())
                }
            }

            idx = m.end()
        }

        if (idx < this@globToPattern.length) {
            append(this@globToPattern.substring(idx).smartQuote())
        }

        append("$")
    }.toString())
}

@Suppress("NOTHING_TO_INLINE")
public inline fun Route.glob(vararg globs: String): List<Pattern> =
        globs.map { glob -> patternCache.getOrPut(glob) { glob.globToPattern() } }

public fun String.cutPath(basePath: String): String = if (this.endsWith("/")) substring(basePath.length, length - 1) else substring(basePath.length)

@Suppress("NOTHING_TO_INLINE")
public fun Route.serve(path: String, fileSystemPath: File, directoryListingEnabled: Boolean = true) {
    handle({
        serve(request, File(fileSystemPath, it.path().cutPath(path)), fileSystemPath, directoryListingEnabled)
    }, { it.path().startsWith(path) && it.method() in listOf(HttpMethod.GET, HttpMethod.HEAD) })
}

public inline fun Route.webSocket(path: String, block: ServerWebSocket.() -> Unit) {
    handle({ request ->
        request.upgrade().block()
    }) {
        it.method() == HttpMethod.GET && it.path() == path
    }
}