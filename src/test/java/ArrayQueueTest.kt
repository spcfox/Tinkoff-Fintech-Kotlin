import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

import ru.tinkoff.fintech.homework.lesson4.ArrayQueue
import java.util.*
import kotlin.NoSuchElementException
import kotlin.collections.ArrayList

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ArrayQueueTest {
    @Test
    fun <T> `конструктор по умолчанию создаёт пустой очередь`() {
        val queue = ArrayQueue<T>()

        assertAll("очередь пустая",
            { assertEquals(0, queue.size) },
            { assertEquals(true, queue.isEmpty) },
        )
    }

    @Test
    fun <T> `element на пустом сете бросает исключение`() {
        val queue = ArrayQueue<T>()

        assertThrows(NoSuchElementException::class.java, queue::element)
    }

    @Test
    fun <T> `remove пустом сете бросает исключение`() {
        val queue = ArrayQueue<T>()

        assertThrows(NoSuchElementException::class.java, queue::remove)
    }

    @Test
    fun <T> `peek пустом сете возвращает null`() {
        val queue = ArrayQueue<T>()

        val result = queue.peek()

        assertEquals(null, result)
    }

    @Test
    fun <T> `poll пустом сете возвращает null`() {
        val queue = ArrayQueue<T>()

        val result = queue.poll()

        assertEquals(null, result)
    }

    @ParameterizedTest
    @MethodSource("oneInt", "oneString")
    fun <T> `offer добавляет элемент`(item: T) {
        val queue = ArrayQueue<T>()

        val result = queue.offer(item)

        assertAll("в очереди лежит один элемент",
            { assertEquals(true, result) },
            { assertEquals(1, queue.size) },
            { assertEquals(false, queue.isEmpty) },
        )
    }

    @ParameterizedTest
    @MethodSource("oneInt", "oneString")
    fun <T> `element возвращает элемент из головы`(item: T) {
        val queue = ArrayQueue<T>()

        queue.offer(item)
        val result = queue.element()

        assertAll("element вернул элемент, и он остался в очереди",
            { assertEquals(1, queue.size) },
            { assertEquals(false, queue.isEmpty) },
            { assertEquals(item, result) },
        )
    }

    @ParameterizedTest
    @MethodSource("oneInt", "oneString")
    fun <T> `peek возвращает элемент из головы`(item: T) {
        val queue = ArrayQueue<T>()

        queue.offer(item)
        val result = queue.peek()

        assertAll("peek вернул элемент, и он остался в очереди",
            { assertEquals(1, queue.size) },
            { assertEquals(false, queue.isEmpty) },
            { assertEquals(item, result) },
        )
    }

    @ParameterizedTest
    @MethodSource("oneInt", "oneString")
    fun <T> `remove удаляет верхний элемент`(item: T) {
        val queue = ArrayQueue<T>()

        queue.offer(item)
        val result = queue.remove()

        assertAll("remove вернул элемент и удалил его",
            { assertEquals(0, queue.size) },
            { assertEquals(true, queue.isEmpty) },
            { assertEquals(item, result) },
        )
    }

    @ParameterizedTest
    @MethodSource("oneInt", "oneString")
    fun <T> `poll удаляет верхний элемент`(item: T) {
        val queue = ArrayQueue<T>()

        queue.offer(item)
        val result = queue.poll()

        assertAll("poll вернул элемент и удалил его",
            { assertEquals(0, queue.size) },
            { assertEquals(true, queue.isEmpty) },
            { assertEquals(item, result) },
        )
    }

    @ParameterizedTest
    @MethodSource("intList", "stringList")
    fun <T> `remove возвращает в том же порядке`(items: List<T>) {
        val queue = ArrayQueue<T>()

        val offerResults = mutableListOf<Boolean>()
        for (item in items) {
            offerResults.add(queue.offer(item))
        }
        val result = ArrayList<T>()
        while (!queue.isEmpty) {
            result.add(queue.remove())
        }

        assertAll("очередь пустая и вернула изначальные элементы",
            { assertTrue( offerResults.all { x -> x } ) },
            { assertEquals(0, queue.size) },
            { assertEquals(true, queue.isEmpty) },
            { assertEquals(items.size, result.size) },
            { assertTrue(
                items.zip(result).all { (a, b) -> Objects.equals(a, b) }
            ) }
        )
    }

    @ParameterizedTest
    @MethodSource("intList", "stringList")
    fun <T> `poll возвращает в том же порядке`(items: List<T>) {
        val queue = ArrayQueue<T>()

        val offerResults = mutableListOf<Boolean>()
        for (item in items) {
            offerResults.add(queue.offer(item))
        }
        val result = ArrayList<T?>()
        while (!queue.isEmpty) {
            result.add(queue.poll())
        }

        assertAll("очередь пустая и вернула изначальные элементы",
            { assertTrue( offerResults.all { x -> x } ) },
            { assertEquals(0, queue.size) },
            { assertEquals(true, queue.isEmpty) },
            { assertEquals(items.size, result.size) },
            { assertTrue(
                items.zip(result).all { (a, b) -> Objects.equals(a, b) }
            ) }
        )
    }

    @ParameterizedTest
    @MethodSource("intList", "stringList")
    fun <T> `чередование добавлений и удалений работает нормально`(items: List<T>) {
        val queue = ArrayQueue<T>()

        val times = 100
        for (i in 0 until times) {
            for (item in items) {
                queue.offer(item)
            }
            for (j in 0 until items.size / 2) {
                queue.remove()
            }
        }
        val result = ArrayList<T>()
        for (i in 0 until times * (items.size - items.size / 2)) {
            result.add(queue.remove())
        }

        assertAll("очередь пустая",
            { assertEquals(0, queue.size) },
            { assertEquals(true, queue.isEmpty) },
        )
    }

    @ParameterizedTest
    @MethodSource("intList", "stringList")
    fun <T> `shrinkToFit ничего не ломает`(items: List<T>) {
        val queue = ArrayQueue<T>()

        val times = 100
        for (i in 0 until times) {
            for (item in items) {
                queue.offer(item)
            }
            for (j in 0 until items.size / 2) {
                queue.remove()
            }
        }
        queue.shrinkToFit()
        val result = ArrayList<T>()
        for (i in 0 until times * (items.size - items.size / 2)) {
            result.add(queue.remove())
        }

        assertAll("очередь пустая",
            { assertEquals(0, queue.size) },
            { assertEquals(true, queue.isEmpty) },
        )
    }

    @ParameterizedTest
    @CsvSource("0", "1", "4", "16", "123", "1024")
    fun `корректная работа с различными стартовыми значениями capacity`(capacity: Int) {
        val queue = ArrayQueue<Int>(capacity)
        val items = listOf(-1, 13, 27, 5, 0, -12)

        for (item in items) {
            queue.offer(item)
        }

        assertAll("в очереди лежат все элементы",
            { assertEquals(items.size, queue.size) },
            { assertEquals(false, queue.isEmpty) },
        )
    }

    @ParameterizedTest
    @CsvSource("-1", "-2", "-123", Int.MIN_VALUE.toString())
    fun `отрицательный capacity запрещён`(capacity: Int) {
        assertThrows(IllegalArgumentException::class.java) { ArrayQueue<Int>(capacity) }
    }

    @ParameterizedTest
    @MethodSource("intList", "stringList")
    fun <T> `конструктор от списков`(items: List<T>) {
        val queue = ArrayQueue(items)

        val result = ArrayList<T>()
        while (!queue.isEmpty) {
            result.add(queue.remove())
        }

        assertAll("очередь пустой и вернула изначальные элементы",
            { assertEquals(0, queue.size) },
            { assertEquals(true, queue.isEmpty) },
            { assertEquals(items.size, result.size) },
            { assertTrue(
                items.zip(result).all { (a, b) -> Objects.equals(a, b) }
            ) }
        )
    }

    @ParameterizedTest
    @MethodSource("stringList")
    fun `конструктор от массивов`(items: List<String>) {
        val queue = ArrayQueue(items.toTypedArray())

        val result = ArrayList<String>()
        while (!queue.isEmpty) {
            result.add(queue.remove())
        }

        assertAll("очередь пустая и вернула изначальные элементы",
            { assertEquals(0, queue.size) },
            { assertEquals(true, queue.isEmpty) },
            { assertEquals(items.size, result.size) },
            { assertTrue(
                items.zip(result).all { (a, b) -> Objects.equals(a, b) }
            ) }
        )
    }

    private fun oneInt(): Stream<Int> = Stream.of(1, 2, 10, -1, -2, -10, Int.MAX_VALUE, Int.MIN_VALUE)

    private fun oneString(): Stream<String?> = Stream.of("Hello", ",", "world", "!", "\n", "\u0000", "", null)

    private fun intList(): Stream<List<Int>> = Stream.of(
        listOf(1, 3, 7),
        listOf(-1, 13, 27, 5, 0, -12),
        (0..1000).toList(),
        (Int.MIN_VALUE..Int.MAX_VALUE step 1_000_000).toList(),
    )

    private fun stringList(): Stream<List<String?>> = Stream.of(
        listOf("Hello", ",", "world", "!"),
        listOf("", null, "123", null),
        listOf("\u0000", null, "null")
    )
}
