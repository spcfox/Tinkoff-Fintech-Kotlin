package ru.tinkoff.fintech.homework.lesson9

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.delay
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import com.ninjasquad.springmockk.MockkBean
import io.mockk.*
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.util.LinkedMultiValueMap
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import ru.tinkoff.fintech.homework.lesson9.myusersapp.client.UnsplashClient
import ru.tinkoff.fintech.homework.lesson9.myusersapp.model.User
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
internal class ControllerTest @Autowired constructor(private val mockMvc: MockMvc, private val objectMapper: ObjectMapper) {
    @MockkBean
    private lateinit var unsplashClient: UnsplashClient

    private val random = Random(883)

    @BeforeEach
    fun initMock() {
        val imageSize = 100
        val delayTime = 100L

        coEvery { unsplashClient.getRandomImage(any(), any()) } coAnswers {
            delay(delayTime)
            ByteArray(imageSize).apply {
                random.nextBytes(this)
            }
        }
    }

    @AfterEach
    private fun clearMock() {
        clearAllMocks()
    }

    @Test
    fun `несуществующий пользователь`() {
        val userId = -1

        val response: ResultActions = getUser(userId)

        response.is4xx()
    }

    @Test
    fun `добавление пользователя`() {
        val username = "name"

        val userId: Int = newUser(username).readResponse()
        val user: User = getUser(userId).readResponse()

        Assertions.assertAll(
            { assertEquals(userId, user.id) },
            { assertEquals(username, user.username) }
        )
    }

    @Test
    fun `генерация аватарки при добавлении`() {
        val username = "name"

        val userId: Int = newUser(username).readResponse()
        val imageBefore: ResultActions = getUserImage(userId)
        Thread.sleep(200L)
        val user: User = getUser(userId).readResponse()
        val imageByUserId: ByteArray = getUserImage(userId).readByteArray()
        val imageByImageId: ByteArray = getImage(user.imageId).readByteArray()

        imageBefore.is4xx()
        coVerify(exactly = 1) { unsplashClient.getRandomImage(any(), any()) }
        assertTrue(imageByUserId.contentEquals(imageByImageId))
    }

    @Test
    fun `генерация новой аватарки`() {
        val username = "name"

        val userId: Int = newUser(username).readResponse()
        val imageBefore: ResultActions = getUserImage(userId)
        Thread.sleep(200L)
        val user1: User = getUser(userId).readResponse()
        val imageByUserId1: ByteArray = getUserImage(userId).readByteArray()
        val imageByImageId1: ByteArray = getImage(user1.imageId).readByteArray()

        newRandomImage(userId)
        Thread.sleep(200L)
        val user2: User = getUser(userId).readResponse()
        val imageByUserId2: ByteArray = getUserImage(userId).readByteArray()
        val imageByImageId2: ByteArray = getImage(user2.imageId).readByteArray()

        imageBefore.is4xx()
        coVerify(exactly = 2) { unsplashClient.getRandomImage(any(), any()) }
        assertAll(
            { assertEquals(user1.id, user2.id) },
            { assertEquals(user1.username, user2.username) },
            { assertNotEquals(user1.imageId, user2.imageId) },
            { assertTrue(imageByUserId1.contentEquals(imageByImageId1)) },
            { assertTrue(imageByUserId2.contentEquals(imageByImageId2)) },
            { assertFalse(imageByUserId1.contentEquals(imageByUserId2)) }
        )
    }

    @Test
    fun `генерация аватарки несуществующему пользователю`() {
        val userId = -1

        val response: ResultActions = newRandomImage(userId)

        response.is4xx()
        coVerify(exactly = 0) { unsplashClient.getRandomImage(any(), any()) }
    }

    @Test
    fun `установка несуществующей аватарки`() {
        val username = "name"
        val imageId = -1

        val userId: Int = newUser(username).readResponse()
        Thread.sleep(200L)
        val response: ResultActions = setImage(userId, imageId)

        response.is4xx()
        coVerify(exactly = 1) { unsplashClient.getRandomImage(any(), any()) }
    }

    @Test
    fun `установка аватарки несуществующему пользователю`() {
        val username = "name"
        val userId = -1

        val newUserId: Int = newUser(username).readResponse()
        Thread.sleep(200L)
        val user: User = getUser(newUserId).readResponse()
        val response: ResultActions = setImage(userId, user.imageId)

        response.is4xx()
        coVerify(exactly = 1) { unsplashClient.getRandomImage(any(), any()) }
    }

