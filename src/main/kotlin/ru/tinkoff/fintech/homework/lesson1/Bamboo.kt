package ru.tinkoff.fintech.homework.lesson1

import java.lang.Integer.min

class Bamboo : Plant {
    private var humidity = 0
    private var height = 100

    override fun water() = water(1)

    fun water(times: Int) {
        humidity = min(100, humidity + 10 * times)
    }

    override fun grow() {
        if (humidity >= 10) {
            humidity -= 10
            height += 100
        }
    }

    override fun status(): String = status(LengthUnits.SM)

    fun status(units: LengthUnits): String = "Bamboo: humidity: $humidity%, height: ${getHeight(units)}${units.unitsName}"

    fun getHeight(units: LengthUnits) = when (units) {
        LengthUnits.SM -> height
        LengthUnits.M -> height / 100
    }

    enum class LengthUnits(val unitsName: String) {
        SM("sm"), M("m")
    }
}
