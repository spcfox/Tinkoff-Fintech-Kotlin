package ru.tinkoff.fintech.homework.lesson1

fun main() {
    val bamboo = Bamboo()
    val rose = Rose()

    val plants = listOf(bamboo, rose)
    plants.forEach { plant ->
        waterAndGrow(plant, 3)
        println(plant.status())
    }

    bamboo.water(3)
    println(bamboo.status(M))
    println(rose.cutRosebud())
    println(rose.status())
    if (rose.isBloom) {
        println("The rose is blooming")
    } else {
        println("The rose is not blooming")
    }

    val garden = Garden(listOf(bamboo, rose, Rose()))
    garden.water()
    garden.grow()
    println(garden.status())
}

fun waterAndGrow(plant: Plant, times: Int) {
    repeat(times) {
        plant.water()
        plant.grow()
    }
}
