package example1

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.kotlin.lang.*
import io.vertx.kotlin.lang.json.object_

fun main(args: Array<String>) {
    DefaultVertx {
        httpServer(8084, block = Route {
            GET("/") {
                contentType("text/plain")
                replyText("Hello, world!")
            }
            GET("/info") {
                contentType("application/json")
                bodyJson {
                    object_(
                            "os.name" to System.getProperty("os.name"),
                            "processors" to Runtime.getRuntime().availableProcessors(),
                            "agents" to listOf("Master", "Slave1", "Slave2")
                    )
                }
            }
            otherwise {
                setStatus(HttpResponseStatus.NOT_FOUND, "Not found")
                contentType("text/plain")
                replyText("The requested resource ${it.path()} was not found")
            }
        })
    }
}
