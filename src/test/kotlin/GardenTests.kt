import io.mockk.every
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import ru.tinkoff.fintech.homework.lesson1.*

@ExtendWith(MockKExtension::class)
class GardenTests {
    @SpyK
    val plant = spyk<Plant> {
        every { water() } returns Unit
        every { grow() } returns Unit
    }

    @ParameterizedTest
    @CsvSource(
        "1,     1",
        "2,     2",
        "10,    10",
        "1,     5",
        "5,     1",
        "0,     100",
        "100,   0",
    )
    fun `проверка вызова поливов и взращивания у сада с одним растением`(waterTimes: Int, growTimes: Int) {
        val garden = Garden(plant)

        repeat(waterTimes) { garden.water() }
        repeat(growTimes) { garden.grow() }

        verify(exactly = waterTimes) { plant.water() }
        verify(exactly = growTimes) { plant.grow() }
    }

    @ParameterizedTest
    @CsvSource(
        "1,     1",
        "2,     2",
        "10,    10",
        "1,     5",
        "5,     1",
        "0,     100",
        "100,   0",
    )
    fun `проверка вызова поливов и взращивания у сада с несколькими растениями`(waterTimes: Int, growTimes: Int) {
        val garden = Garden(Rose(), plant, Bamboo())

        repeat(waterTimes) { garden.water() }
        repeat(growTimes) { garden.grow() }

        verify(exactly = waterTimes) { plant.water() }
        verify(exactly = growTimes) { plant.grow() }
    }

    @ParameterizedTest
    @CsvSource(
        "1,     1",
        "2,     2",
        "10,    10",
        "1,     5",
        "5,     1",
        "0,     100",
        "100,   0",
    )
    fun `проверка вызова поливов и взращивания и сада с повторяющимся растениями`(waterTimes: Int, growTimes: Int) {
        val garden = Garden(plant, plant)

        repeat(waterTimes) { garden.water() }
        repeat(growTimes) { garden.grow() }

        verify(exactly = 2 * waterTimes) { plant.water() }
        verify(exactly = 2 * growTimes) { plant.grow() }
    }
}
