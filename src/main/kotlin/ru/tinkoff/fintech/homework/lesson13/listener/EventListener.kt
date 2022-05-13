package ru.tinkoff.fintech.homework.lesson13.listener

import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Service
import ru.tinkoff.fintech.homework.lesson13.model.EventStatus
import ru.tinkoff.fintech.homework.lesson13.repository.EventJpaRepository
import ru.tinkoff.fintech.homework.lesson13.service.NotificationUtils

@Service
class EventListener(
    val eventRepository: EventJpaRepository,
    val notificationUtils: NotificationUtils,
    ) {

    @JmsListener(destination = "events")
    fun consume(eventId: Int) {
        val event = eventRepository.getById(eventId)
        if (event.status == EventStatus.IN_PROCESS) {
            try {
                val notificationConsume = notificationUtils.getNotificationConsume(event)
                notificationConsume.sendNotification(event)
            } catch (e: Exception) {
                eventRepository.updateStatus(event.id, EventStatus.ERROR)
                throw e
            }
        }
    }
}
