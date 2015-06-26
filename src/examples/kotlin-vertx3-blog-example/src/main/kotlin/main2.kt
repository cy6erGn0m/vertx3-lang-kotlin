package testme2

import io.vertx.core.http.HttpServerFileUpload
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.streams.ReadStream
import io.vertx.kotlin.lang.*
import io.vertx.kotlin.lang.json.object_
import io.vertx.rx.java.RxHelper
import rx.Observable
import rx.lang.kotlin.fold
import rx.lang.kotlin.observable
import java.security.MessageDigest

fun <T> ReadStream<T>.toObservable(): Observable<T> = RxHelper.toObservable(this)
fun HttpServerRequest.fileUploads(): Observable<HttpServerFileUpload> = observable { subscriber ->
    uploadHandler { file ->
        subscriber.onNext(file)
    }
    endHandler {
        subscriber.onCompleted()
    }
    exceptionHandler {
        subscriber.onError(it)
    }
}

fun main(args: Array<String>) {
    val indexPage = ClassLoader.getSystemResourceAsStream("index.html")?.readBytes() ?: throw IllegalStateException("No index.html page found")
    val indexPageBuffer = bufferOf(indexPage)

    DefaultVertx {
        httpServer(8084, block = Route {
            GET("/") { request ->
                val response = request.response()

                response.contentType("text/html", "UTF-8")
                response.body {
                    write(indexPageBuffer)
                }
            }
            GET("/test") { request ->
                val response = request.response()
                response.contentType("text/plain")
                response.bodyJson {
                    object_(
                            "os.name" to System.getProperty("os.name"),
                            "processors" to Runtime.getRuntime().availableProcessors(),
                            "agents" to listOf("Master", "Slave1", "Slave2")
                    )
                }
            }
            POST("/upload") { request ->
                request.setExpectMultipart(true)
                response.contentType("text/plain", "UTF-8")
                response.setChunked(true)
                val start = System.currentTimeMillis()

                request.fileUploads()
                        .flatMap { fileUpload ->
                            fileUpload.toObservable()
                                    .fold(MessageDigest.getInstance("MD5") to 0) { md, buffer -> md.first.update(buffer.getBytes()); md.first to md.second + buffer.length() }
                                    .map { fileUpload.filename() to it }
                        }
                        .filter { it.second.second > 0 }
                        .map { "${it.second.first.digest().toHexString()}\t${it.first}" }
                        .fold(StringBuilder(8192)) { sb, e -> sb.append(e).append("\n") }
                        .map { sb -> sb.append("\nDone in ${System.currentTimeMillis() - start} ms\n").toString() }
                        .doOnNext { text ->
                            val response = request.response()
                            response.setChunked(true)
                            response.end(text)
                        }
                        .subscribe()
            }
            otherwise {
                otherwise {
                    setStatus(404, "Resource not found")
                    body {
                        write("The requested resource was not found\n")
                    }
                }
            }
        })
    }
}

private fun ByteArray.toHexString() = map { Integer.toHexString(it.toInt() and 0xff).padStart(2, '0') }.join("")

