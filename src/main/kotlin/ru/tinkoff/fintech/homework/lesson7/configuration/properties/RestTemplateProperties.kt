package ru.tinkoff.fintech.homework.lesson7.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("rest-template")
class RestTemplateProperties (
    val connectTimeoutInSeconds: Long,
    val readTimeoutInSeconds: Long
)