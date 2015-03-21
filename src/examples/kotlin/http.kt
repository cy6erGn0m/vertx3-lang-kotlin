package examples

import io.vertx.core.AbstractVerticle
import io.vertx.kotlin.lang.replyJson
import io.vertx.kotlin.lang.httpServer
import io.vertx.kotlin.lang.json.array_
import io.vertx.kotlin.lang.json.json_
import io.vertx.kotlin.lang.json.object_

class SimpleRest : AbstractVerticle() {
    override fun start() {
        httpServer(9091) { request ->
            replyJson {
                object_(
                        "a" to 1,
                        "b" to array_("x", "y", "z"),
                        "params" to array_(request.params())
                )
            }
        }
    }
}

