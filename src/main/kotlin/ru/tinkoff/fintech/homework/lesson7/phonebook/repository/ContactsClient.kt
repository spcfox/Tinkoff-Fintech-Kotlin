package ru.tinkoff.fintech.homework.lesson7.phonebook.repository

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.*
import ru.tinkoff.fintech.homework.lesson7.configuration.properties.ContactsClientProperties
import ru.tinkoff.fintech.homework.lesson7.phonebook.model.Contact
import ru.tinkoff.fintech.homework.lesson7.phonebook.model.ContactInfo

@Service
class ContactsClient(
    private val restTemplate: RestTemplate,
    private val properties: ContactsClientProperties
) : ContactsRepository {
    override fun getContacts(page: Int, size: Int): List<Contact> =
        restTemplate.getForObject(properties.get, mapOf("page" to page, "size" to size))

    override fun getContact(contactId: Int): Contact? = try {
        restTemplate.getForObject(properties.getById, mapOf("contactId" to contactId))
    } catch (e: HttpClientErrorException.NotFound) {
        null
    }

    override fun addContact(contactInfo: ContactInfo): Contact =
        restTemplate.postForObject(properties.add, mapOf("contactInfo" to contactInfo))

    override fun find(query: String, page: Int, size: Int): List<Contact> =
        restTemplate.postForObject(properties.find, mapOf("query" to query, "page" to page, "size" to size))
}
