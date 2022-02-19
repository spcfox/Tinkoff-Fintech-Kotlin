package ru.tinkoff.fintech.homework.lesson1

fun main() {
    val bamboo = Bamboo()
    val rose = Rose()

    val plants = listOf(bamboo, rose)
    plants.forEach { plant ->
        repeat(3) {
            plant.water()
            plant.grow()
        }
        println(plant.status())
    }

    bamboo.water(3)
    println(bamboo.status(Bamboo.LengthUnits.M))
    println(rose.cutRosebud())
    println(rose.status())

    val garden = Garden(listOf(bamboo, rose, Rose()))
    garden.water()
    garden.grow()
    println(garden.status())
}
