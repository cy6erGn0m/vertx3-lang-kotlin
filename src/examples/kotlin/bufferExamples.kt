package examples

import io.vertx.core.net.NetSocket
import io.vertx.kotlin.lang.Buffer
import io.vertx.kotlin.lang.appendLf
import io.vertx.kotlin.lang.bufferOf
import io.vertx.kotlin.lang.writeBuffer

// this is port of https://github.com/eclipse/vert.x/blob/master/src/main/java/examples/BufferExamples.java

class BufferExamples {
    fun example1() {
        val buffer = Buffer {}
    }

    fun example2() {
        val buffer = bufferOf("some string")
    }

    fun example3() {
        val buffer = bufferOf("some string", Charsets.UTF_16)
    }

    fun example5() {
        val buffer = Buffer(10000) {
            // put something...
        }
    }

    fun example6(socket : NetSocket) {
        socket.writeBuffer {
            appendInt(127)
            appendString("Hello")
            appendLf()
        }
    }

    fun example7() {
        Buffer {
            setInt(1000, 123)
            setString(0, "hello")
        }
    }

    fun example8() {
        val buffer = Buffer {}

        for (pos in 0 .. buffer.length() step 4) {
            println("int value at $pos is ${buffer.getInt(pos)}");
        }
    }
}