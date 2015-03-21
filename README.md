#Vert.x 3 Kotlin bindings

Simple REST service:

```kotlin
fun main(args: Array<String>) {
    DefaultVertx {
        httpServer(8084) { request ->
            bodyJson {
                object_(
                        "title" to "Hello, my remote peer",
                        "message" to "You address is ${request.remoteAddress().host()}"
                )
            }
        }
    }
}
```

Simple echo socket server
```kotlin
class EchoSocket : AbstractVerticle() {
    override fun start() {
        netServer(9094) { client ->
            client.startPumpTo(client)
        }
    }
}
```

HTTP routing example:
```kotlin
import io.vertx.kotlin.lang.*

fun main(args: Array<String>) {
    DefaultVertx {
        httpServer(8084, "0.0.0.0", Route {
            GET("/path") { request ->
                bodyJson {
                    "field1" to "test me not"
                }
            }

            otherwise {
                setStatus(404, "Resource not found")
                body {
                    write("The requested resource was not found\n")
                }
            }
        })
    }
}
```

See https://github.com/cy6erGn0m/vertx3-lang-kotlin/tree/master/src/examples/kotlin for more examples
