package com.emoulgen.vibecodingapp.utils

// Delivery time options (in hours)
object DeliveryTimeOption {
    const val HOURS_12 = 12
    const val HOURS_24 = 24
    const val HOURS_48 = 48
    const val HOURS_72 = 72
    const val DAYS_5 = 120
    const val DAYS_7 = 168

    val ALL = mapOf(
        "12 Hours" to HOURS_12,
        "24 Hours" to HOURS_24,
        "48 Hours" to HOURS_48,
        "3 Days" to HOURS_72,
        "5 Days" to DAYS_5,
        "7 Days" to DAYS_7
    )

    fun getLabel(hours: Int): String {
        return when (hours) {
            HOURS_12 -> "12 Hours"
            HOURS_24 -> "24 Hours"
            HOURS_48 -> "48 Hours"
            HOURS_72 -> "3 Days"
            DAYS_5 -> "5 Days"
            DAYS_7 -> "7 Days"
            else -> "$hours Hours"
        }
    }
}