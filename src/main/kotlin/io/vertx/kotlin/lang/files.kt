package io.vertx.kotlin.lang

import io.vertx.core
import io.vertx.core.Handler
import io.vertx.core.buffer.Buffer
import io.vertx.core.file.*
import kotlinx.util.with

public inline fun OpenOptions(block: OpenOptions.() -> Unit): OpenOptions = OpenOptions().with(block)

public fun FileSystem.open(path: String, options: OpenOptions = OpenOptions(), handler: (AsyncResult<AsyncFile>) -> Unit) {
    open(path, options, Handler { handler(it.toAsyncResultK()) })
}

public fun FileSystem.delete(path: String, handler: (AsyncResult<Void>) -> Unit) {
    delete(path, Handler { handler(it.toAsyncResultK()) })
}

public fun FileSystem.move(from: String, to: String, handler: (AsyncResult<Void>) -> Unit) {
    move(from, to, Handler { handler(it.toAsyncResultK()) })
}

public fun FileSystem.copy(from: String, to: String, handler: (AsyncResult<Void>) -> Unit) {
    copy(from, to, Handler { handler(it.toAsyncResultK()) })
}

public fun FileSystem.props(path: String, handler: (AsyncResult<FileProps>) -> Unit) {
    props(path, Handler { handler(it.toAsyncResultK()) })
}

public fun FileSystem.size(path: String, handler: (AsyncResult<Long>) -> Unit) {
    props(path) {
        handler(it.mapIfSuccess { it.size() })
    }
}

public fun AsyncFile.close(handler: (AsyncResult<Void>) -> Unit) {
    close(Handler { handler(it.toAsyncResultK()) })
}

public fun AsyncFile.read(buffer: Buffer, offset: Int, position: Long, length: Int, handler: (AsyncResult<Buffer>) -> Unit) {
    read(buffer, offset, position, length, Handler { handler(it.toAsyncResultK()) })
}

public fun AsyncFile.flush(handler: (AsyncResult<Void>) -> Unit) {
    flush(Handler { handler(it.toAsyncResultK()) })
}

