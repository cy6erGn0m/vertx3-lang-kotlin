package io.vertx.kotlin.lang

import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.net.NetSocket
import io.vertx.core.streams.Pump
import io.vertx.core.streams.ReadStream
import io.vertx.core.streams.WriteStream
import io.vertx.kotlin.lang.json.Json

public fun <T> ReadStream<T>.startPumpTo(out: WriteStream<T>): Pump =
        Pump.pump(this, out).start()

public fun <T> ReadStream<T>.startPumpTo(maxQueueSize: Int, out: WriteStream<T>): Pump =
        Pump.pump(this, out, maxQueueSize).start()

@suppress("UNCHECKED_CAST")
public inline fun <T : WriteStream<Buffer>> T.writeBuffer(initialSize: Int = 8192, block: Buffer.() -> Unit): T = write(Buffer(initialSize, block)) as T

public inline fun HttpServerResponse.use(block: HttpServerResponse.() -> Unit): Unit {
    try {
        block()
    } finally {
        close()
    }
}

public inline fun NetSocket.use(block: NetSocket.() -> Unit) {
    try {
        block()
    } finally {
        close()
    }
}

public fun <W : WriteStream<Buffer>> W.writeJson(block: Json.() -> Any): W {
    writeBuffer {
        appendJson(block)
    }

    return this
}