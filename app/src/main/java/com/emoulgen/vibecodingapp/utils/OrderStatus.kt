package com.emoulgen.vibecodingapp.utils

// Order status
object OrderStatus {
    const val INCOMPLETE = "Incomplete"
    const val PENDING = "Pending"
    const val IN_PROGRESS = "In Progress"
    const val COMPLETE = "Complete"
    const val CANCELLED = "Cancelled"

    val ALL = listOf(
        INCOMPLETE,
        PENDING,
        IN_PROGRESS,
        COMPLETE,
        CANCELLED
    )
}