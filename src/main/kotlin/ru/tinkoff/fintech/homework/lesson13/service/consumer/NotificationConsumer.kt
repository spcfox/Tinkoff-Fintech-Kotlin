package ru.tinkoff.fintech.homework.lesson13.service.consumer

import ru.tinkoff.fintech.homework.lesson13.model.Event
import ru.tinkoff.fintech.homework.lesson13.model.EventStatus
import ru.tinkoff.fintech.homework.lesson13.repository.EventJpaRepository

abstract class NotificationConsumer(private val eventRepository: EventJpaRepository) {
    fun sendNotification(event: Event) {
        notification(event)
        eventRepository.updateStatus(event.id, EventStatus.DONE)
    }

    protected abstract fun notification(event: Event)
}