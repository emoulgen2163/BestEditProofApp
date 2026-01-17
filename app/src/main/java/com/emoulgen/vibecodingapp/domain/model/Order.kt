package com.emoulgen.vibecodingapp.domain.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

// Order data class for Firestore
data class OrderData(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val orderNumber: String = "",
    val projectTitle: String = "",
    val serviceType: String = "",
    val status: String = "Incomplete",
    val price: Double = 0.0,
    val deliveryTime: Int = 24, // hours
    val deliveryTimeLabel: String? = null, // Add this for display
    val wordCount: Int = 0, // Add this field
    val englishVariant: String = "",
    val projectDescription: String = "",
    val promotionCode: String? = null,
    val fileUrl: String? = null,
    val fileName: String? = null,
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val updatedAt: Timestamp? = null,
    val dueDate: Timestamp? = null
)

// Order for UI (matches dashboard Order class)
data class Order(
    val id: String,
    val title: String,
    val status: String,
    val price: String,
    val rate: String = ""
)

// Create Order Request (for new order flow)
data class CreateOrderRequest(
    val projectTitle: String,
    val serviceType: String,
    val deliveryTime: Int, // hours
    val deliveryTimeLabel: String, // e.g., "1 day", "12 hours" - for price calculation
    val wordCount: Int,
    val englishVariant: String,
    val projectDescription: String,
    val promotionCode: String?,
    val fileUrl: String?
)

// Draft data class for order creation form
data class OrderDraft(
    var serviceType: String = "Proofreading",
    var deliveryTime: String = "12 hours", // String label for display
    var deliveryTimeHours: Int = 12, // Converted to hours (Int) for CreateOrderRequest
    var promoCode: String? = null,
    var price: String = "0.00",
    var wordCount: Int = 0,
    var projectTitle: String = "",
    var englishVariant: String = "N/A",
    var projectDescription: String = "",
    var fileName: String? = null,
    val fileUrl: String? = null  // Add this field
)