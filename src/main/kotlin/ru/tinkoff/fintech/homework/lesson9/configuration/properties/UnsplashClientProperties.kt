package ru.tinkoff.fintech.homework.lesson9.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("unsplash-client")
class UnsplashClientProperties(
    val url: String,
    val getRandomImage: String
)