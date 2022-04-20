package ru.tinkoff.fintech.homework.lesson9.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource

@Configuration
class JdbcConnection {
    @Bean
    fun jdbcTemplate(dataSource: DataSource): JdbcTemplate = JdbcTemplate(dataSource)
}