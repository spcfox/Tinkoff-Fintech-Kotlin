package ru.tinkoff.fintech.homework.lesson13.service

import org.springframework.stereotype.Service
import ru.tinkoff.fintech.homework.lesson13.service.consumer.EmailConsumer
import ru.tinkoff.fintech.homework.lesson13.service.consumer.NotificationConsumer
import ru.tinkoff.fintech.homework.lesson13.service.consumer.PushConsumer
import ru.tinkoff.fintech.homework.lesson13.service.consumer.SmsConsumer
import ru.tinkoff.fintech.homework.lesson13.model.Event
import ru.tinkoff.fintech.homework.lesson13.model.EventType

@Service
class NotificationUtils(
    val smsService: SmsConsumer,
    val emailService: EmailConsumer,
    val pushService: PushConsumer,
) {

    fun getNotificationConsume(event: Event): NotificationConsumer = when (event.type) {
        EventType.SMS -> smsService
        EventType.EMAIL -> emailService
        EventType.PUSH -> pushService
    }
}