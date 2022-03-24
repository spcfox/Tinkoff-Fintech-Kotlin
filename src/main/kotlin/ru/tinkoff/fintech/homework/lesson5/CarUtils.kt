package ru.tinkoff.fintech.homework.lesson5

const val USD_RATE = 96.25

interface CarUtilsInterface {
    fun carsRuIntoEn(cars: Iterable<Car>): List<Car>

    fun groupByBodyType(cars: Iterable<Car>): Map<String, List<Car>>

    fun <R> filterMapAndTake3(cars: Iterable<Car>, predicate: (Car) -> Boolean, transform: (Car) -> R): List<R>
}

object CarUtils : CarUtilsInterface {
    override fun carsRuIntoEn(cars: Iterable<Car>): List<Car> {
        return cars.map(Car::translateFromRuIntoEn).toList()
    }

    override fun groupByBodyType(cars: Iterable<Car>): Map<String, List<Car>> {
        return cars.groupBy(Car::bodyType)
    }

    override fun <R> filterMapAndTake3(cars: Iterable<Car>, predicate: (Car) -> Boolean, transform: (Car) -> R): List<R> {
        return cars.filter(predicate).map(transform).take(3)
    }
}

object CarUtilsSequences : CarUtilsInterface {
    override fun carsRuIntoEn(cars: Iterable<Car>): List<Car> {
        return cars.asSequence().map(Car::translateFromRuIntoEn).toList()
    }

    override fun groupByBodyType(cars: Iterable<Car>): Map<String, List<Car>> {
        return cars.asSequence().groupBy(Car::bodyType)
    }

    override fun <R> filterMapAndTake3(cars: Iterable<Car>, predicate: (Car) -> Boolean, transform: (Car) -> R): List<R> {
        return cars.asSequence().filter(predicate).map(transform).take(3).toList()
    }
}
