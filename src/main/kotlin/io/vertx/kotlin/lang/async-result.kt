package io.vertx.kotlin.lang


import io.vertx.core.AsyncResult as coreAsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.Vertx

public interface AsyncResult<T>
public class AsyncSuccessResult<T>(public val result: T) : AsyncResult<T>
public class AsyncErrorResult<T>(public val error: Throwable) : AsyncResult<T>

public val AsyncResult<*>.success: Boolean
    get() = this is AsyncSuccessResult

public fun <T> AsyncResult<T>.sendToFuture(f: Future<T>): Unit = when (this) {
    is AsyncSuccessResult -> f.complete(result)
    is AsyncErrorResult -> f.fail(error)
    else -> f.fail(IllegalStateException("Unknown AsyncResult"))
}

public fun AsyncResult<*>.sendToFutureVoid(f: Future<Void>): Unit = when (this) {
    is AsyncSuccessResult -> f.complete(null)
    is AsyncErrorResult -> f.fail(error)
    else -> f.fail(IllegalStateException("Unknown AsyncResult"))
}

public fun <T> coreAsyncResult<T>.toAsyncResultK(): AsyncResult<T> = when {
    succeeded() -> AsyncSuccessResult(this.result())
    failed() -> AsyncErrorResult(this.cause())
    else -> AsyncErrorResult(IllegalStateException("Bad async result object ${this.javaClass.name}"))
}

public inline fun <F, T> AsyncResult<F>.mapIfSuccess(map: (F) -> T): AsyncResult<T> = when (this) {
    is AsyncSuccessResult -> AsyncSuccessResult(map(this.result))
    is AsyncErrorResult -> AsyncErrorResult<T>(this.error)
    else -> AsyncErrorResult(IllegalStateException("Bad async result object ${this.javaClass.name}"))
}

public inline fun <T, R> AsyncResult<T>.ifSuccess(errorValue: R, block: (T) -> R): R = when (this) {
    is AsyncSuccessResult -> block(this.result)
    else -> errorValue
}

public fun <T> AsyncResult<T>.asSequence(): Sequence<T> = when (this) {
    is AsyncSuccessResult -> sequenceOf(this.result)
    else -> emptySequence()
}

public fun <T> Vertx.executeBlocking(blockingCodeHandler: (Future<T>) -> Unit, resultHandler: (AsyncResult<T>) -> Unit) {
    executeBlocking<T>(Handler { blockingCodeHandler(it) }, Handler { resultHandler(it.toAsyncResultK()) })
}