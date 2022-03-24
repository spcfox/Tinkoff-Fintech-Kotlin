package ru.tinkoff.fintech.homework.lesson5

data class Price(val value: Double, val currency: String) {
    fun toUsd() = when (currency) {
        "RUB" -> Price(value / USD_RATE, "USD")
        "USD" -> this
        else -> throw IllegalArgumentException("Unknown currency '$currency'")
    }
}

data class Car(
    val brand: String,
    val name: String,
    val bodyType: String,
    val fuelConsumption: Double,
    val price: Price,
)
