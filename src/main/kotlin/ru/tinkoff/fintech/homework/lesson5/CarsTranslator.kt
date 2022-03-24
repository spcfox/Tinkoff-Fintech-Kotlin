package ru.tinkoff.fintech.homework.lesson5

private val brandsRuIntoEn = listOf(
    "Тойота" to "Toyota",
    "Шкода" to "Skoda",
    "ВАЗ" to "LADA",
    "Ауди" to "Audi",
    "Порше" to "Porsche",
)

private val namesRuIntoEn = listOf(
    "Камри" to "Camry",
    "Октавиа" to "Octavia",
    "А4" to "A4",
    "Супра" to "Supra",
)

private val bodyTypesRuIntoEn = listOf(
    "Седан" to "Sedan",
    "Купе" to "Coupe",
    "Кабриолет" to "Convertible",
)

fun Car.translateFromRuIntoEn() = Car(
    brand = translate(brand, brandsRuIntoEn),
    name = translate(name, namesRuIntoEn),
    bodyType = translate(bodyType, bodyTypesRuIntoEn),
    fuelConsumption = fuelConsumption,
    price = price.toUsd()
)

private fun translate(field: String, dictionary: List<Pair<String, String>>) =
    dictionary.find { it.first == field }?.second ?: field
