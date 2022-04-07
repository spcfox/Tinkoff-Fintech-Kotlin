package ru.tinkoff.fintech.homework.lesson7.phonebook

import org.springframework.web.bind.annotation.*
import ru.tinkoff.fintech.homework.lesson7.phonebook.model.Contact
import ru.tinkoff.fintech.homework.lesson7.phonebook.model.ContactInfo
import ru.tinkoff.fintech.homework.lesson7.phonebook.service.ContactsService

@RestController
@RequestMapping("/book")
class PhoneBook(private val contactService: ContactsService) {
    @GetMapping("/contacts")
    fun getContacts(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "5") size: Int
    ): List<Contact> =
        contactService.getContacts(page, size)

    @GetMapping("/contact/{contactId}")
    fun getContact(@PathVariable contactId: Int): Contact =
        contactService.getContact(contactId)

    @PostMapping("/add")
    fun addContact(@RequestBody contactInfo: ContactInfo): Contact =
        contactService.addContact(contactInfo)

    @GetMapping("find")
    fun findContact(
        @RequestParam query: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "5") size: Int
    ): List<Contact> =
        contactService.findContact(query, page, size)
}