package ru.tinkoff.fintech.homework.lesson13.service

import org.springframework.stereotype.Service
import ru.tinkoff.fintech.homework.lesson13.model.Event
import ru.tinkoff.fintech.homework.lesson13.model.EventStatus
import ru.tinkoff.fintech.homework.lesson13.model.EventType
import ru.tinkoff.fintech.homework.lesson13.repository.EventJpaRepository

@Service
class NotificationService(val eventRepository: EventJpaRepository) {

    fun newSms(body: String) = newEvent(body, EventType.SMS)

    fun newEmail(body: String) = newEvent(body, EventType.EMAIL)

    fun newPush(body: String) = newEvent(body, EventType.PUSH)

    private fun newEvent(body: String, type: EventType): Event {
        val event = Event(type = type, status = EventStatus.NEW, body = body)
        return eventRepository.save(event)
    }
}