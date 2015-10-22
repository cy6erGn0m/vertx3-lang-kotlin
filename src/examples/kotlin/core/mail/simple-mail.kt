package core.mail

import io.vertx.core.*
import io.vertx.ext.mail.*
import io.vertx.kotlin.lang.*

fun sendMe(vertx: Vertx) {
    val mailService = MailService.createEventBusProxy(vertx, "test.mail")
    mailService.sendMail(MailMessage("no-reply@test.org", "user@some.server.org", "test me not", "Body")) {
        when (it) {
            is AsyncErrorResult -> println("Failed to send mail")
            is AsyncSuccessResult -> println("Succesfully sent: ${it.result}")
        }
    }
}
