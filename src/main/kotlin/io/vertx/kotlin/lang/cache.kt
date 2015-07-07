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
import io.vertx.kotlin.lang.startPumpTo
import io.vertx.kotlin.lang.toAsyncResultK
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.name

class Cache(val vertx: Vertx, val cacheDirLocation: String)

public fun HttpServerRequest.withStringifiedCache(cache: Cache, vararg items: Any?, block: WriteStream<Buffer>.() -> Unit) {
    withCache(cache, {it.toString()}, *items, block = block)
}

public fun HttpServerRequest.withHashedCache(cache: Cache, vararg items: Any?, block: WriteStream<Buffer>.() -> Unit) {
    withCache(cache, {it?.hashCode()?.toHexString() ?: "0"}, *items, block = block)
}

public fun HttpServerRequest.withCache(cache: Cache, mapper: (Any?) -> String, vararg items: Any?, block: WriteStream<Buffer>.() -> Unit) {
    val cacheKey = items.map(mapper).joinToString("")
    val cacheFileLocation = "${cache.cacheDirLocation}/$cacheKey.cache"
    val tempFileLocation = cacheFileLocation + ".${System.currentTimeMillis()}.${Thread.currentThread().getId()}.tmp"

    cache.vertx.fileSystem().mkdirs(cache.cacheDirLocation) {
        cache.vertx.fileSystem().open(cacheFileLocation, OpenOptions().setRead(true).setCreate(false)) { existingFileOrFail ->
            if (existingFileOrFail.succeeded()) {
                // sending loop
                val existingFile = existingFileOrFail.result()!!
                existingFile.startPumpTo(response())
                existingFile.endHandler {
                    response().end()
                    existingFile.close()
                }
                existingFile.exceptionHandler {
                    LoggerFactory.getLogger("io.vertx.kotlin.lang.http").error("File read failed", it)

                    response().end()
                    existingFile.close()
                }
            } else {
                cache.vertx.fileSystem().open(tempFileLocation, OpenOptions().setCreate(true).setWrite(true).setTruncateExisting(true)) { newFileOrFail ->
                    if (newFileOrFail.succeeded()) {
                        val newFile = newFileOrFail.result()!!

                        try {
                            newFile.block()
                        } finally {
                            newFile.close {
                                cache.vertx.fileSystem().move(tempFileLocation, cacheFileLocation) {
                                    withCache(cache, mapper, *items, block = block)
                                }
                            }
                        }
                    } else {
                        // pass through with no cache
                        response().block()
                    }
                }
            }
        }

    }
}
