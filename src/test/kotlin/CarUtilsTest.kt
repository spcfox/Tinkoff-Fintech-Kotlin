import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import ru.tinkoff.fintech.homework.lesson5.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CarUtilsTest {
    private val carsRuList = listOf(
        Car("Тойота", "Камри",  "Седан", 8.0, Price(2_500_000.0, "RUB")),
        Car( "Шкода", "Октавиа","Купе", 6.1, Price(1_665_000.0, "RUB")),
        Car( "ВАЗ", "2107","Седан", 7.8, Price(100_000.0, "RUB")),
        Car( "Ауди", "А4","Седан", 6.3, Price(3_000_000.0, "RUB")),
        Car( "Порше", "911","Кабриолет", 11.5, Price(12_500_000.0, "RUB")),
        Car( "Тойота", "Супра","Купе", 9.1, Price(5_545_000.0, "RUB")),
    )

    private val carsEnList = listOf(
        Car("Toyota", "Camry", "Sedan", 8.0, Price(2_500_000.0 / USD_RATE, "USD")),
        Car( "Skoda", "Octavia","Coupe", 6.1, Price(1_665_000.0 / USD_RATE, "USD")),
        Car( "LADA", "2107","Sedan", 7.8, Price(100_000.0 / USD_RATE, "USD")),
        Car( "Audi", "A4","Sedan", 6.3, Price(3_000_000.0 / USD_RATE, "USD")),
        Car( "Porsche", "911","Convertible", 11.5, Price(12_500_000.0 / USD_RATE, "USD")),
        Car( "Toyota", "Supra","Coupe", 9.1, Price(5_545_000.0 / USD_RATE, "USD")),
    )

    private val carsGroupedByBodyType = mapOf(
        "Седан" to listOf(
            Car("Тойота", "Камри", "Седан", 8.0, Price(2_500_000.0, "RUB")),
            Car( "ВАЗ", "2107","Седан", 7.8, Price(100_000.0, "RUB")),
            Car( "Ауди", "А4","Седан", 6.3, Price(3_000_000.0, "RUB")),
        ),
        "Купе" to listOf(
            Car( "Шкода", "Октавиа","Купе", 6.1, Price(1_665_000.0, "RUB")),
            Car( "Тойота", "Супра","Купе", 9.1, Price(5_545_000.0, "RUB")),
        ),
        "Кабриолет" to listOf(
            Car( "Порше", "911","Кабриолет", 11.5, Price(12_500_000.0, "RUB")),
        ),
    )

    private val carsWorthTwoMillions = listOf("Тойота Камри", "Ауди А4", "Порше 911")

    private fun utils() = listOf(CarUtils, CarUtilsSequences)

    @ParameterizedTest
    @MethodSource("utils")
    fun `перевод на английский`(utils: CarUtilsInterface) {
        val cars = carsRuList

        val result = utils.carsRuIntoEn(cars)

        assertEquals(carsEnList, result)
    }

    @ParameterizedTest
    @MethodSource("utils")
    fun `перевод пустого списка`(utils: CarUtilsInterface) {
        val cars = emptyList<Car>()

        val result = utils.carsRuIntoEn(cars)

        assertEquals(emptyList<Car>(), result)
    }

    @ParameterizedTest
    @MethodSource("utils")
    fun `группировка по типу кузова`(utils: CarUtilsInterface) {
        val cars = carsRuList

        val result = utils.groupByBodyType(cars)

        assertEquals(carsGroupedByBodyType, result)
    }

    @ParameterizedTest
    @MethodSource("utils")
    fun `группировка пустого списка`(utils: CarUtilsInterface) {
        val cars = emptyList<Car>()

        val result = utils.groupByBodyType(cars)

        assertEquals(emptyMap<String, List<Car>>(), result)
    }

    @ParameterizedTest
    @MethodSource("utils")
    fun `группировка после перевода`(utils: CarUtilsInterface) {
        val cars = carsRuList

        val carsEn = utils.carsRuIntoEn(cars)
        val result = utils.groupByBodyType(carsEn)

        assertEquals(utils.groupByBodyType(carsEnList), result)
    }

    @ParameterizedTest
    @MethodSource("utils")
    fun `первые 3 машины дороже 2,000,000 рублей`(utils: CarUtilsInterface) {
        val cars = carsRuList

        val result = utils.filterMapAndTake3(
            cars,
            { it.price.value >= 2_000_000 },
            { "${it.brand} ${it.name}" }
        )

        assertEquals(carsWorthTwoMillions, result)
    }

    @ParameterizedTest
    @MethodSource("utils")
    fun `первые 3 машины дороже $50,000`(utils: CarUtilsInterface) {
        val cars = carsRuList

        val carsEn = utils.carsRuIntoEn(cars)
        val result = utils.filterMapAndTake3(
            carsEn,
            { it.price.value >= 50_000 },
            { it }
        )

        assertAll(
            { assertTrue(result.size <= 3) },
            { assertTrue(cars.all { it.price.value >= 50_000 }) },
        )
    }

    @ParameterizedTest
    @MethodSource("utils")
    fun `первые 3 машины дороже $1,000`(utils: CarUtilsInterface) {
        val cars = carsRuList

        val carsEn = utils.carsRuIntoEn(cars)
        val result = utils.filterMapAndTake3(
            carsEn,
            { it.price.value >= 1_000 },
            { it }
        )

        assertAll(
            { assertTrue(result.size <= 3) },
            { assertTrue(cars.all { it.price.value >= 1_000 }) },
        )
    }
}
