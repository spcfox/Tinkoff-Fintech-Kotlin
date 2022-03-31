package ru.tinkoff.fintech.homework.lesson6.tests

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ninjasquad.springmockk.MockkBean
import io.mockk.clearAllMocks
import io.mockk.every
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.util.LinkedMultiValueMap
import ru.tinkoff.fintech.homework.lesson6.phonebook.model.Contact
import ru.tinkoff.fintech.homework.lesson6.phonebook.model.ContactInfo
import ru.tinkoff.fintech.homework.lesson6.phonebook.service.PhoneBookClient
import kotlin.math.min

@SpringBootTest
@AutoConfigureMockMvc
class PhoneBookTest @Autowired constructor(private val mockMvc: MockMvc, private val objectMapper: ObjectMapper) {
    @MockkBean
    private lateinit var phoneBookClient: PhoneBookClient

    @BeforeEach
    private fun initMock() {
        every { phoneBookClient.getContacts(any(), any()) } answers {
            val n = contactsList.size
            val page = firstArg<Int>()
            val size = secondArg<Int>()
            val from = min(n, page * size)
            val to = min(n, from + size)
            println("page: $page")
            println("size: $size")
            contactsList.subList(from, to)
        }
        every { phoneBookClient.getContact(any()) } answers { contactsList.find { it.id == firstArg() } }
        every { phoneBookClient.addContact(any()) } answers {
            val id = contactsList.maxOf { it.id } + 1
            val contact = Contact(id, firstArg())
            contactsList.add(contact)
            contact
        }
        every { phoneBookClient.find(any(), any(), any()) } answers {
            val query = firstArg<String>().lowercase()
            val page = secondArg<Int>()
            val size = thirdArg<Int>()
            contactsList
                .filter { query in it.firstName.lowercase() || query in it.lastName.lowercase() || query in it.phone }
                .let {
                    val n = it.size
                    val from = min(n, page * size)
                    val to = min(n, from + size)
                    it.subList(from, to)
                }
        }
    }

    @AfterEach
    private fun clearMock() {
        clearAllMocks()
    }

    @Test
    fun `получить все контакты`() {
        val contacts: List<Contact> = getContacts(page = 0, size = 100).readResponse()

        assertEquals(contactsList, contacts)
    }

    @Test
    fun `получить первые 3 контакт`() {
        val contacts: List<Contact> = getContacts(page = 0, size = 3).readResponse()

        assertEquals(contactsList.subList(0, 3), contacts)
    }

    @Test
    fun `получить первые 3 контакта середины`() {
        val contacts: List<Contact> = getContacts(page = 2, size = 3).readResponse()

        assertEquals(contactsList.subList(6, 9), contacts)
    }

    @Test
    fun `по умолчанию 1 страница`() {
        val contacts: List<Contact> = getContacts(size = 3).readResponse()

        assertEquals(contactsList.subList(0, 3), contacts)
    }

    @Test
    fun `по умолчанию 5 контактов на странице`() {
        val contacts: List<Contact> = getContacts().readResponse()

        assertEquals(contactsList.subList(0, 5), contacts)
    }

    @Test
    fun `запрос несуществующей страницы`() {
        val contacts: List<Contact> = getContacts(page = 100).readResponse()

        assertEquals(emptyList<Contact>(), contacts)
    }

    @Test
    fun `запрос отрицательной страницы`() {
        val response = getContacts(page = -1)

        response.is4xx()
    }

    @Test
    fun `запрос страницы с отрицательным размером`() {
        val response = getContacts(size = -1)

        response.is4xx()
    }

    @Test
    fun `запрос страницы с нулевым размером`() {
        val response = getContacts(size = 0)

        response.is4xx()
    }

    @Test
    fun `получить контакт по id`() {
        val contact: Contact = getContact(contactId = 8).readResponse()

        assertEquals(Contact(8, "112", "", "112"), contact)
    }

    @Test
    fun `получить с несуществующим id`() {
        val response = getContact(contactId = 1)

        response.is4xx()
    }

    @Test
    fun `получить с отрицательным id`() {
        val response = getContact(contactId = -1)

        response.is4xx()
    }

    @Test
    fun `добавление контакта`() {
        val contactInfo = ContactInfo("Имя", "Фамилия", "+79123754432")

        val contact: Contact = addContact(contactInfo = contactInfo).readResponse()
        val contactById: Contact = getContact(contact.id).readResponse()

        assertEquals(contact, contactById)
    }

