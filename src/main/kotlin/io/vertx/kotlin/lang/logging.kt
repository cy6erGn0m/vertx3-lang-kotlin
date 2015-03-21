package io.vertx.kotlin.lang

import io.vertx.core.logging.Logger

public inline fun Logger.info(messageProvider: () -> String) {
    if (isInfoEnabled()) {
            info(messageProvider())
    }
}

public inline fun Logger.info(e : Throwable, messageProvider: () -> String) {
    if (isInfoEnabled()) {
        info(messageProvider(), e)
    }
}

public inline fun Logger.debug(messageProvider : () -> String) {
    if (isDebugEnabled()) {
        debug(messageProvider())
    }
}

public inline fun Logger.debug(t : Throwable, messageProvider : () -> String) {
    if (isDebugEnabled()) {
        debug(messageProvider(), t)
    }
}

public inline fun Logger.trace(messageProvider : () -> String) {
    if (isTraceEnabled()) {
        trace(messageProvider())
    }
}

public inline fun Logger.trace(t : Throwable, messageProvider : () -> String) {
    if (isTraceEnabled()) {
        trace(messageProvider(), t)
    }
}

