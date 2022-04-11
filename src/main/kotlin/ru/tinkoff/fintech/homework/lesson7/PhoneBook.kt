package ru.tinkoff.fintech.homework.lesson7

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class PhoneBook

fun main() {
    runApplication<PhoneBook>()
}
