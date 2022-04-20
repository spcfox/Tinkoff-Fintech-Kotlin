package ru.tinkoff.fintech.homework.lesson9.myusersapp.client

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import ru.tinkoff.fintech.homework.lesson9.configuration.properties.UnsplashClientProperties

@Service
class UnsplashClient(
    @Qualifier("UnsplashWebClient") private val webClient: WebClient,
    private val unsplashClientProperties: UnsplashClientProperties
) {
    suspend fun getRandomImage(width: Int, height: Int): ByteArray = webClient.get()
            .uri { uriBuilder -> uriBuilder
                .path(unsplashClientProperties.getRandomImage)
                .build(width, height)
            }
            .accept(MediaType.IMAGE_JPEG)
            .retrieve()
            .awaitBody()
}
