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

See https://github.com/cy6erGn0m/vertx3-lang-kotlin/tree/master/src/examples/kotlin for more examples
