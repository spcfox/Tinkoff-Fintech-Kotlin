import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import ru.tinkoff.fintech.homework.lesson1.Bamboo

class BambooTests {
    @Test
    fun `стандартные значение — влажность 0%, высота 100см`() {
        val bamboo = Bamboo()

        assertEquals("Bamboo: humidity: 0%, height: 100sm", bamboo.status())
    }

    @Test
    fun `полив увеличивает влажность на 10%`() {
        val bamboo = Bamboo()

        bamboo.water()

        assertEquals("Bamboo: humidity: 10%, height: 100sm", bamboo.status())
    }

    @ParameterizedTest
    @CsvSource(
        "1,     10",
        "2,     20",
        "5,     50",
        "9,     90",
        "10,     100"
    )
    fun `многократный полив увеличивает влажность на times * 10%`(times: Int, humidity: Int) {
        val bamboo = Bamboo()

        bamboo.water(times)

        assertEquals("Bamboo: humidity: $humidity%, height: 100sm", bamboo.status())
    }

    @Test
    fun `не может быть больше 100% — 1 полив`() {
        val bamboo = Bamboo()

        bamboo.water(10)
        bamboo.water()

        assertEquals("Bamboo: humidity: 100%, height: 100sm", bamboo.status())
    }

    @ParameterizedTest
    @CsvSource("11", "12", "20", "100", Int.MAX_VALUE.toString())
    fun `не может быть больше 100% — многократный полив с 0`(times: Int) {
        val bamboo = Bamboo()

        bamboo.water(times)

        assertEquals("Bamboo: humidity: 100%, height: 100sm", bamboo.status())
    }

    @ParameterizedTest
    @CsvSource(
        "10,    1",
        "10,    2",
        "10,    10",
        "10,    100",
        "5,     6",
        "5,     7",
        "5,     10",
        "5,     100",
        "${Int.MAX_VALUE},  1",
        "1,                 ${Int.MAX_VALUE}",
        "${Int.MAX_VALUE},  ${Int.MAX_VALUE}",
    )
    fun `не может быть больше 100% — многократный полив дважды`(times1: Int, times2: Int) {
        val bamboo = Bamboo()

        bamboo.water(times1)
        bamboo.water(times2)

        assertEquals("Bamboo: humidity: 100%, height: 100sm", bamboo.status())
    }

    @ParameterizedTest
    @CsvSource("0", "-1", "-2", "-10", Int.MIN_VALUE.toString())
    fun `количество поливов должно быть положительное`(times: Int) {
        val bamboo = Bamboo()

        assertThrows(IllegalArgumentException::class.java) { bamboo.water(times) }
    }
}