    @Test
    fun `изменение аватарки на предыдущую`() {
        val username = "name"

        val userId: Int = newUser(username).readResponse()
        val imageBefore: ResultActions = getUserImage(userId)
        Thread.sleep(200L)
        val user1: User = getUser(userId).readResponse()
        val imageByUserId1: ByteArray = getUserImage(userId).readByteArray()
        val imageByImageId1: ByteArray = getImage(user1.imageId).readByteArray()

        newRandomImage(userId)
        Thread.sleep(200L)
        val user2: User = getUser(userId).readResponse()
        val imageByUserId2: ByteArray = getUserImage(userId).readByteArray()
        val imageByImageId2: ByteArray = getImage(user2.imageId).readByteArray()

        setImage(userId, user1.imageId)
        val user3: User = getUser(userId).readResponse()
        val imageByUserId3: ByteArray = getUserImage(userId).readByteArray()
        val imageByImageId3: ByteArray = getImage(user3.imageId).readByteArray()

        imageBefore.is4xx()
        coVerify(exactly = 2) { unsplashClient.getRandomImage(any(), any()) }
        assertAll(
            { assertEquals(user1.id, user2.id) },
            { assertEquals(user1.id, user3.id) },
            { assertEquals(user1.username, user2.username) },
            { assertEquals(user1.username, user3.username) },
            { assertNotEquals(user1.imageId, user2.imageId) },
            { assertEquals(user1.imageId, user3.imageId) },
            { assertTrue(imageByUserId1.contentEquals(imageByImageId1)) },
            { assertTrue(imageByUserId2.contentEquals(imageByImageId2)) },
            { assertTrue(imageByUserId3.contentEquals(imageByImageId3)) },
            { assertTrue(imageByUserId1.contentEquals(imageByImageId3)) },
            { assertFalse(imageByUserId1.contentEquals(imageByUserId2)) }
        )
    }

    @Test
    fun `обмен аватарками`() {
        val username1 = "user"
        val username2 = "name"

        val user1Id: Int = newUser(username1).readResponse()
        val user2Id: Int = newUser(username2).readResponse()

        Thread.sleep(200L)

        val user1Before: User = getUser(user1Id).readResponse()
        val image1ByUserIdBefore: ByteArray = getUserImage(user1Id).readByteArray()
        val image1ByImageIdBefore: ByteArray = getImage(user1Before.imageId).readByteArray()
        val user2Before: User = getUser(user2Id).readResponse()
        val image2ByUserIdBefore: ByteArray = getUserImage(user2Id).readByteArray()
        val image2ByImageIdBefore: ByteArray = getImage(user2Before.imageId).readByteArray()

        setImage(user1Id, user2Before.imageId)
        setImage(user2Id, user1Before.imageId)

        val user1After: User = getUser(user1Id).readResponse()
        val image1ByUserIdAfter: ByteArray = getUserImage(user1Id).readByteArray()
        val image1ByImageIdAfter: ByteArray = getImage(user1After.imageId).readByteArray()
        val user2After: User = getUser(user2Id).readResponse()
        val image2ByUserIdAfter: ByteArray = getUserImage(user2Id).readByteArray()
        val image2ByImageIdAfter: ByteArray = getImage(user2After.imageId).readByteArray()

        coVerify(exactly = 2) { unsplashClient.getRandomImage(any(), any()) }
        assertAll(
            { assertNotEquals(user1Before.id, user2Before.id) },
            { assertEquals(user1Before.id, user1After.id) },
            { assertEquals(username1, user1Before.username) },
            { assertEquals(username1, user1After.username) },
            { assertEquals(user2Before.id, user2After.id) },
            { assertEquals(username2, user2Before.username) },
            { assertEquals(username2, user2After.username) },

            { assertTrue(image1ByUserIdBefore.contentEquals(image1ByImageIdBefore)) },
            { assertTrue(image1ByUserIdAfter.contentEquals(image1ByImageIdAfter)) },
            { assertTrue(image2ByUserIdBefore.contentEquals(image2ByImageIdBefore)) },
            { assertTrue(image2ByUserIdAfter.contentEquals(image2ByImageIdAfter)) },
            { assertFalse(image1ByImageIdBefore.contentEquals(image2ByImageIdBefore)) },
            { assertFalse(image1ByImageIdAfter.contentEquals(image2ByImageIdAfter)) },
            { assertTrue(image1ByImageIdBefore.contentEquals(image2ByImageIdAfter)) },
            { assertTrue(image1ByImageIdAfter.contentEquals(image2ByImageIdBefore)) },
        )
    }

    private fun getUser(userId: Int): ResultActions =
        mockMvc.perform(get("/users/$userId"))

    private fun getUserImage(userId: Int): ResultActions =
        mockMvc.perform(
            get("/users/user-image/$userId")
                .contentType(MediaType.IMAGE_JPEG)
        )

    private fun getImage(imageId: Int): ResultActions =
        mockMvc.perform(
            get("/users/image/$imageId")
                .contentType(MediaType.IMAGE_JPEG)
        )

    private fun newUser(username: String): ResultActions =
        mockMvc.perform(
            post("/users/new")
                .params(createParams("username" to username))
        )

    private fun newRandomImage(userId: Int): ResultActions =
        mockMvc.perform(
            post("/users/new-image")
                .params(createParams("userId" to userId))
        )

    private fun setImage(userId: Int, imageId: Int): ResultActions =
        mockMvc.perform(
            post("/users/set-image")
                .params(createParams("userId" to userId, "imageId" to imageId))
        )

    private fun ResultActions.is4xx() = andExpect(status().is4xxClientError)

    private inline fun <reified T> ResultActions.readResponse(): T = this
        .andExpect(status().isOk)
        .andReturn().response.getContentAsString(Charsets.UTF_8)
        .let { if (T::class == String::class) it as T else objectMapper.readValue(it, T::class.java) }

    private fun ResultActions.readByteArray(): ByteArray = this
        .andExpect(status().isOk)
        .andReturn().response.contentAsByteArray

    private fun createParams(vararg params: Pair<String, Any?>) =
        LinkedMultiValueMap<String, String>().apply {
            params.forEach { (key, value) -> if (value != null) add(key, value.toString()) }
        }
}
