package examples

import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Future
import io.vertx.kotlin.lang.*
import io.vertx.kotlin.lang.json.object_

class DeployVerticle : AbstractVerticle() {
    override fun start() {
        verticle("deployMe") {
            when (it) {
                is AsyncSuccessResult -> println("Successfully deployed with ID ${it.result}")
                is AsyncErrorResult -> println("Failed to deploy verticle with error ${it.error}")
            }
        }
    }
}

class DeployVerticleWithOptionsAsync : AbstractVerticle() {
    override fun start(startFuture: Future<Void>) {
        val deploymentOptions = DeploymentOptions().withConfig {
            object_(
                    "myOption" to "1"
            )
        }
        verticle("yo", deploymentOptions) { r -> r.sendToFutureVoid(startFuture) }
    }
}

