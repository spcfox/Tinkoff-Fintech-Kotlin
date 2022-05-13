package ru.tinkoff.fintech.homework.lesson13.model

import javax.persistence.*

@Entity
@Table(name = "events")
class Event(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    var id: Int = 0,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    var type: EventType,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: EventStatus,

    @Column(name = "body", nullable = false)
    var body: String,
)

enum class EventType {
    SMS, EMAIL, PUSH
}

enum class EventStatus {
    NEW, IN_PROCESS, DONE, ERROR
}