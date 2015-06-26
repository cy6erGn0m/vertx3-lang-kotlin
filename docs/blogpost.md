# Kotlin goes reactive with Vert.x 3

Reactive approach popularity growth every day. The main reason is that traditional thread blocking approach has big
 disadvantage: a running application could stuck if there are many requests that hold too many physical threads.
 [Kotlin](http://kotlinlang.org) is not an exception so due to the same reason Kotlin programming may be not so pleasant
 as it should be.

Fortunately there is [vert.x](http://vertx.io/) that help you to build
 [reactive](http://www.reactivemanifesto.org/) applications. With new
 [vertx3-lang-kotlin](https://github.com/cy6erGn0m/vertx3-lang-kotlin) and
  [RxKotlin](https://github.com/ReactiveX/RxKotlin) it becomes even better.

# Get started

Before do something let's start with some definitions

First of all lets start with empty HTTP server

```kotlin
import io.vertx.kotlin.lang.*

fun main(args: Array<String>) {
    DefaultVertx {
        httpServer(8084) { request ->
            val response = request.response()
            response.contentType("text/plain")
            response.body {
                replyText("Hello, world!")
            }
        }
    }
}
```

This server does nothing but always responding the same text. Looks not enough interesting, isn't it? We can respond
 with some JSON instead:

```kotlin
import io.vertx.kotlin.lang.*
import io.vertx.kotlin.lang.json.object_

fun main(args: Array<String>) {
    DefaultVertx {
        httpServer(8084) { request ->
            val response = request.response()
            response.contentType("application/json")
            response.body {
                replyJson {
                    object_(
                            "os.name" to System.getProperty("os.name"),
                            "processors" to Runtime.getRuntime().availableProcessors(),
                            "agents" to listOf("Master", "Slave1", "Slave2")
                    )
                }
            }
        }
    }
}
```

Once I ran it in my browser I can see the following JSON
```json
{"os.name":"Linux","processors":8,"agents":["Master","Slave1","Slave2"]}

```

Quick and simple, isn't it? Well, we have this response to _any_ request but what if I need more control? Here we go
 with httproute provided by vertx3-lang-kotlin library

```kotlin
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
        })
    }
}
```

Notice functions `Route` and `GET` that just assembles lambdas for you to get corresponding request handler. Also it
does a bit magic to get you code even shorter because `this` will be reassigned to response
What about 404 then? Let's add it too:

```kotlin
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
```

After all we still haven't start page so let's add it too. Before begin let me notice that in real life static pages
 should't be done as I am going to do in this example. As it is just example I omit required configurations. In
 real application you also may need template engine. There are tons of them so just take any you like

So first of all I'll put index.html page to the resources directory and load it's content like this:

```kotlin
val indexPage = ClassLoader.getSystemResourceAsStream("index.html")?.readBytes() ?: throw IllegalStateException("No index.html page found")
val indexPageBuffer = bufferOf(indexPage)
```

after that it is easy to use it in `GET("/")`
```kotlin
GET("/") { request ->
    contentType("text/html", "UTF-8")
    body {
        write(indexPageBuffer)
    }
}
```



