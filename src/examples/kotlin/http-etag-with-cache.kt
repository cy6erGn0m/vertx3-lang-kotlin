package etagWithCache

import io.vertx.core.AbstractVerticle
import io.vertx.kotlin.lang.*
import io.vertx.kotlin.lang.http.*
import io.vertx.kotlin.lang.json.array_
import io.vertx.kotlin.lang.json.object_

fun main(args: Array<String>) {
    DefaultVertx {
        val cache = Cache(this, "target/cache")

        httpServer(9091) { request ->
            contentType("application/json")
            setChunked(true)

            val params = request.params().toList().groupBy { it.key }.mapValues { it.value.map { it.value } }

            request.withHashEtag(params) {
                request.withHashedCache(cache, params) { end ->
                    setTimer(2000L) {
                        writeJson {
                            object_(
                                    "a" to 1,
                                    "b" to array_("x", "y", "z"),
                                    "params" to array_(request.params())
                            )
                        }
                        end()
                    }
                }
            }
        }
    }
}
