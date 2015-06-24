package io.vertx.kotlin.lang

import io.vertx.core.Handler
import io.vertx.ext.mail.MailMessage
import io.vertx.ext.mail.MailResult
import io.vertx.ext.mail.MailService

fun MailService.sendMail(message: MailMessage, event: (AsyncResult<MailResult>) -> Unit) {
    sendMail(message, Handler { result ->
        event(result.toAsyncResultK())
    })
}
