package ru.tinkoff.fintech.homework.lesson4

import kotlin.NoSuchElementException
import kotlin.math.min

class ArrayQueue<E> {
    companion object {
        private const val DEFAULT_CAPACITY = 16
    }

    private var array: Array<Any?>
    private var head = 0
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

    fun element(): E = getOrThrow(0)

    fun remove(): E {
        val item = element()
        removeHead()
        return item
    }

    fun peek(): E? = getOrNull(0)

    fun poll(): E? {
        val item = peek()
        removeHead()
        return item
    }

    fun offer(item: E): Boolean {
        ensureCapacity()
        array[getArrayIndex(size)] = item
        size++
        return true
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
        if (newArraySize == array.size) {
            return
        }
        val newArray = arrayOfNulls<Any?>(newArraySize)
        val startSize = min(size, array.size - head)
        array.copyInto(newArray, destinationOffset = 0, startIndex = head, endIndex = head + startSize)
        if (startSize < size) {
            array.copyInto(newArray, destinationOffset = startSize, startIndex = 0, endIndex = size - startSize)
        }
        array = newArray
        head = 0
    }

    private fun getOrThrow(index: Int): E {
        if (isEmpty) {
            throw NoSuchElementException()
        }
        return get(index)
    }

    private fun removeHead() {
        array[head] = null
        size--
        head = (head + 1) % array.size
    }

    private fun getOrNull(index: Int): E? {
        if (isEmpty) {
            return null
        }
        return get(index)
    }

    private fun get(index: Int): E {
        @Suppress("UNCHECKED_CAST")
        return array[getArrayIndex(index)] as E
    }

    private fun getArrayIndex(index: Int): Int {
        assert(index in array.indices)
        return (head + index) % array.size
    }
}