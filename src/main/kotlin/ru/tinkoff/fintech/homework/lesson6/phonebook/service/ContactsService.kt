package ru.tinkoff.fintech.homework.lesson6.phonebook.service

import org.springframework.stereotype.Service
import ru.tinkoff.fintech.homework.lesson6.phonebook.model.Contact
import ru.tinkoff.fintech.homework.lesson6.phonebook.model.ContactInfo

@Service
class ContactsService(private val contactsClient: ContactsClient) {
    fun getContacts(page: Int, size: Int): List<Contact> {
        require(page >= 0 && size > 0)
        return contactsClient.getContacts(page, size)
    }

    fun getContact(contactId: Int): Contact {
        val contact = contactsClient.getContact(contactId)
        return requireNotNull(contact) { "Contact $contactId not found" }
    }

    fun addContact(contactInfo: ContactInfo): Contact =
        contactsClient.addContact(contactInfo)

    fun findContact(query: String, page: Int, size: Int): List<Contact> {
        require(query.isNotBlank() && page >= 0 && size > 0)
        return contactsClient.find(query, page, size)
    }
}