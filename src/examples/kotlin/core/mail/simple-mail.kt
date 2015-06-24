package core.mail

import io.vertx.core.Vertx
import io.vertx.ext.mail.MailMessage
import io.vertx.ext.mail.MailResult
import io.vertx.ext.mail.MailService
import io.vertx.kotlin.lang.AsyncErrorResult
import io.vertx.kotlin.lang.AsyncSuccessResult
import io.vertx.kotlin.lang.sendMail

fun sendMe(vertx: Vertx) {
    val mailService = MailService.createEventBusProxy(vertx, "test.mail")
    mailService.sendMail(MailMessage("no-reply@test.org", "user@some.server.org", "test me not", "Body")) {
        when (it) {
            is AsyncErrorResult -> println("Failed to send mail")
            is AsyncSuccessResult -> println("Succesfully sent: ${it.result}")
        }
    }
}
