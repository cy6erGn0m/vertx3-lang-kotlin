package io.vertx.kotlin.lang


import io.vertx.core
import io.vertx.core.Future

public trait AsyncResult<T>
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
    this.succeeded() -> AsyncSuccessResult(this.result())
    this.failed() -> AsyncErrorResult(this.cause())
    else -> AsyncErrorResult(IllegalStateException("Bad async result object"))
}