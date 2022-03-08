package ru.tinkoff.fintech.homework.lesson1

class Garden(private val plants: List<Plant>) {
    constructor(vararg plants: Plant) : this(plants.toList())

    fun water() {
        plants.forEach { it.water() }
    }

    fun grow() {
        plants.forEach { it.grow() }
    }

    fun status(): String = "Garden:\n" + plants.joinToString(System.lineSeparator() + "\t", "\t") { it.status() }
}