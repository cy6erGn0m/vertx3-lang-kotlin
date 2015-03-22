package io.vertx.kotlin.lang

import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern
import kotlin.InlineOption.ONLY_LOCAL_RETURN

public val patternCache : MutableMap<String, Pattern> = ConcurrentHashMap()
public class Route(request: HttpServerRequest,
                   response: HttpServerResponse
                   ) {
    public val request: HttpServerRequest = request
    public val response: HttpServerResponse = response
    public var completed: Boolean = false
}

public inline fun Route(inlineOptions(ONLY_LOCAL_RETURN) block: Route.() -> Unit): HttpServerResponse.(HttpServerRequest) -> Unit = {
    Route(it, this).block()
}

public inline fun Route.handle(block: HttpServerResponse.(HttpServerRequest) -> Unit, predicate : (HttpServerRequest) -> Boolean) {
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

public inline fun Route.GET_g(globs: List<Pattern>, block: HttpServerResponse.(HttpServerRequest) -> Unit) {
    handle(block) { request ->
        request.method() == HttpMethod.GET && globs.any {it.matcher(request.path()).find()}
    }
}

public inline fun Route.otherwise(block: HttpServerResponse.(HttpServerRequest) -> Unit) {
    handle(block) {true}
}


private val globPattern = Pattern.compile("""\?|\*+|(\[[^\]]+\])""")
private fun String.smartQuote() = if (isEmpty()) "" else Pattern.quote(this)
public fun String.globToPattern() : Pattern = globPattern.matcher(this).let { m ->
    Pattern.compile(StringBuilder {
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

        if (idx < this@globToPattern.length()) {
            append(this@globToPattern.substring(idx).smartQuote())
        }

        append("$")
    }.toString())
}

[suppress("NOTHING_TO_INLINE")]
public inline fun Route.glob(vararg globs : String) : List<Pattern> =
    globs.map { glob -> patternCache.getOrPut(glob) {glob.globToPattern()} }

[suppress("NOTHING_TO_INLINE")]
public fun Route.serve(path : String, fileSystemPath : File) {
    handle({
        serve(request, File(fileSystemPath, it.path().substring(path.length())), fileSystemPath)
    }, {it.path().startsWith(path) && it.method() in listOf(HttpMethod.GET, HttpMethod.HEAD)})
}