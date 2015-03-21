package examples

import io.vertx.core.AbstractVerticle
import io.vertx.kotlin.lang.*
import io.vertx.kotlin.lang.json.array_
import io.vertx.kotlin.lang.json.object_
import kotlinx.util.with

class NetSocketsEcho : AbstractVerticle() {
    override fun start() {
        netServer(9090) { client ->
            client.handler { bytes ->
                client.write(bytes)
                client.writeBuffer {
                    appendString("yo")
                }
            }
            client.startPumpTo(client)
            client.closeHandler {

            }
        }
    }
}

class NetSocketBytes : AbstractVerticle() {
    override fun start() {
        netServer(9090) { client ->
            client.handler { bytes ->
                client.writeBuffer {
                    appendString("yo")
                    appendLf()
                    appendJson {
                        object_(
                                "field1" to "value1",
                                "field2" to array_(1, 2, 3)
                        )
                    }
                }
            }
        }
    }
}

class SameAnswerSockets : AbstractVerticle() {
    override fun start() {
        netServer(8080) { client ->
            client.use {
                writeBuffer {
                    appendString("Here we go\n")
                    appendString("Socket will be closed after all")
                }
            }
        }
    }
}