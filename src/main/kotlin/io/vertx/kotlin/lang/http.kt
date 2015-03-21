package io.vertx.kotlin.lang


import io.vertx.core
import io.vertx.core.Handler
import io.vertx.core.Verticle
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.core.json.impl
import io.vertx.core.net.NetServerOptions
import io.vertx.core.shareddata.impl.ClusterSerializable
import io.vertx.kotlin.lang.json.Json
import kotlinx.util.with

public fun HttpServerOptions(port: Int, host: String = NetServerOptions.DEFAULT_HOST) : HttpServerOptions = HttpServerOptions().setHost(host).setPort(port)

public fun Verticle.createHttpServerWithOptions(options: HttpServerOptions, handler: HttpServerResponse.(HttpServerRequest) -> Unit): HttpServer =
        getVertx().createHttpServer(options).with { requestHandler { request -> request.response().handler(request) } }

public fun Verticle.createHttpServer(port: Int, host: String = NetServerOptions.DEFAULT_HOST, handler: HttpServerResponse.(HttpServerRequest) -> Unit): HttpServer =
        createHttpServerWithOptions(HttpServerOptions(port, host), handler)

public fun Verticle.httpServer(port : Int, host : String = NetServerOptions.DEFAULT_HOST, block : HttpServerResponse.(HttpServerRequest) -> Unit) : HttpServer =
        createHttpServer(port, host, block).listen()

public fun Verticle.httpServerWithOptions(options: HttpServerOptions, block : HttpServerResponse.(HttpServerRequest) -> Unit) : HttpServer =
        createHttpServerWithOptions(options, block).listen()

public fun HttpServer.listen(listenHandler: (AsyncResult<HttpServer>) -> Unit): HttpServer = this.listen(object : Handler<core.AsyncResult<HttpServer>> {
    override fun handle(event: core.AsyncResult<HttpServer>?) {
        listenHandler(event?.toAsyncResultK() ?: AsyncErrorResult<HttpServer>(NullPointerException("event shouldn't be null")))
    }
})

public fun HttpServerResponse.replyBuffer(block: () -> Buffer) {
    write(block()).end()
}

public fun HttpServerResponse.Buffer(block: Buffer.() -> Unit) : Unit { write(Buffer(8192, block)).end() }
public fun HttpServerResponse.Json(block: Json.() -> Any) : Unit {
    Json().block().let { result -> impl.Json.encode(result) }.let { encoded -> Buffer.buffer(encoded) }.let { buffer -> this.write(buffer)}
}