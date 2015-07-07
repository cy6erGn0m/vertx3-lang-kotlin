package etagWithCache

import io.vertx.core.AbstractVerticle
import io.vertx.kotlin.lang.DefaultVertx
import io.vertx.kotlin.lang.contentType
import io.vertx.kotlin.lang.http.*
import io.vertx.kotlin.lang.httpServer
import io.vertx.kotlin.lang.json.array_
import io.vertx.kotlin.lang.json.object_
import io.vertx.kotlin.lang.replyJson

fun main(args: Array<String>) {
    DefaultVertx {
        val cache = Cache(this, "target/cache")

        httpServer(9091) { request ->
            contentType("application/json")
            setChunked(true)

            request.withHashEtag(request.params()) {
                request.withHashedCache(cache, request.params()) {
                    Thread.sleep(10000)
                    replyJson {
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
