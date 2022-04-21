package ru.tinkoff.fintech.homework.lesson9.myusersapp.repository

import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Service
import ru.tinkoff.fintech.homework.lesson9.myusersapp.model.User
import ru.tinkoff.fintech.homework.lesson9.myusersapp.model.UserImage
import java.sql.Statement

@Service
class UsersRepository(private val jdbcTemplate: JdbcTemplate)  {
    companion object {
        private val rowMapper = RowMapper { rs, _ ->
            User(
                id = rs.getInt("user_id"),
                username = rs.getString("username"),
                imageId = rs.getInt("image_id")
            )
        }

        private const val getUserQuery = "select * from users where user_id = ?"
        private const val setImageQuery = "update users set image_id = ? where user_id = ?"
        private const val addUserQuery = "insert into users(username) values (?)"
    }

    fun getUser(userId: Int): User? = try {
        jdbcTemplate.queryForObject(getUserQuery, rowMapper, userId)
    } catch (e: EmptyResultDataAccessException) {
        null
    }

    fun setImage(userId: Int, imageId: Int) {
        val updated = jdbcTemplate.update(setImageQuery, imageId, userId)
        check(updated == 1) { "Expected 1 line update but updated $updated" }
    }

    fun addUser(username: String): Int {
        val keyHolder = GeneratedKeyHolder()
        jdbcTemplate.update(
            { connection ->
                connection.prepareStatement(addUserQuery, Statement.RETURN_GENERATED_KEYS).apply {
                    setString(1, username)
                }
            },
            keyHolder
        )
        return keyHolder.key?.toInt() ?: throw IllegalArgumentException("User was not inserted")
    }
}