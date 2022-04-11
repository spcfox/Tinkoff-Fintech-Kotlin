package ru.tinkoff.fintech.homework.lesson7.phonebook.repository

import org.springframework.stereotype.Service
import ru.tinkoff.fintech.homework.lesson7.phonebook.model.Contact
import ru.tinkoff.fintech.homework.lesson7.phonebook.model.ContactInfo

@Service
interface ContactsRepository {
    fun getContacts(page: Int, size: Int): List<Contact>

    fun getContact(contactId: Int): Contact?

    fun addContact(contactInfo: ContactInfo): Contact

    fun find(query: String, page: Int, size: Int): List<Contact>
}
