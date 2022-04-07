package ru.tinkoff.fintech.homework.lesson6.phonebook.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.*
import ru.tinkoff.fintech.homework.lesson6.phonebook.model.Contact
import ru.tinkoff.fintech.homework.lesson6.phonebook.model.ContactInfo

@Service
class ContactsClient(
    private val restTemplate: RestTemplate,
    @Value("phone.book.address") private val phoneBookAddress: String
) {
    fun getContacts(page: Int, size: Int): List<Contact> =
        restTemplate.getForObject("$phoneBookAddress$GET_CONTACTS", page, size)

    fun getContact(contactId: Int): Contact? = try {
        restTemplate.getForObject("$phoneBookAddress$GET_CONTACT_BY_ID", contactId)
    } catch (e: HttpClientErrorException.NotFound) {
        null
    }

    fun addContact(contactInfo: ContactInfo): Contact =
        restTemplate.postForObject("$phoneBookAddress$ADD_CONTACT", contactInfo)

    fun find(query: String, page: Int, size: Int): List<Contact> =
        restTemplate.postForObject("$phoneBookAddress$FIND", query, page, size)
}

private const val GET_CONTACTS = "/contacts"
private const val GET_CONTACT_BY_ID = "/contacts"
private const val ADD_CONTACT = "/contact/add"
private const val FIND = "/contact/find"
