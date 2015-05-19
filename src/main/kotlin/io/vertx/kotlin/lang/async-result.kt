package io.vertx.kotlin.lang


import io.vertx.core
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.Vertx

public interface AsyncResult<T>
public class AsyncSuccessResult<T>(val result: T) : AsyncResult<T>
public class AsyncErrorResult<T>(val error: Throwable) : AsyncResult<T>

public inline val AsyncResult<*>.success: Boolean
    get() = this is AsyncSuccessResult

public fun <T> AsyncResult<T>.sendToFuture(f : Future<T>) : Unit = when(this) {
    is AsyncSuccessResult -> f.complete(result)
    is AsyncErrorResult -> f.fail(error)
    else -> f.fail(IllegalStateException("Unknown AsyncResult"))
}

public fun AsyncResult<*>.sendToFutureVoid(f : Future<Void>) : Unit = when(this) {
    is AsyncSuccessResult -> f.complete(null)
    is AsyncErrorResult -> f.fail(error)
    else -> f.fail(IllegalStateException("Unknown AsyncResult"))
}

public fun <T> core.AsyncResult<T>.toAsyncResultK(): AsyncResult<T> = when {
    succeeded() -> AsyncSuccessResult(this.result())
    failed() -> AsyncErrorResult(this.cause())
    else -> AsyncErrorResult(IllegalStateException("Bad async result object ${this.javaClass.getName()}"))
}

public fun <T> Vertx.executeBlocking(blockingCodeHandler: (Future<T>) -> Unit, resultHandler: (AsyncResult<T>) -> Unit) {
    executeBlocking<T>(Handler { blockingCodeHandler(it) }, Handler { resultHandler(it.toAsyncResultK()) })
}