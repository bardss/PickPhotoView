package com.jakubaniola.pickphotoview

import java.util.concurrent.atomic.AtomicInteger

internal object UniqueIdGenerator {

    private val atomicInteger = AtomicInteger()

    fun generateNextId(): Int {
        return atomicInteger.incrementAndGet()
    }
}