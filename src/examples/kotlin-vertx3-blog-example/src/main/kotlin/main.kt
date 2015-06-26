package testme

import io.vertx.kotlin.lang.*
import java.security.MessageDigest

fun main(args: Array<String>) {
    val indexPage = ClassLoader.getSystemResourceAsStream("index.html")?.readBytes() ?: throw IllegalStateException("No index.html page found")
    val indexPageBuffer = bufferOf(indexPage)

    DefaultVertx {
        httpServer(8084, block = Route {
            GET("/") { request ->
                contentType("text/html", "UTF-8")
                body {
                    write(indexPageBuffer)
                }
            }
            POST("/upload") { request ->
                request.setExpectMultipart(true)
                contentType("text/plain", "UTF-8")
                setChunked(true)
                val start = System.currentTimeMillis()

                request.uploadHandler { file ->
                    val md5sum = MessageDigest.getInstance("MD5")
                    val fileName = file.filename()
                    var updated = false

                    file.handler {
                        md5sum.update(it.getBytes())
                        updated = true
                    }
                    file.endHandler {
                        val digest = md5sum.digest().toHexString()
                        if (updated || fileName.isNotBlank()) {
                            write("$digest\t$fileName\n")
                        }
                    }
                }

                request.endHandler {
                    end("\nProcessed in ${System.currentTimeMillis() - start} ms")
                }
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