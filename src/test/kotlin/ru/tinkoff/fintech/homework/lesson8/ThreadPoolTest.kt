package ru.tinkoff.fintech.homework.lesson8

import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.concurrent.RejectedExecutionException

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ThreadPoolTest {
    private val task = mockk<Runnable>()

    @BeforeEach
    fun initMock() {
        every { task.run() } returns Unit
    }

    @AfterEach
    fun clearMock() {
        clearAllMocks()
    }

    @ParameterizedTest
    @ValueSource(ints = [0, -1, -10, Int.MIN_VALUE])
    fun `некорректное число потоков`(threads: Int) {
        assertThrows(IllegalArgumentException::class.java) { ThreadPool(threads) }
    }

    @ParameterizedTest
    @MethodSource("threadsAndTasksCount")
    fun `все задачи выполняются`(threads: Int, tasks: Int) {
        val pool = spyk(ThreadPool(threads))

        for (i in 0 until tasks) {
            pool.execute(task)
        }
        pool.shutdown()
        Thread.sleep(100)

        verify(exactly = tasks) { pool.execute(any()) }
        verify(exactly = tasks) { task.run() }
    }

    @ParameterizedTest
    @MethodSource("threadsAndTasksCount")
    fun `не принимает задачу после shutdown`(threads: Int, tasks: Int) {
        val pool = spyk(ThreadPool(threads))

        for (i in 0 until tasks) {
            pool.execute(task)
        }
        pool.shutdown()

        assertThrows(RejectedExecutionException::class.java) { pool.execute(task) }
        Thread.sleep(100)
        verify(exactly = tasks + 1) { pool.execute(any()) }
        verify(exactly = tasks) { task.run() }
    }

    @ParameterizedTest
    @MethodSource("threadsAndTasksCount")
    fun `корректно принимает задачи из нескольких потоков`(threads: Int, tasks: Int) {
        val pool = spyk(ThreadPool(threads))
        val producerThreads = 2
        val threadsArray = Array(producerThreads) { Thread {
            for (i in 0 until tasks) {
                pool.execute(task)
            }
        } }

        threadsArray.forEach { it.start() }
        threadsArray.forEach { it.join() }
        pool.shutdown()
        Thread.sleep(100)

        verify(exactly = 2 * tasks) { pool.execute(any()) }
        verify(exactly = 2 * tasks) { task.run() }
    }

    private fun threadsAndTasksCount() = intArrayOf(1, 2, 5).flatMap { threads ->
        intArrayOf(0, 1, 5, 10, 100).map { tasks -> Arguments.of(threads, tasks) }
    }
}
