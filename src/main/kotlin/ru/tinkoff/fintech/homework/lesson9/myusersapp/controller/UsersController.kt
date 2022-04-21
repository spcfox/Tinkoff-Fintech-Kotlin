package ru.tinkoff.fintech.homework.lesson9.myusersapp.controller

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import ru.tinkoff.fintech.homework.lesson9.myusersapp.model.User
import ru.tinkoff.fintech.homework.lesson9.myusersapp.service.UsersService

@RestController
@RequestMapping("/users")
class UsersController(private val usersService: UsersService) {
    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: Int): User = usersService.getUser(userId)

    @GetMapping("/user-image/{userId}", produces = [MediaType.IMAGE_JPEG_VALUE])
    @ResponseBody
    fun getUserImage(@PathVariable userId: Int): ByteArray = usersService.getUserImage(userId)

    @GetMapping("/image/{imageId}", produces = [MediaType.IMAGE_JPEG_VALUE])
    @ResponseBody
    fun getImage(@PathVariable imageId: Int): ByteArray = usersService.getImage(imageId)

    @PostMapping("/new")
    fun newUser(@RequestParam username: String): Int = usersService.newUser(username)

    @PostMapping("/new-image")
    fun newRandomImage(@RequestParam userId: Int): Unit = usersService.newRandomImage(userId)

    @PostMapping("/set-image")
    fun setImage(@RequestParam userId: Int, @RequestParam imageId: Int): Unit =
        usersService.setImage(userId, imageId)
}
