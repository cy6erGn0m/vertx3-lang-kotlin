#Vert.x 3 Kotlin bindings

Simple REST service:

```kotlin
class KVerticle : AbstractVerticle() {
    override fun start() {
        httpServer(9091) {
            Json {
                object_(
                        "a" to 1,
                        "b" to array_("x", "y", "z"),
                        "params" to object_(it.params().map { it.getKey() to it.getValue() })
                )
            }
        }
    }
}
```

Simple echo socket server
```kotlin
class X : AbstractVerticle() {
    override fun start() {
        netServer(9094) { client ->
            client.startPumpTo(client)
        }
    }
}
```
