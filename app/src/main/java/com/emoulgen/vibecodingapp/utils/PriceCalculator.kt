package com.emoulgen.vibecodingapp.utils

import kotlin.math.ceil

object PriceCalculator {
    
    // Service type coefficients
    private const val EDITING_COEFFICIENT = 1.00
    private const val PROOFREADING_COEFFICIENT = 0.85 // 20% cheaper than editing (15% of 1.00 = 0.85)
    
    // Word count ranges
    private val RANGE_0_3000 = 0..3000
    private val RANGE_3000_7500 = 3001..7500
    private val RANGE_7500_15000 = 7501..15000
    private val RANGE_15000_60000 = 15001..60000
    private val RANGE_60000_PLUS = 60001..Int.MAX_VALUE
    
    // Delivery time coefficients by word count range
    // Format: wordCountRange -> (deliveryTime -> coefficient)
    private val DELIVERY_TIME_COEFFICIENTS = mapOf(
        // 0-3000 words
        RANGE_0_3000 to mapOf(
            "12 hours" to 0.0560,
            "1 day" to 0.0460,
            "2 day" to 0.0380,
            "3 day" to 0.0330,
            "5 day" to 0.0310,
            "10 day" to 0.0290,
            "15 day" to 0.0270
        ),
        // 3000-7500 words
        RANGE_3000_7500 to mapOf(
            "12 hours" to 0.0560,
            "1 day" to 0.0460,
            "2 day" to 0.0380,
            "3 day" to 0.0330,
            "5 day" to 0.0310,
            "10 day" to 0.0290,
            "15 day" to 0.0270
        ),
        // 7500-15000 words
        RANGE_7500_15000 to mapOf(
            "12 hours" to 0.0560,
            "1 day" to 0.0440,
            "2 day" to 0.0360,
            "3 day" to 0.0330,
            "5 day" to 0.0310,
            "10 day" to 0.0260,
            "15 day" to 0.0250
        ),
        // 15000-60000 words
        RANGE_15000_60000 to mapOf(
            "12 hours" to 0.0560,
            "1 day" to 0.0440,
            "2 day" to 0.0330,
            "3 day" to 0.0310,
            "5 day" to 0.0280,
            "10 day" to 0.0260,
            "15 day" to 0.0250
        ),
        // More than 60000 words
        RANGE_60000_PLUS to mapOf(
            "12 hours" to 0.0560,
            "1 day" to 0.0440,
            "2 day" to 0.0360,
            "3 day" to 0.0290,
            "5 day" to 0.0260,
            "10 day" to 0.0250,
            "15 day" to 0.0230
        )
    )
    
    /**
     * Calculate the base price before any discount
     * Formula: wordCount * serviceTypeCoefficient * deliveryTimeCoefficient
     */
    fun calculateBasePrice(
        serviceType: String,
        deliveryTime: String,
        wordCount: Int
    ): Double {
        if (wordCount <= 0) return 0.0
        
        // Get service type coefficient (normalize for case-insensitive matching)
        val normalizedServiceType = serviceType.trim().lowercase()
        val serviceCoefficient = when (normalizedServiceType) {
            "editing" -> EDITING_COEFFICIENT
            "proofreading", "proof" -> PROOFREADING_COEFFICIENT
            else -> EDITING_COEFFICIENT // Default to editing
        }
        
        // Get word count range
        val wordCountRange = when (wordCount) {
            in RANGE_0_3000 -> RANGE_0_3000
            in RANGE_3000_7500 -> RANGE_3000_7500
            in RANGE_7500_15000 -> RANGE_7500_15000
            in RANGE_15000_60000 -> RANGE_15000_60000
            else -> RANGE_60000_PLUS
        }
        
        // Get delivery time coefficient for this word count range
        // Normalize delivery time string for lookup (handle case variations)
        val normalizedDeliveryTime = deliveryTime.trim().lowercase()
        val deliveryCoefficient = DELIVERY_TIME_COEFFICIENTS[wordCountRange]
            ?.get(normalizedDeliveryTime)
            ?: DELIVERY_TIME_COEFFICIENTS[RANGE_0_3000]?.get("1 day") ?: 0.0460 // Default fallback
        
        // Calculate: wordCount * serviceCoefficient * deliveryCoefficient
        val basePrice = wordCount * serviceCoefficient * deliveryCoefficient
        
        // Round up to 2 decimal places
        return (ceil(basePrice * 100) / 100)
    }
    
    /**
     * Calculate final price after applying promotion code discount
     * @param basePrice The base price before discount
     * @param promotionCode The promotion code (e.g., "IEJEEPRO", "SUTJIPTO")
     * @return Final price after discount
     */
    fun calculateFinalPrice(
        basePrice: Double,
        promotionCode: String?
    ): Double {
        if (promotionCode.isNullOrBlank()) return basePrice
        
        // Get discount rate from promotion code
        val discountRate = getPromotionCodeDiscount(promotionCode) ?: return basePrice
        
        // Apply discount: basePrice * (1 - discountRate/100)
        val finalPrice = basePrice * (1 - discountRate / 100.0)
        
        // Round up to 2 decimal places
        return (ceil(finalPrice * 100) / 100)
    }
    
    /**
     * Get discount rate for a promotion code
     * This should ideally come from a database/API, but for now using hardcoded values
     * based on the promo codes shown in the image
     */
    private fun getPromotionCodeDiscount(code: String): Int? {
        // Map of promotion codes to discount rates (from the image)
        val promoCodeDiscounts = mapOf(
            "IEJEEPRO" to 10,
            "SUTJIPTO" to 15,
            "MRCH2023" to 20,
            "FALLDEAL" to 20,
            "BEST2025" to 25,
            "OPENED22" to 10,
            "DRSMITHA" to 15
        )
        
        return promoCodeDiscounts[code.uppercase()]
    }
    
    /**
     * Calculate price with all parameters
     * This is the main function to use for calculating prices
     */
    fun calculatePrice(
        serviceType: String,
        deliveryTime: String,
        wordCount: Int,
        promotionCode: String? = null
    ): Double {
        val basePrice = calculateBasePrice(serviceType, deliveryTime, wordCount)
        return calculateFinalPrice(basePrice, promotionCode)
    }
}
