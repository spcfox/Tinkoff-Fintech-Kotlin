package ru.tinkoff.fintech.homework.lesson7.phonebook.service

import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Service
import ru.tinkoff.fintech.homework.lesson7.phonebook.model.Contact
import ru.tinkoff.fintech.homework.lesson7.phonebook.model.ContactInfo
import java.sql.Statement

@Service
class JdbcContactsRepository(private val jdbcTemplate: JdbcTemplate) : ContactsRepository {
    companion object {
        private val rowMapper = RowMapper { rs, _ ->
            Contact(
                id = rs.getInt("contact_id"),
                firstName = rs.getString("first_name"),
                lastName = rs.getString("last_name"),
                phone = rs.getString("phone")
            )
        }
    }

    override fun getContacts(page: Int, size: Int): List<Contact> = jdbcTemplate.query(
        "select * from contacts order by contact_id offset ? rows fetch next ? rows only",
        rowMapper,
        page * size,
        size
    )

    override fun getContact(contactId: Int): Contact? = try {
         jdbcTemplate.queryForObject("select * from contacts where contact_id = ?", rowMapper, contactId)
    } catch (e: EmptyResultDataAccessException) {
        null
    }

    override fun addContact(contactInfo: ContactInfo): Contact {
        val keyHolder = GeneratedKeyHolder()
        jdbcTemplate.update(
            { connection ->
                connection
                    .prepareStatement(
                        "insert into contacts(first_name, last_name, phone) values (?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                    ).apply {
                        setString(1, contactInfo.firstName)
                        setString(2, contactInfo.lastName)
                        setString(3, contactInfo.phone)
                    }
            },
            keyHolder
        )
        println(keyHolder)
        return keyHolder.key?.let { Contact(it.toInt(), contactInfo) }
            ?: throw IllegalStateException("Contact not inserted")
    }

    override fun find(query: String, page: Int, size: Int): List<Contact> {
        val template = "%${query.lowercase()}%"
        return jdbcTemplate.query(
            "select * from contacts" +
                    " where lower(first_name) like ? or lower(last_name) like ? or phone like ?" +
                    "order by contact_id offset ? rows fetch next ? rows only",
            rowMapper,
            template,
            template,
            template,
            page * size,
            size
        )
    }
}
