package ru.tinkoff.fintech.homework.lesson13.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import ru.tinkoff.fintech.homework.lesson13.model.Event
import ru.tinkoff.fintech.homework.lesson13.service.NotificationService

@RestController
class NotificationController(val notificationService: NotificationService) {

    @PostMapping("sms")
    fun sms(body: String): Event = notificationService.newSms(body)

    @PostMapping("email")
    fun email(body: String): Event = notificationService.newEmail(body)

    @PostMapping("push")
    fun push(body: String): Event = notificationService.newPush(body)
}
