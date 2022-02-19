package ru.tinkoff.fintech.homework.lesson1

import java.lang.Integer.min

class Rose : Plant {
    private var humidity = 0
    private var height = 10
    private var rosebudSize = 0;

    val isBloom
        get() = rosebudSize > 0

    override fun water() {
        humidity = min(100, humidity + 20)
    }

    override fun grow() {
        if (humidity >= 10) {
            humidity -= 10
            when {
                height < 30 -> height += 10
                else -> rosebudSize += 5
            }
        }
    }

    override fun status(): String = "Rose: humidity: $humidity%, height: ${height}sm, rosebud size: ${rosebudSize}sm"

    fun cutRosebud(): Rosebud? {
        if (!isBloom) {
            return null
        }
        val bud = Rosebud(rosebudSize)
        rosebudSize = 0
        return bud
    }

    data class Rosebud(val size: Int)
}
