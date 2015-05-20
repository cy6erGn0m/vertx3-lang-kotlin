package io.vertx.kotlin.lang

import io.vertx.core
import io.vertx.core.Handler
import io.vertx.core.Verticle
import io.vertx.core.net.NetServer
import io.vertx.core.net.NetServerOptions
import io.vertx.core.net.NetSocket

public fun NetServerOptions(port: Int, host: String = NetServerOptions.DEFAULT_HOST): NetServerOptions = NetServerOptions().setHost(host).setPort(port)

public fun Verticle.createNetServerWithOptions(options: NetServerOptions = NetServerOptions(), block: (NetSocket) -> Unit): NetServer = getVertx().createNetServer(options).connectHandler(block)
public fun Verticle.createNetServer(port: Int, host: String = NetServerOptions.DEFAULT_HOST, block: (NetSocket) -> Unit): NetServer =
        createNetServerWithOptions(NetServerOptions(port, host), block)

public fun Verticle.netServerWithOptions(options: NetServerOptions, block: (NetSocket) -> Unit): NetServer =
        createNetServerWithOptions(options, block).listen()

public fun Verticle.netServer(port: Int, host: String = NetServerOptions.DEFAULT_HOST, block: (NetSocket) -> Unit): NetServer =
        netServerWithOptions(NetServerOptions(port, host), block)

public fun NetServer.listen(handler: (AsyncResult<NetServer>) -> Unit): NetServer = listen(object : Handler<core.AsyncResult<NetServer>> {
    override fun handle(event: core.AsyncResult<NetServer>?) {
        handler(event?.toAsyncResultK() ?: AsyncErrorResult<NetServer>(NullPointerException("async result is null")))
    }
})
