package ru.tinkoff.fintech.homework.lesson7.tests

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.jdbc.core.JdbcTemplate
import ru.tinkoff.fintech.homework.lesson7.phonebook.model.Contact
import ru.tinkoff.fintech.homework.lesson7.phonebook.model.ContactInfo
import ru.tinkoff.fintech.homework.lesson7.phonebook.repository.JdbcContactsRepository
import javax.sql.DataSource

@JdbcTest
class JdbcContactRepositoryTest @Autowired constructor(ds: DataSource) {
    private val repository = JdbcContactsRepository(JdbcTemplate(ds))

    private val contactsList = listOf(
        Contact(1, "Иван", "Сергеев", "+79991234567"),
        Contact(2, "Ольга", "Шемякина", "+79113442312"),
        Contact(3, "Сергей", "Ландау", "+79591236812"),
        Contact(4, "Иван", "Носков", "+79235321342"),
        Contact(5, "Афанасий", "Беспалов", "+79194364845"),
        Contact(6, "Никита", "Алексеев", "+7932504358"),
        Contact(7, "112", "", "112"),
        Contact(8, "Олег", "Галкин", "+79750123712"),
        Contact(9, "Наталья", "Алексеева", "+79012003765"),
        Contact(10, "Мария", "Сергеева", "+79671243201"),
        Contact(11, "Заказ пиццы", "", "+78003020060"),
    )

    @Test
    fun `получить все контакты`() {
        val contacts: List<Contact> = repository.getContacts(page = 0, size = 100)

        assertEquals(contactsList, contacts)
    }

    @Test
    fun `получить первые 3 контакт`() {
        val contacts: List<Contact> = repository.getContacts(page = 0, size = 3)

        assertEquals(contactsList.subList(0, 3), contacts)
    }

    @Test
    fun `получить первые 3 контакта середины`() {
        val contacts: List<Contact> = repository.getContacts(page = 2, size = 3)

        assertEquals(contactsList.subList(6, 9), contacts)
    }

    @Test
    fun `запрос несуществующей страницы`() {
        val contacts: List<Contact> = repository.getContacts(page = 100, size = 5)

        assertEquals(emptyList<Contact>(), contacts)
    }

    @Test
    fun `получить контакт по id`() {
        val contact: Contact? = repository.getContact(contactId = 7)

        assertEquals(Contact(7, "112", "", "112"), contact)
    }

    @Test
    fun `получить с несуществующим id`() {
        val contact: Contact? = repository.getContact(contactId = 15)

        assertNull(contact)
    }

    @Test
    fun `добавление контакта`() {
        val contactInfo = ContactInfo("Имя", "Фамилия", "+79123754432")

        val contact: Contact = repository.addContact(contactInfo = contactInfo)
        val contactById: Contact? = repository.getContact(contact.id)

        assertEquals(contact, contactById)
    }

    @Test
    fun `поиск контакта по имени`() {
        val contacts: List<Contact> = repository.find(query = "Никита", page = 0, size = 5)

        assertEquals(
            listOf(
                Contact(6, "Никита", "Алексеев", "+7932504358")
            ),
            contacts
        )
    }

    @Test
    fun `поиск с совпадениями в разных полях`() {
        val contacts: List<Contact> = repository.find(query = "еРГе", page = 0, size = 5)

        assertEquals(
            listOf(
                Contact(1, "Иван", "Сергеев", "+79991234567"),
                Contact(3, "Сергей", "Ландау", "+79591236812"),
                Contact(10, "Мария", "Сергеева", "+79671243201"),
            ),
            contacts
        )
    }

    @Test
    fun `поиск контакта по цифрам`() {
        val contacts: List<Contact> = repository.find(query = "11", page = 0, size = 5)

        assertEquals(
            listOf(
                Contact(2, "Ольга", "Шемякина", "+79113442312"),
                Contact(7, "112", "", "112"),
            ),
            contacts
        )
    }

    @Test
    fun `поиск с паджинацией`() {
        val contacts: List<Contact> = repository.find(query = "А", page = 2, size = 4)

        assertEquals(
            listOf(
                Contact(10, "Мария", "Сергеева", "+79671243201"),
                Contact(11, "Заказ пиццы", "", "+78003020060"),
            ),
            contacts
        )
    }
}
