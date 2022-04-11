package ru.tinkoff.fintech.homework.lesson6.configuration

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import ru.tinkoff.fintech.homework.lesson6.configuration.properties.ContactsClientProperties
import ru.tinkoff.fintech.homework.lesson6.configuration.properties.RestTemplateProperties
import java.time.Duration

@Configuration
class ServiceConfiguration(
    private val restTemplateProperties: RestTemplateProperties
) {
    @Bean
    fun restTemplate(builder: RestTemplateBuilder): RestTemplate = builder
        .setConnectTimeout(Duration.ofSeconds(restTemplateProperties.connectTimeoutInSeconds))
        .setReadTimeout(Duration.ofSeconds(restTemplateProperties.readTimeoutInSeconds))
        .build()
}
