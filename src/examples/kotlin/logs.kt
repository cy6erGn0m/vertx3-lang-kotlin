package examples

import io.vertx.core.AbstractVerticle
import io.vertx.core.logging.Logger
import io.vertx.core.logging.impl.LoggerFactory
import io.vertx.kotlin.lang.createHttpServer

class LoggerVerticle : AbstractVerticle() {
    val logger = LoggerFactory.getLogger("me")

    override fun start() {
        // if trace is not enabled then no string will be actually constructed
        // also as far as trace function inlined so no additional overhead of this method call except call to logger.isTraceEnabled()
        logger.trace {
            (1..100).map {"fox jump no. $it"}.joinToString("\n")
        }
    }
}