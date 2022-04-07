package ru.tinkoff.fintech.homework.lesson7.phonebook.service

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import ru.tinkoff.fintech.homework.lesson7.phonebook.model.Contact

interface ContactDao : JpaRepository<Contact, Int> {
    fun findByFirstNameContainsIgnoreCaseOrLastNameContainsIgnoreCaseOrPhoneContains
                (firstName: String, lastName: String, phone: String, pageable: Pageable): List<Contact>
}
