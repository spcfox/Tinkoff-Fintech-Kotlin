import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

import ru.tinkoff.fintech.homework.lesson4.ArrayStack
import java.util.*
import kotlin.NoSuchElementException
import kotlin.collections.ArrayList

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ArrayStackTest {
    @Test
    fun <T> `конструктор по умолчанию создаёт пустой стек`() {
        val stack = ArrayStack<T>()

        assertAll("стек пустой",
            { assertEquals(0, stack.size) },
            { assertEquals(true, stack.isEmpty) },
        )
    }

    @Test
    fun <T> `peek на пустом сете бросает исключение`() {
        val stack = ArrayStack<T>()

        assertThrows(NoSuchElementException::class.java, stack::peek)
    }

    @Test
    fun <T> `pop пустом сете бросает исключение`() {
        val stack = ArrayStack<T>()

        assertThrows(NoSuchElementException::class.java, stack::pop)
    }

    @ParameterizedTest
    @MethodSource("oneInt", "oneString")
    fun <T> `push добавляет элемент`(item: T) {
        val stack = ArrayStack<T>()

        stack.push(item)

        assertAll("в стеке лежит один элемент",
            { assertEquals(1, stack.size) },
            { assertEquals(false, stack.isEmpty) },
        )
    }

    @ParameterizedTest
    @MethodSource("oneInt", "oneString")
    fun <T> `peek возвращает верхний элемент`(item: T) {
        val stack = ArrayStack<T>()

        stack.push(item)
        val result = stack.peek()

        assertAll("peek вернул элемент, и он остался в стеке",
            { assertEquals(1, stack.size) },
            { assertEquals(false, stack.isEmpty) },
            { assertEquals(item, result) },
        )
    }

    @ParameterizedTest
    @MethodSource("oneInt", "oneString")
    fun <T> `pop удаляет верхний элемент`(item: T) {
        val stack = ArrayStack<T>()

        stack.push(item)
        val result = stack.pop()

        assertAll("pop вернул элемент и удалил его",
            { assertEquals(0, stack.size) },
            { assertEquals(true, stack.isEmpty) },
            { assertEquals(item, result) },
        )
    }

    @ParameterizedTest
    @MethodSource("intList", "stringList")
    fun <T> `элементы возвращаются в обратном порядке`(items: List<T>) {
        val stack = ArrayStack<T>()

        for (item in items) {
            stack.push(item)
        }
        val result = ArrayList<T>()
        while (!stack.isEmpty) {
            result.add(stack.pop())
        }
        result.reverse()

        assertAll("стек пустой и вернул изначальные элементы",
            { assertEquals(0, stack.size) },
            { assertEquals(true, stack.isEmpty) },
            { assertEquals(items.size, result.size) },
            { assertTrue(
                items.zip(result).all { (a, b) -> Objects.equals(a, b) }
            ) }
        )
    }

    @ParameterizedTest
    @MethodSource("intList", "stringList")
    fun <T> `shrinkToFit ничего не ломает`(items: List<T>) {
        val stack = ArrayStack<T>()

        for (item in items) {
            stack.push(item)
        }
        stack.shrinkToFit()
        val result = ArrayList<T>()
        while (!stack.isEmpty) {
            result.add(stack.pop())
        }
        result.reverse()

        assertAll("стек пустой и вернул изначальные элементы",
            { assertEquals(0, stack.size) },
            { assertEquals(true, stack.isEmpty) },
            { assertEquals(items.size, result.size) },
            { assertTrue(
                items.zip(result).all { (a, b) -> Objects.equals(a, b) }
            ) }
        )
    }

    @ParameterizedTest
    @CsvSource("0", "1", "4", "16", "123", "1024")
    fun `корректная работа с различными стартовыми значениями capacity`(capacity: Int) {
        val stack = ArrayStack<Int>(capacity)
        val items = listOf(-1, 13, 27, 5, 0, -12)

        for (item in items) {
            stack.push(item)
        }

        assertAll("в стеке лежат все элементы",
            { assertEquals(items.size, stack.size) },
            { assertEquals(false, stack.isEmpty) },
        )
    }

    @ParameterizedTest
    @CsvSource("-1", "-2", "-123", Int.MIN_VALUE.toString())
    fun `отрицательный capacity запрещён`(capacity: Int) {
        assertThrows(IllegalArgumentException::class.java) { ArrayStack<Int>(capacity) }
    }

    @ParameterizedTest
    @MethodSource("intList", "stringList")
    fun <T> `конструктор от списков`(items: List<T>) {
        val stack = ArrayStack(items)

        val result = ArrayList<T>()
        while (!stack.isEmpty) {
            result.add(stack.pop())
        }
        result.reverse()

        assertAll("стек пустой и вернул изначальные элементы",
            { assertEquals(0, stack.size) },
            { assertEquals(true, stack.isEmpty) },
            { assertEquals(items.size, result.size) },
            { assertTrue(
                items.zip(result).all { (a, b) -> Objects.equals(a, b) }
            ) }
        )
    }

    @ParameterizedTest
    @MethodSource("stringList")
    fun `конструктор от массивов`(items: List<String>) {
        val stack = ArrayStack(items.toTypedArray())

        val result = ArrayList<String>()
        while (!stack.isEmpty) {
            result.add(stack.pop())
        }
        result.reverse()

        assertAll("стек пустой и вернул изначальные элементы",
            { assertEquals(0, stack.size) },
            { assertEquals(true, stack.isEmpty) },
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
