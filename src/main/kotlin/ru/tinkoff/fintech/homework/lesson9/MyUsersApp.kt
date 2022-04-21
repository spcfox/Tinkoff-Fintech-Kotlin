package ru.tinkoff.fintech.homework.lesson9

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan
@SpringBootApplication
class UsersApp

fun main() {
    runApplication<UsersApp>()
}