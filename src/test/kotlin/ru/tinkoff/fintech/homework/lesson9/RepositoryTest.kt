package ru.tinkoff.fintech.homework.lesson9

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.jdbc.core.JdbcTemplate
import ru.tinkoff.fintech.homework.lesson9.myusersapp.repository.ImagesRepository
import ru.tinkoff.fintech.homework.lesson9.myusersapp.repository.UsersRepository
import javax.sql.DataSource

@JdbcTest
class RepositoryTest @Autowired constructor(ds: DataSource) {
    private val usersRepository = UsersRepository(JdbcTemplate(ds))
    private val imagesRepository = ImagesRepository(JdbcTemplate(ds))

    @Test
    fun `пользователь с несуществующим id`() {
        val userId = -1

        val user = usersRepository.getUser(userId)

        assertNull(user)
    }

    @Test
    fun `добавление и получение пользователя`() {
        val username = "name"

        val userId = usersRepository.addUser(username)
        val user = usersRepository.getUser(userId)

        assertAll(
            { assertEquals(userId, user?.id) },
            { assertEquals(username, user?.username) }
        )
    }

    @Test
    fun `изображение с несуществующим id`() {
        val imageId = -1

        val image = imagesRepository.getImage(imageId)

        assertNull(image)
    }

    @Test
    fun `добавление изображения`() {
        val imageData = byteArrayOf(1, 2, 3)

        val imageId = imagesRepository.addImage(imageData)
        val image = imagesRepository.getImage(imageId)

        assertAll(
            { assertEquals(image?.id, imageId) },
            { assertTrue(imageData.contentEquals(image?.data)) }
        )
    }

    @Test
    fun `установить пользователю несуществующее изображение`() {
        val username = "name"
        val imageId = -1

        val userId = usersRepository.addUser(username)

        assertThrows(DataIntegrityViolationException::class.java) { usersRepository.setImage(userId, imageId) }
    }

    @Test
    fun `установить изображение несуществующему пользователю`() {
        val userId = -1
        val imageData = byteArrayOf(1, 2, 3)

        val imageId = imagesRepository.addImage(imageData)

        assertThrows(IllegalStateException::class.java) { usersRepository.setImage(userId, imageId) }
    }

    @Test
    fun `установить изображение`() {
        val username = "username"
        val imageData = byteArrayOf(1, 2, 3)

        val userId = usersRepository.addUser(username)
        val imageId = imagesRepository.addImage(imageData)

        val imageBefore = imagesRepository.getImageByUserId(userId)
        usersRepository.setImage(userId, imageId)
        val imageAfter = imagesRepository.getImageByUserId(userId)

        assertAll(
            { assertNull(imageBefore) },
            { assertTrue(imageData.contentEquals(imageAfter?.data)) }
        )
    }

    @Test
    fun `изменить изображение`() {
        val username = "username"
        val imageData1 = byteArrayOf(1, 2, 3)
        val imageData2 = byteArrayOf(4, 5, 6)

        val userId = usersRepository.addUser(username)
        val imageId1 = imagesRepository.addImage(imageData1)
        val imageId2 = imagesRepository.addImage(imageData2)

        val imageBefore = imagesRepository.getImageByUserId(userId)
        usersRepository.setImage(userId, imageId1)
        val image1 = imagesRepository.getImageByUserId(userId)
        usersRepository.setImage(userId, imageId2)
        val image2 = imagesRepository.getImageByUserId(userId)

        assertAll(
            { assertNull(imageBefore) },
            { assertTrue(imageData1.contentEquals(image1?.data)) },
            { assertTrue(imageData2.contentEquals(image2?.data)) }
        )
    }
}