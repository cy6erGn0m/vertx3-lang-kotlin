package io.vertx.kotlin.lang.http

import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.file.FileSystem
import io.vertx.core.file.OpenOptions
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.logging.LoggerFactory
import io.vertx.core.streams.WriteStream
import io.vertx.kotlin.lang.AsyncSuccessResult
import io.vertx.kotlin.lang.endIfNotYet
import io.vertx.kotlin.lang.startPumpTo
import io.vertx.kotlin.lang.toAsyncResultK
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.name

class Cache(val vertx: Vertx, val cacheDirLocation: String)

public fun HttpServerRequest.withStringifiedCache(cache: Cache, vararg items: Any?, block: WriteStream<Buffer>.(onEnd: () -> Unit) -> Unit) {
    withCache(cache, { it?.toStringEx() ?: "null" }, *items, predicate = ::predicateAlwaysTrue, block = block)
}

public fun HttpServerRequest.withHashedCache(cache: Cache, vararg items: Any?, block: WriteStream<Buffer>.(onEnd: () -> Unit) -> Unit) {
    withCache(cache, { it?.hashCodeEx()?.toHexString() ?: "0" }, *items, predicate = ::predicateAlwaysTrue, block = block)
}

public fun HttpServerRequest.withCache(cache: Cache,
                                       mapper: (Any?) -> String,
                                       vararg items: Any?,
                                       predicate: (file: String, onValid: () -> Unit, onInvalid: () -> Unit) -> Unit = ::predicateAlwaysTrue,
                                       block: WriteStream<Buffer>.(onEnd: () -> Unit) -> Unit) {

    val cacheKey = items.map(mapper).joinToString("")
    val cacheFileLocation = "${cache.cacheDirLocation}/$cacheKey.cache"
    val tempFileLocation = cacheFileLocation + ".${System.currentTimeMillis()}.${Thread.currentThread().getId()}.tmp"

    cache.vertx.fileSystem().mkdirs(cache.cacheDirLocation) {
        cache.vertx.fileSystem().open(cacheFileLocation, OpenOptions().setRead(true).setCreate(false)) { existingFileOrFail ->
            if (existingFileOrFail.succeeded()) {
                predicate(cacheFileLocation, {  // valid
                    val existingFile = existingFileOrFail.result()!!

                    existingFile.endHandler {
                        response().endIfNotYet()
                        existingFile.close()
                    }
                    existingFile.exceptionHandler {
                        LoggerFactory.getLogger("io.vertx.kotlin.lang.http").error("File read failed", it)

                        response().endIfNotYet()
                        existingFile.close()
                    }

                    existingFile.startPumpTo(response())
                }, {   // invalidate
                    cache.vertx.fileSystem().delete(cacheFileLocation) {
                        // retry
                        withCache(cache, mapper, predicate, *items, block = block)
                    }
                })
            } else {
                cache.vertx.fileSystem().open(tempFileLocation, OpenOptions().setCreate(true).setWrite(true).setTruncateExisting(true)) { newFileOrFail ->
                    if (newFileOrFail.succeeded()) {
                        val newFile = newFileOrFail.result()!!

                        newFile.block {
                            newFile.close {
                                cache.vertx.fileSystem().move(tempFileLocation, cacheFileLocation) {
                                    withCache(cache, mapper, *items, predicate = ::predicateAlwaysTrue, block = block) // ::predicateAlwaysTrue here to avoid possible infinite loop
                                }
                            }
                        }
                    } else {
                        // pass through with no cache
                        response().block {
                            response().endIfNotYet()
                        }
                    }
                }
            }
        }

    }
}

@suppress("UNUSED_PARAMETER")
private fun predicateAlwaysTrue(file: String, onValid: () -> Unit, onInvalid: () -> Unit) {
    onValid()
}

private fun Any.toStringEx() = when (this) {
    is Array<*> -> Arrays.toString(this)
    else -> toString()
}

private fun Any.hashCodeEx() = when (this) {
    is Array<*> -> Arrays.hashCode(this)
    else -> hashCode()
}
