package ru.tinkoff.fintech.homework.lesson13.sheduled

import org.springframework.jms.core.JmsTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import ru.tinkoff.fintech.homework.lesson13.model.EventStatus
import ru.tinkoff.fintech.homework.lesson13.repository.EventJpaRepository

@Service
class EventHandler(val jmsTemplate: JmsTemplate, val eventRepository: EventJpaRepository) {

    @Scheduled(cron = "\${event.producer.cron}")
    fun produce() {
        val newEvents = eventRepository.getAllByStatus(EventStatus.NEW)
        newEvents.forEach { event ->
            try {
                jmsTemplate.send("events") { session ->
                    session.createObjectMessage(event.id)
                }
                eventRepository.updateStatus(event.id, EventStatus.NEW, EventStatus.IN_PROCESS)
            } catch (e: Exception) {
                eventRepository.updateStatus(event.id, EventStatus.ERROR)
                throw e
            }
        }
    }
}
