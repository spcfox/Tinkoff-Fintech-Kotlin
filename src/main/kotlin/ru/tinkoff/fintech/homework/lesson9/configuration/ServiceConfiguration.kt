package ru.tinkoff.fintech.homework.lesson9.configuration

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import ru.tinkoff.fintech.homework.lesson9.configuration.properties.UnsplashClientProperties


@Configuration
class ServiceConfiguration(
    private val unsplashClientProperties: UnsplashClientProperties
) {
    @Bean
    @Qualifier("UnsplashWebClient")
    fun unsplashWebClient(webClientBuilder: WebClient.Builder): WebClient =
        webClientBuilder
            .clientConnector(ReactorClientHttpConnector(
                HttpClient.create().followRedirect(true)
            ))
            .baseUrl(unsplashClientProperties.url).build()
}