    @Test
    fun `поиск контакта по имени`() {
        val contacts: List<Contact> = findContact(query = "Никита").readResponse()

        assertEquals(
            listOf(
                Contact(7, "Никита", "Алексеев", "+7932504358")
            ),
            contacts
        )
    }

    @Test
    fun `поиск с совпадениями в разных полях`() {
        val contacts: List<Contact> = findContact(query = "еРГе").readResponse()

        assertEquals(
            listOf(
                Contact(0, "Иван", "Сергеев", "+79991234567"),
                Contact(3, "Сергей", "Ландау", "+79591236812"),
                Contact(12, "Мария", "Сергеева", "+79671243201"),
            ),
            contacts
        )
    }

    @Test
    fun `поиск контакта по цифрам`() {
        val contacts: List<Contact> = findContact(query = "11").readResponse()

        assertEquals(
            listOf(
                Contact(2, "Ольга", "Шемякина", "+79113442312"),
                Contact(8, "112", "", "112"),
            ),
            contacts
        )
    }

    @Test
    fun `поиск с паджинацией`() {
        val contacts: List<Contact> = findContact(query = "А", page = 2, size = 4).readResponse()

        assertEquals(
            listOf(
                Contact(12, "Мария", "Сергеева", "+79671243201"),
                Contact(14, "Заказ пиццы", "", "+78003020060"),
            ),
            contacts
        )
    }

    @Test
    fun `стандартные значения для паджинации в поиске`() {
        val contacts: List<Contact> = findContact(query = "А").readResponse()

        assertEquals(
            listOf(
                Contact(0, "Иван", "Сергеев", "+79991234567"),
                Contact(2, "Ольга", "Шемякина", "+79113442312"),
                Contact(3, "Сергей", "Ландау", "+79591236812"),
                Contact(5, "Иван", "Носков", "+79235321342"),
                Contact(6, "Афанасий", "Беспалов", "+79194364845"),
            ),
            contacts
        )
    }

    @Test
    fun `поиск по пустой строке`() {
        val response = findContact("    ")

        response.is4xx()
    }

    @Test
    fun `поиск с отрицательным номером строки страницы`() {
        val response = findContact("Иван", page = -1)

        response.is4xx()
    }

    @Test
    fun `поиск с отрицательным номером размером страницы`() {
        val response = findContact("Иван", size = -1)

        response.is4xx()
    }

    @Test
    fun `поиск с отрицательным номером нулевым страницы`() {
        val response = findContact("Иван", size = 0)

        response.is4xx()
    }

    fun getContacts(page: Int? = null, size: Int? = null): ResultActions =
        mockMvc.perform(
            get("/book/contacts")
                .params(createParams("page" to page, "size" to size))
        )

    fun getContact(contactId: Int): ResultActions =
        mockMvc.perform(get("/book/contact/$contactId"))

    fun addContact(contactInfo: ContactInfo): ResultActions =
        mockMvc.perform(
            post("/book/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contactInfo))
        )

    fun findContact(query: String, page: Int? = null, size: Int? = null): ResultActions =
        mockMvc.perform(
            get("/book/find")
                .params(createParams("query" to query, "page" to page, "size" to size))
        )

    private fun ResultActions.is4xx() = andExpect(status().is4xxClientError)

    private fun ResultActions.is5xx() = andExpect(status().is5xxServerError)

    private inline fun <reified T> ResultActions.readResponse(): T = this
        .andExpect(status().isOk)
        .andReturn().response.getContentAsString(Charsets.UTF_8)
        .let { if (T::class == String::class) it as T else objectMapper.readValue(it) }

    private fun createParams(vararg params: Pair<String, Any?>) =
        LinkedMultiValueMap<String, String>().apply {
            params.forEach { (key, value) -> if (value != null) add(key, value.toString()) }
        }

    private val contactsList = mutableListOf(
        Contact(0, "Иван", "Сергеев", "+79991234567"),
        Contact(2, "Ольга", "Шемякина", "+79113442312"),
        Contact(3, "Сергей", "Ландау", "+79591236812"),
        Contact(5, "Иван", "Носков", "+79235321342"),
        Contact(6, "Афанасий", "Беспалов", "+79194364845"),
        Contact(7, "Никита", "Алексеев", "+7932504358"),
        Contact(8, "112", "", "112"),
        Contact(10, "Олег", "Глакин", "+79750123712"),
        Contact(11, "Наталья", "Алексеева", "+79012003765"),
        Contact(12, "Мария", "Сергеева", "+79671243201"),
        Contact(14, "Заказ пиццы", "", "+78003020060"),
    )
}
