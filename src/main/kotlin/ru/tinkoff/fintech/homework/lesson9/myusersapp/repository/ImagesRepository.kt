package ru.tinkoff.fintech.homework.lesson9.myusersapp.repository

import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Service
import ru.tinkoff.fintech.homework.lesson9.myusersapp.model.UserImage
import java.io.ByteArrayInputStream
import java.sql.Statement

@Service
class ImagesRepository(private val jdbcTemplate: JdbcTemplate)  {
    companion object {
        private val rowMapper = RowMapper { rs, _ ->
            UserImage(
                id = rs.getInt("image_id"),
                data = rs.getBytes("data")
            )
        }

        private const val getImageQuery = "select * from images where image_id = ?"
        private const val getImageByUserIdQuery =
            "select images.image_id, images.data from users inner join images on users.image_id = images.image_id where user_id = ?"
        private const val addImageQuery = "insert into images(data) values (?)"
    }

    fun getImage(imageId: Int): UserImage? = try {
        jdbcTemplate.queryForObject(getImageQuery, rowMapper, imageId)
    } catch (e: EmptyResultDataAccessException) {
        null
    }

    fun getImageByUserId(userId: Int): UserImage? = try {
        jdbcTemplate.queryForObject(getImageByUserIdQuery, rowMapper, userId)
    } catch (e: EmptyResultDataAccessException) {
        null
    }

    fun addImage(imageData: ByteArray): Int {
        val keyHolder = GeneratedKeyHolder()
        jdbcTemplate.update(
            { connection ->
                connection.prepareStatement(addImageQuery, Statement.RETURN_GENERATED_KEYS).apply {
                    setBlob(1, ByteArrayInputStream(imageData), imageData.size.toLong())
                }
            },
            keyHolder
        )
        return keyHolder.key?.toInt() ?: throw IllegalArgumentException("User was not inserted")
    }
}
