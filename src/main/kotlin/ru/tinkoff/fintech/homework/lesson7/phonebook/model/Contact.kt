package ru.tinkoff.fintech.homework.lesson7.phonebook.model

import com.sun.istack.NotNull
import javax.persistence.*

@Table(name = "contacts")
@Entity
data class Contact(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contact_id")
    var id: Int = -1,

    @NotNull
    @Column(name = "first_name")
    val firstName: String? = null,

    @NotNull
    @Column(name = "last_name")
    val lastName: String? = null,

    @NotNull
    @Column(name = "phone")
    val phone: String? = null,
) {
    constructor(id: Int, contactInfo: ContactInfo) : this(
        id = id,
        firstName = contactInfo.firstName,
        lastName = contactInfo.lastName,
        phone = contactInfo.phone
    )

    constructor(contactInfo: ContactInfo) : this(
        firstName = contactInfo.firstName,
        lastName = contactInfo.lastName,
        phone = contactInfo.phone
    )
}

data class ContactInfo(
    val firstName: String,
    val lastName: String,
    val phone: String,
)