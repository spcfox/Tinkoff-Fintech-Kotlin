package ru.tinkoff.fintech.homework.lesson9.myusersapp.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import ru.tinkoff.fintech.homework.lesson9.myusersapp.client.UnsplashClient
import ru.tinkoff.fintech.homework.lesson9.myusersapp.model.User
import ru.tinkoff.fintech.homework.lesson9.myusersapp.repository.ImagesRepository
import ru.tinkoff.fintech.homework.lesson9.myusersapp.repository.UsersRepository

@Service
class UsersService(
    private val usersRepository: UsersRepository,
    private val imagesRepository: ImagesRepository,
    private val unsplashClient: UnsplashClient
) {
    fun getUser(userId: Int): User {
        val user = usersRepository.getUser(userId)
        return requireNotNull(user) { "User with id $userId not found" }
    }

    fun getUserImage(userId: Int): ByteArray {
        val image = imagesRepository.getImageByUserId(userId)
        return requireNotNull(image?.data) { "Image not found" }
    }

    fun getImage(imageId: Int): ByteArray {
        val image = imagesRepository.getImage(imageId)
        return requireNotNull(image?.data) { "Image with id $imageId not found" }
    }

    fun newUser(username: String): Int {
        val userId = usersRepository.addUser(username)
        CoroutineScope(Dispatchers.IO).launch {
            setRandomImage(userId)
        }
        return userId
    }

    fun newRandomImage(userId: Int) {
        val user = usersRepository.getUser(userId)
        requireNotNull(user) { "User with id $userId not found" }
        CoroutineScope(Dispatchers.IO).launch {
            setRandomImage(userId)
        }
    }

    fun setImage(userId: Int, imageId: Int) {
        val user = usersRepository.getUser(userId)
        requireNotNull(user) { "User with id $userId not found" }
        try {
            usersRepository.setImage(userId, imageId)
        } catch (e: DataIntegrityViolationException) {
            throw IllegalArgumentException("Image wih id $imageId not found", e)
        }
    }

    private suspend fun setRandomImage(userId: Int) {
        val imageData = unsplashClient.getRandomImage(200, 200)
        val imageId = imagesRepository.addImage(imageData)
        usersRepository.setImage(userId, imageId)
    }
}