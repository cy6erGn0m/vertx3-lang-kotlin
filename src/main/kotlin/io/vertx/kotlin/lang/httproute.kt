package io.vertx.kotlin.lang

import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import kotlin.InlineOption.ONLY_LOCAL_RETURN

public class Route(request: HttpServerRequest,
                   response: HttpServerResponse) {
    public val request: HttpServerRequest = request
    public val response: HttpServerResponse = response
    public var completed: Boolean = false
}

public inline fun Route(inlineOptions(ONLY_LOCAL_RETURN) block: Route.() -> Unit): HttpServerResponse.(HttpServerRequest) -> Unit = {
    Route(it, this).block()
}

public inline fun Route.GET(path: String, virtualHost: String? = null, block: HttpServerResponse.(HttpServerRequest) -> Unit) {
    if (!completed && request.path().startsWith(path) && (virtualHost == null || virtualHost == request.getHeader("Host"))) {
        // TODO better virtual host checking
        response.block(request)
        completed = true
    }
}

public inline fun Route.otherwise(block: HttpServerResponse.(HttpServerRequest) -> Unit) {
    if (!completed) {
        response.block(request)
        completed = true
    }
}
