package examples

import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerRequest
import io.vertx.kotlin.lang.*
import io.vertx.kotlin.lang.json.JsonObject

// Kotlin port of https://github.com/eclipse/vert.x/blob/master/src/main/java/examples/CoreExamples.java
class CoreExamples {
    fun example1() {
        DefaultVertx {
        }
    }

    fun example2() {
        val options = VertxOptions {
            setWorkerPoolSize(40)
        }
        DefaultVertx(options) {

        }
    }

    fun example3(request: HttpServerRequest) {
        request.response()
                .contentType("text/plain")
                .writeBuffer {
                    appendString("some text")
                }
                .end()
    }

    fun example4(request: HttpServerRequest) {
        request.response {
            contentType("text/plain")
            replyWithBuffer {
                appendString("some text")
            }
        }
    }

    fun example5(vertx: Vertx) {
        vertx.setPeriodic(1000) {
            // This handler will get called every second
            println("Timer fired")
        }
    }

    fun example6(server: HttpServer) {
        server.requestHandler { request ->
            request.replyWithBuffer {
                appendString("hello world!")
            }
        }
    }

    object BlockingAPI {
        fun someBlockingMethod(s: String) = s
    }

    fun example7(vertx: Vertx) {
        vertx.executeBlocking<String>({ future ->
            future.complete(BlockingAPI.someBlockingMethod("hello"))
        }, {
            when (it) {
                is AsyncSuccessResult -> println("Success result is ${it.result}")
                is AsyncErrorResult -> println("Failed")
            }
        })
    }

    fun example7_1(vertx: Vertx) {
        vertx.deployVerticle("com.mycompany.MyOrderProcessorVerticle", DeploymentOptions().setWorker(true))
    }

    class MyVerticle : AbstractVerticle() {
        override fun start() {
            super.start()
        }
    }

    fun example8(vertx: Vertx) {
        val vertifcle = MyVerticle()
        vertx.deployVerticle(vertifcle)
    }

    fun example9(vertx: Vertx) {
        // Deploy a Java verticle - the name is the fully qualified class name of the verticle class
        vertx.deployVerticle("com.mycompany.MyOrderProcessorVerticle")

        // Deploy a JavaScript verticle
        vertx.deployVerticle("verticles/myverticle.js")

        // Deploy a Ruby verticle verticle
        vertx.deployVerticle("verticles/my_verticle.rb")
    }

    public fun example10(vertx: Vertx) {
        vertx.deployVerticle("com.mycompany.MyOrderProcessorVerticle") {
            when (it) {
                is AsyncSuccessResult -> println("Deployment id is: ${it.result}");
                is AsyncErrorResult -> println("Deployment failed")
            }
        }
    }

    fun example11(vertx: Vertx, deploymentID: String) {
        vertx.undeploy(deploymentID) {
            when (it) {
                is AsyncSuccessResult -> println("Undeployed ok");
                is AsyncErrorResult -> println("Undeploy failed")
            }
        }
    }

    public fun example12(vertx: Vertx) {
        val options = DeploymentOptions().setInstances(16)
        vertx.deployVerticle("com.mycompany.MyOrderProcessorVerticle", options)
    }

    public fun example13(vertx: Vertx) {
        val options = DeploymentOptions().setConfig(JsonObject(
                "name" to "tim",
                "directory" to "blah"
        ))

        vertx.deployVerticle("com.mycompany.MyOrderProcessorVerticle", options)
    }

    public fun example14(vertx: Vertx) {
        val options = DeploymentOptions().setIsolationGroup("mygroup")
        options.setExtraClasspath(listOf("lib/jars/some-library.jar"))
        vertx.deployVerticle("com.mycompany.MyIsolatedVerticle", options)
    }

    fun example15(vertx: Vertx) {
        val timerID = vertx.setTimer(1000) { id ->
            println("And one second later this is printed");
        }

        println("First this is printed as timer with ID $timerID scheduled")
    }

    fun example16(vertx: Vertx) {
        val timerID = vertx.setPeriodic(1000) { id ->
            println("And every second this is printed");
        }

        println("First this is printed as timer with ID $timerID scheduled")
    }

    public fun example17(vertx: Vertx, timerID: Long) {
        vertx.cancelTimer(timerID)
    }
}