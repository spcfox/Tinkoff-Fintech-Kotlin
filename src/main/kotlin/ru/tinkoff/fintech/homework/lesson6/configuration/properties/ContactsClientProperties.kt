package ru.tinkoff.fintech.homework.lesson6.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.stereotype.Component

@ConstructorBinding
@ConfigurationProperties("contacts-client")
class ContactsClientProperties(
    val url: String,
    val paths: Paths
) {

    val get: String
        get() = url + paths.get
    val getById: String
        get() = url + paths.getById
    val add: String
        get() = url + paths.add
    val find: String
        get() = url + paths.find

    class Paths(
        val get: String,
        val getById: String,
        val add: String,
        val find: String
        )
}