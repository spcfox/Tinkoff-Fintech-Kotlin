package ru.tinkoff.fintech.homework.lesson4

import kotlin.NoSuchElementException

class ArrayStack<E> {
    companion object {
        private const val DEFAULT_CAPACITY = 16
    }

    private var array: Array<Any?>
    var size: Int
        private set
    val isEmpty: Boolean
        get() = size == 0

    constructor() : this(DEFAULT_CAPACITY)

    constructor(capacity: Int) {
        if (capacity < 0) {
            throw IllegalArgumentException("Capacity cannot be negative")
        }
        array = arrayOfNulls(capacity)
        size = 0
    }

    constructor(array: Array<out E>) : this(array.size) {
        array.copyInto(this.array)
        size = array.size
    }

    constructor(collection: Collection<E>) {
        array = (collection as Collection<Any?>).toTypedArray()
        size = array.size
    }

    fun push(item: E) {
        ensureCapacity()
        array[size++] = item
    }

    fun pop(): E {
        val item = peek()
        array[--size] = null
        return item
    }

    fun peek(): E {
        if (isEmpty) {
            throw NoSuchElementException()
        }
        @Suppress("UNCHECKED_CAST")
        return array[size - 1] as E
    }

    fun shrinkToFit() {
        changeArraySize(size)
    }

    private fun ensureCapacity() {
        assert(size <= array.size)
        if (array.size == size) {
            changeArraySize(2 * (size + 1))
        }
    }

    private fun changeArraySize(newArraySize: Int) {
        assert(newArraySize >= size)
        if (newArraySize != array.size) {
            array = array.copyOf(newArraySize)
        }
    }
}