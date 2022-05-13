package ru.tinkoff.fintech.homework.lesson13.configuration

import org.apache.activemq.RedeliveryPolicy
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQConnectionFactoryCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jms.annotation.EnableJms
import org.springframework.jms.config.DefaultJmsListenerContainerFactory
import javax.jms.ConnectionFactory

@Configuration
@EnableJms
class JmsConfiguration {
    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
    fun jmsListenerContainerFactory(connectionFactory: ConnectionFactory) = DefaultJmsListenerContainerFactory().apply {
        setConnectionFactory(connectionFactory)
        setErrorHandler { e -> log.error(e.message, e) }
    }

    @Bean
    fun configureRedeliveryPolicy() = ActiveMQConnectionFactoryCustomizer { factory ->
        val redeliveryPolicy = RedeliveryPolicy()
        redeliveryPolicy.redeliveryDelay = 1000L
        redeliveryPolicy.maximumRedeliveries = RedeliveryPolicy.NO_MAXIMUM_REDELIVERIES
        factory.redeliveryPolicy = redeliveryPolicy
    }
}
