package ru.tinkoff.fintech.homework.lesson7.phonebook.service

import org.springframework.context.annotation.Primary
import org.springframework.data.domain.PageRequest
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException
import org.springframework.stereotype.Service
import ru.tinkoff.fintech.homework.lesson7.phonebook.model.Contact
import ru.tinkoff.fintech.homework.lesson7.phonebook.model.ContactInfo

@Primary
@Service
class JpaContactsRepository(private val dao: ContactDao): ContactsRepository {
    override fun getContacts(page: Int, size: Int): List<Contact> =
        dao.findAll(PageRequest.of(page, size)).toList()

    override fun getContact(contactId: Int): Contact? = try {
        dao.getById(contactId)
    } catch (e: JpaObjectRetrievalFailureException) {
        null
    }

    override fun addContact(contactInfo: ContactInfo): Contact =
        dao.saveAndFlush(Contact(contactInfo))

    override fun find(query: String, page: Int, size: Int): List<Contact> =
        dao.findByFirstNameContainsIgnoreCaseOrLastNameContainsIgnoreCaseOrPhoneContains(query, query, query, PageRequest.of(page, size))
}
