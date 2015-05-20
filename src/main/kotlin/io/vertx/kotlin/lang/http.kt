package io.vertx.kotlin.lang


import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core
import io.vertx.core.Handler
import io.vertx.core.Verticle
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.net.NetServerOptions
import io.vertx.kotlin.lang.json.Json
import kotlinx.util.with
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.InlineOption.ONLY_LOCAL_RETURN

public fun HttpServerOptions(port: Int, host: String = NetServerOptions.DEFAULT_HOST): HttpServerOptions = HttpServerOptions().setHost(host).setPort(port)

public inline fun Vertx.createHttpServerWithOptions(options: HttpServerOptions, inlineOptions(ONLY_LOCAL_RETURN) handler: HttpServerResponse.(HttpServerRequest) -> Unit): HttpServer =
        createHttpServer(options).with { requestHandler { request -> request.response().handler(request) } }

public inline fun Verticle.createHttpServerWithOptions(options: HttpServerOptions, inlineOptions(ONLY_LOCAL_RETURN) handler: HttpServerResponse.(HttpServerRequest) -> Unit): HttpServer =
        getVertx().createHttpServerWithOptions(options, handler)

public inline fun Vertx.createHttpServer(port: Int, host: String = NetServerOptions.DEFAULT_HOST, inlineOptions(ONLY_LOCAL_RETURN) handler: HttpServerResponse.(HttpServerRequest) -> Unit): HttpServer =
        createHttpServerWithOptions(HttpServerOptions(port, host), handler)

public inline fun Verticle.createHttpServer(port: Int, host: String = NetServerOptions.DEFAULT_HOST, inlineOptions(ONLY_LOCAL_RETURN) handler: HttpServerResponse.(HttpServerRequest) -> Unit): HttpServer =
        createHttpServerWithOptions(HttpServerOptions(port, host), handler)

public inline fun Vertx.httpServer(port: Int, host: String = NetServerOptions.DEFAULT_HOST, inlineOptions(ONLY_LOCAL_RETURN) block: HttpServerResponse.(HttpServerRequest) -> Unit): HttpServer =
        createHttpServer(port, host, block).listen()

public inline fun Verticle.httpServer(port: Int, host: String = NetServerOptions.DEFAULT_HOST, inlineOptions(ONLY_LOCAL_RETURN) block: HttpServerResponse.(HttpServerRequest) -> Unit): HttpServer =
        createHttpServer(port, host, block).listen()

public inline fun Vertx.httpServerWithOptions(options: HttpServerOptions, inlineOptions(ONLY_LOCAL_RETURN) block: HttpServerResponse.(HttpServerRequest) -> Unit): HttpServer =
        createHttpServerWithOptions(options, block).listen()

public inline fun Verticle.httpServerWithOptions(options: HttpServerOptions, inlineOptions(ONLY_LOCAL_RETURN) block: HttpServerResponse.(HttpServerRequest) -> Unit): HttpServer =
        createHttpServerWithOptions(options, block).listen()

public fun HttpServer.listen(listenHandler: (AsyncResult<HttpServer>) -> Unit): HttpServer = this.listen(object : Handler<core.AsyncResult<HttpServer>> {
    override fun handle(event: core.AsyncResult<HttpServer>?) {
        listenHandler(event?.toAsyncResultK() ?: AsyncErrorResult<HttpServer>(NullPointerException("event shouldn't be null")))
    }
})

public inline fun HttpServerResponse.replyBuffer(block: () -> Buffer) {
    write(block()).end()
}

public fun HttpServerResponse.replyText(text: String, charset: Charset = Charsets.UTF_8) {
    end(text, charset.name())
}

public inline fun HttpServerRequest.replyBuffer(block: () -> Buffer) {
    response().replyBuffer(block)
}

public inline fun HttpServerResponse.replyWithBuffer(block: Buffer.() -> Unit) {
    write(Buffer(8192, block)).end()
}

public inline fun HttpServerRequest.replyWithBuffer(block: Buffer.() -> Unit) {
    response().replyWithBuffer(block)
}

public inline fun HttpServerResponse.replyJson(block: Json.() -> Any) {
    return Buffer { appendJson(block) }.let { buffer -> this.write(buffer).end() }
}

public inline fun HttpServerRequest.replyJson(block: Json.() -> Any) {
    response().replyJson(block)
}

public fun HttpServerResponse.header(headerName: CharSequence, headerValue: String): HttpServerResponse {
    return putHeader(headerName, headerValue)
}

public fun HttpServerResponse.header(headerName: CharSequence, headerValue: Number): HttpServerResponse {
    return putHeader(headerName, headerValue.toString())
}

private val dateFormatLocal = object : ThreadLocal<SimpleDateFormat>() {
    override fun initialValue(): SimpleDateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US).with {
        setTimeZone(TimeZone.getTimeZone("GMT"))
    }
}

public fun HttpServerResponse.header(headerName: CharSequence, headerValue: Date): Unit {
    putHeader(headerName, dateFormatLocal.get().format(headerValue))
}

public fun HttpServerResponse.contentType(mimeType: String, encoding: String = "utf-8"): HttpServerResponse {
    return header("Content-Type", "$mimeType;charset=$encoding")
}

public fun HttpServerResponse.setStatus(code: Int, message: String): HttpServerResponse = with {
    setStatusCode(code)
    setStatusMessage(message)
}

public fun HttpServerResponse.setStatus(status: HttpResponseStatus, message: String): HttpServerResponse =
        setStatus(status.code(), message)

public fun HttpServerResponse.setStatusCode(status: HttpResponseStatus): HttpServerResponse = setStatusCode(status.code())


public inline var HttpServerResponse.contentLength: Long
    get() = headers().getAll("Content-Length").distinct().single().toLong()
    set(newValue) {
        setChunked(false)
        header("Content-Length", newValue)
    }

public inline fun HttpServerResponse.body(block: HttpServerResponse.() -> Unit) {
    setChunked(true)
    block()
    end()
}

public inline fun HttpServerResponse.bodyJson(block: Json.() -> Any) {
    setChunked(true)
    contentType("application/json")
    body {
        replyJson(block)
    }
}

public inline fun HttpServerRequest.response(block: HttpServerResponse.() -> Unit) {
    response().with(block)
}