package ru.tinkoff.fintech.homework.lesson13.configuration

import org.slf4j.LoggerFactory
import org.springframework.boot.task.TaskSchedulerCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableScheduling
class SchedulingConfiguration {
    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
    fun taskSchedulerCustomizer() = TaskSchedulerCustomizer { scheduler ->
        scheduler.setErrorHandler { e -> log.error(e.message, e) }
    }
}
