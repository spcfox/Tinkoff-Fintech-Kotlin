package ru.tinkoff.fintech.homework.lesson13.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import ru.tinkoff.fintech.homework.lesson13.model.Event
import ru.tinkoff.fintech.homework.lesson13.model.EventStatus
import javax.transaction.Transactional

interface EventJpaRepository : JpaRepository<Event, Int> {
    fun getAllByStatus(status: EventStatus): List<Event>

    @Transactional
    @Modifying
    @Query("update Event e set e.status = :status where e.id = :id")
    fun updateStatus(@Param("id") id: Int, @Param("status") status: EventStatus)
}