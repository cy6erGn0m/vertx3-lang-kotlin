# Vert.x 3 Kotlin bindings
[ ![Download](https://api.bintray.com/packages/cy6ergn0m/maven/vertx3-lang-kotlin/images/download.svg) ](https://bintray.com/cy6ergn0m/maven/vertx3-lang-kotlin/_latestVersion)
[![Build Status](https://travis-ci.org/cy6erGn0m/vertx3-lang-kotlin.svg?branch=master)](https://travis-ci.org/cy6erGn0m/vertx3-lang-kotlin)
[ ![Kotlin](https://img.shields.io/badge/Kotlin-1.0.2-blue.svg) ](https://kotlinlang.org/)

This module provides [Kotlin](http://kotlinlang.org) language bindings including DSL and extension functions 
for [vert.x 3](http://vertx.io/) and provides better Kotlin programming experience

# Getting started (Gradle)

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

# Getting started (Maven)

Please refer to [Kotlin Using Maven](http://kotlinlang.org/docs/reference/using-maven.html) for Kotlin Maven
configuration before setup vertx3-lang-kotlin.

Once you get it well configured you can proceed.

First of all you need additional repository added:

```xml
<repository>
    <id>bintray-cy</id>
    <name>bintray-cy</name>
    <url>http://dl.bintray.com/cy6ergn0m/maven</url>
</repository>
```

After that you can add dependency

```xml
<dependency>
    <groupId>org.jetbrains.kotlinx</groupId>
    <artifactId>vertx3-lang-kotlin</artifactId>
    <version>[0.0.4,0.1.0)</version>
</dependency>
```

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
                        end()  // NOTE you MUST call it at end of with*Cache lambda
                    }
                }
            }
        }
    }
}
```

Cached page to be updated only when something changes

```kotlin
    request.withHashedCache(cache, blog.lastPost?.id) { end ->
        writeJson {
            object_(
                    "title" to blog.displayName,
                    "lastPostTitle" to blog.lastPost?.title
            )
        }
        end()
    }
```

Shopping cart ETags example:

```kotlin
val cart = session.getCart()

request.withHashedEtag(cart.items.map { it.id to it.amount }) {
  // generate page
}
```

In this example we have ETag based on cart content so if user has no any changes in the cart then the page will not be
 generated if user already have valid version in cart's page.

Both on-disk caching and ETag configured properly may significantly reduce web server performance. However
 you have to choose among ETag/Cache and think of valid keys to be used.
 The general recommendation is to use on-disk cache for public pages (available to all users and 
 have the same content) and etags for everything possible.
 
> :red_circle: Note that on-disk cache is never purged automatically so you have to do it your own.  

> :red_circle: Notice that you can't set any headers/cookies/etc in a cached lambda. If you need something special you have to specify it before withCache block

#### On-disk invalidation

You can optionally provide your invalidation strategy by passing predicate function: this function will be called for
 each request and receive cache file name (you can use it e.g. to check file modification date or exam it's content) and
 two lambdas `onValid` and `onInvalid`: you must to call one of them according to your decision.

### More examples
See [more examples](src/examples/kotlin). Some of them just copied from original examples at vert.x repo.

