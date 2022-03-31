package ru.tinkoff.fintech.homework.lesson6.phonebook.model

data class Contact(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val phone: String,
) {
    constructor(id: Int, contactInfo: ContactInfo) : this(
        id,
        contactInfo.firstName,
        contactInfo.lastName,
        contactInfo.phone
    )
}

data class ContactInfo(
    val firstName: String,
    val lastName: String,
    val phone: String,
)