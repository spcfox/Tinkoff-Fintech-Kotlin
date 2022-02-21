package ru.tinkoff.fintech.homework.lesson1

import java.lang.Integer.min

const val SM = "sm"
const val M = "m"

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

    override fun status(): String = status(SM)

    fun status(units: String): String = "Bamboo: humidity: $humidity%, height: ${getHeight(units)}$units"

    private fun getHeight(units: String) = when (units) {
        SM -> height
        M -> height / 100
        else -> throw IllegalArgumentException("Unsupported units '$units'")
    }
}
