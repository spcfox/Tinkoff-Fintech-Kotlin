package ru.tinkoff.fintech.homework.lesson13

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.SpykBean
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.util.LinkedMultiValueMap
import ru.tinkoff.fintech.homework.lesson13.model.Event
import ru.tinkoff.fintech.homework.lesson13.model.EventStatus
import ru.tinkoff.fintech.homework.lesson13.model.EventType
import ru.tinkoff.fintech.homework.lesson13.repository.EventJpaRepository
import ru.tinkoff.fintech.homework.lesson13.service.consumer.EmailConsumer
import ru.tinkoff.fintech.homework.lesson13.service.consumer.NotificationConsumer
import ru.tinkoff.fintech.homework.lesson13.service.consumer.PushConsumer
import ru.tinkoff.fintech.homework.lesson13.service.consumer.SmsConsumer

@SpringBootTest
@AutoConfigureMockMvc
@KotlinParameterizedTest
class NotificationTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    private val eventRepository: EventJpaRepository,
    ) {

    @SpykBean
    private lateinit var smsConsumer: SmsConsumer
    @SpykBean
    private lateinit var emailConsumer: EmailConsumer
    @SpykBean
    private lateinit var pushConsumer: PushConsumer

    @AfterEach
    private fun clearMock() {
        clearAllMocks()
    }

    @ParameterizedTest
    @MethodSource("methods")
    fun `отправка 1 уведомления`(type: EventType, consumer: NotificationConsumer, method: (String) -> ResultActions) {
        val message = "message body"

        val newEvent: Event = method(message).readResponse()
        var processedEvent: Event
        do {
            Thread.sleep(100L)
            processedEvent = eventRepository.getById(newEvent.id)
        } while (processedEvent.status == EventStatus.NEW || processedEvent.status == EventStatus.IN_PROCESS)

        assertAll(
            { assertEquals(newEvent.id, processedEvent.id) },
            { assertEquals(message, newEvent.body) },
            { assertEquals(message, processedEvent.body) },
            { assertEquals(type, newEvent.type) },
            { assertEquals(type, processedEvent.type) },
            { assertEquals(EventStatus.NEW, newEvent.status) },
            { assertEquals(EventStatus.DONE, processedEvent.status) },
        )
        verify(exactly = 1) { consumer.sendNotification(any()) }
    }

    @ParameterizedTest
    @MethodSource("methods")
    fun `отправка 100 уведомлений`(type: EventType, consumer: NotificationConsumer, method: (String) -> ResultActions) {
        val count = 100
        val message = "message body"
        val newEvents = mutableListOf<Event>()

        for (i in 0 until count) {
            newEvents.add(method(message + i).readResponse())
        }

        val processedEvents = mutableListOf<Event>()
        for (event in newEvents) {
            var processedEvent: Event
            do {
                Thread.sleep(100L)
                processedEvent = eventRepository.getById(event.id)
            } while (processedEvent.status == EventStatus.NEW || processedEvent.status == EventStatus.IN_PROCESS)
            processedEvents.add(processedEvent)
        }

        assertAll(
            { assertEquals(newEvents.map { it.id }, processedEvents.map { it.id }) },
            { assertEquals(newEvents.map { it.body }, processedEvents.map { it.body }) },
            { assertTrue(newEvents.all { it.type == type }) },
            { assertTrue(processedEvents.all { it.type == type }) },
            { assertTrue(newEvents.all { it.status == EventStatus.NEW }) },
            { assertTrue(processedEvents.all { it.status == EventStatus.DONE }) },
        )
        verify(exactly = count) { consumer.sendNotification(any()) }
    }

    @ParameterizedTest
    @MethodSource("methods")
    fun `ошибка при отправке`(type: EventType, consumer: NotificationConsumer, method: (String) -> ResultActions) {
        val message = "message body"
        every { consumer.sendNotification(any()) } throws IllegalStateException()

        val newEvent: Event = method(message).readResponse()
        var processedEvent: Event
        do {
            Thread.sleep(100L)
            processedEvent = eventRepository.getById(newEvent.id)
        } while (processedEvent.status == EventStatus.NEW || processedEvent.status == EventStatus.IN_PROCESS)

        assertAll(
            { assertEquals(newEvent.id, processedEvent.id) },
            { assertEquals(message, newEvent.body) },
            { assertEquals(message, processedEvent.body) },
            { assertEquals(type, newEvent.type) },
            { assertEquals(type, processedEvent.type) },
            { assertEquals(EventStatus.NEW, newEvent.status) },
            { assertEquals(EventStatus.ERROR, processedEvent.status) },
        )
        verify(exactly = 1) { consumer.sendNotification(any()) }
    }

    private fun methods() = listOf(
        Arguments.of(EventType.SMS, smsConsumer, ::sms),
        Arguments.of(EventType.EMAIL, emailConsumer, ::email),
        Arguments.of(EventType.PUSH, pushConsumer, ::push),
    )

    private fun sms(body: String): ResultActions =
        mockMvc.perform(
            post("/sms")
                .params(createParams("body" to body))
        )

    private fun email(body: String): ResultActions =
        mockMvc.perform(
            post("/email")
                .params(createParams("body" to body))
        )

    private fun push(body: String): ResultActions =
        mockMvc.perform(
            post("/push")
                .params(createParams("body" to body))
        )

    private inline fun <reified T> ResultActions.readResponse(): T = this
        .andExpect(status().isOk)
        .andReturn().response.getContentAsString(Charsets.UTF_8)
        .let { if (T::class == String::class) it as T else objectMapper.readValue(it, T::class.java) }


    private fun createParams(vararg params: Pair<String, Any?>) =
        LinkedMultiValueMap<String, String>().apply {
            params.forEach { (key, value) -> if (value != null) add(key, value.toString()) }
        }
}