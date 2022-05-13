package ru.tinkoff.fintech.homework.lesson13.service.consumer

import org.springframework.stereotype.Service
import ru.tinkoff.fintech.homework.lesson13.model.Event
import ru.tinkoff.fintech.homework.lesson13.repository.EventJpaRepository

@Service
class SmsConsumer(eventRepository: EventJpaRepository) : NotificationConsumer(eventRepository) {
    override fun notification(event: Event) {
        println("SMS: ${event.body}")
    }
}