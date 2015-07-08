# Vert.x 3 Kotlin bindings [ ![Download](https://api.bintray.com/packages/cy6ergn0m/maven/vertx3-lang-kotlin/images/download.svg) ](https://bintray.com/cy6ergn0m/maven/vertx3-lang-kotlin/_latestVersion)

This module provides [Kotlin](http://kotlinlang.org) language bindings including DSL and extension functions 
for [vert.x 3](http://vertx.io/) and provides better Kotlin programming experience

# Get started (Gradle)

Please refer to [Kotlin Using Gradle](http://kotlinlang.org/docs/reference/using-gradle.html) for Kotlin Gradle
configuration before.

When you have kotlin configured properly add the following repository

```groovy
repositories { maven { url "http://dl.bintray.com/cy6ergn0m/maven" } }
```

then add dependency

```groovy
dependencies {
    compile 'org.jetbrains.kotlinx:vertx3-lang-kotlin:0.0.+'
}
```

See [Kotlin vert.x 3 Gradle project example](src/examples/kotlin-vertx3-gradle-example)

### Run example application
```bash
./gradlew runExample
```

after that open http://localhost:8084/testit.html and try to play with queries and the [source code](src/examples/kotlin/route.kt).

## Examples

### Simple REST service

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

### Simple echo socket server

```kotlin
class EchoSocket : AbstractVerticle() {
    override fun start() {
        netServer(9094) { client ->
            client.startPumpTo(client)
        }
    }
}
```

### HTTP routing example (non-apex)

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

### HTTP ETag and on-disk cache support

```kotlin
import io.vertx.kotlin.lang.*
import io.vertx.kotlin.lang.http.*
import io.vertx.kotlin.lang.json.*

fun main(args: Array<String>) {
    DefaultVertx {
        val cache = Cache(this, "target/cache")

        httpServer(9091) { request ->
            contentType("application/json")
            setChunked(true)

            val params = request.params().toList().groupBy { it.key }.mapValues { it.value.map { it.value } }

            request.withHashEtag(params) {
                request.withHashedCache(cache, params) { end ->
                    setTimer(2000L) {
                        writeJson {
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
}
```

### More examples
See [more examples](src/examples/kotlin). Some of them just copied from original examples at vert.x repo.

