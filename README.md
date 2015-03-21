#Vert.x 3 Kotlin bindings

Simple REST service:

```kotlin
class SimpleREST : AbstractVerticle() {
    override fun start() {
        httpServer(9091) {
            Json {
                object_(
                        "a" to 1,
                        "b" to array_("x", "y", "z"),
                        "params" to array_(it.params())
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
