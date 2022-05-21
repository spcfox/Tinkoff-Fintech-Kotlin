package ru.tinkoff.fintech.homework.lesson13.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional
import ru.tinkoff.fintech.homework.lesson13.model.Event
import ru.tinkoff.fintech.homework.lesson13.model.EventStatus

interface EventJpaRepository : JpaRepository<Event, Int> {
    fun getAllByStatus(status: EventStatus): List<Event>

    @Transactional
    @Modifying
    @Query("update Event e set e.status = :status where e.id = :id")
    fun updateStatus(@Param("id") id: Int, @Param("status") status: EventStatus)

    @Transactional
    @Modifying
    @Query("update Event e set e.status = :status where e.id = :id and e.status = :oldStatus")
    fun updateStatus(@Param("id") id: Int, @Param("oldStatus") oldStatus: EventStatus, @Param("status") status: EventStatus)
}