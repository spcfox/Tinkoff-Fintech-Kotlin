package ru.tinkoff.fintech.homework.lesson8

import java.util.concurrent.*

class ThreadPool(nThreads: Int) : Executor {
    init {
        require(nThreads > 0) { "Number of threads must be positive" }
    }

    private val threads: Array<Thread> = Array(nThreads) { WorkerThread() }
    private val tasks: BlockingQueue<Runnable> = LinkedBlockingQueue()
    @Volatile var isShutdown = false
        private set

    private val endTask: Runnable = Runnable { addEnd() }

    init {
        threads.forEach { it.start() }
    }

    override fun execute(task: Runnable) {
        if (isShutdown) {
            throw RejectedExecutionException("ThreadPool is shutdown")
        }
        tasks.put(task)
    }

    fun shutdown() {
        if (!isShutdown) {
            isShutdown = true
            addEnd()
        }
    }

    private fun addEnd() = tasks.put(endTask)

    private inner class WorkerThread : Thread() {
        override fun run() {
            do {
                val task = tasks.take()
                task.run()
            } while (task !== endTask)
        }
    }
}
