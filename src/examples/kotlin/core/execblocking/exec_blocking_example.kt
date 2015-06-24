package core.execblocking

import io.vertx.core.Vertx
import io.vertx.kotlin.lang.AsyncErrorResult
import io.vertx.kotlin.lang.AsyncSuccessResult
import io.vertx.kotlin.lang.executeBlocking

fun executeBlockingExample(vertx: Vertx) {
    vertx.executeBlocking<String> ({ f ->
        Thread.sleep(500)
        f.complete("Kotlin + vert.x!")
    }, { result ->
        when (result) {
            is AsyncSuccessResult -> println("Succeeded: ${result.result}")
            is AsyncErrorResult -> println("Failed to complete future: ${result.error}")
        }
    })
}
