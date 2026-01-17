package com.emoulgen.vibecodingapp.utils

object DeliveryTimeConverter {
    /**
     * Converts delivery time label string to hours (Int)
     */
    fun toHours(deliveryTimeLabel: String): Int {
        return when (deliveryTimeLabel.trim().lowercase()) {
            "12 hours" -> 12
            "1 day" -> 24
            "2 day" -> 48
            "3 day" -> 72
            "5 day" -> 120
            "10 day" -> 240
            "15 day" -> 360
            else -> 24 // Default to 1 day
        }
    }
    
    /**
     * Converts hours (Int) back to delivery time label string
     */
    fun hoursToString(hours: Int): String {
        return when (hours) {
            12 -> "12 hours"
            24 -> "1 day"
            48 -> "2 day"
            72 -> "3 day"
            120 -> "5 day"
            240 -> "10 day"
            360 -> "15 day"
            else -> "1 day" // Default
        }
    }
}

