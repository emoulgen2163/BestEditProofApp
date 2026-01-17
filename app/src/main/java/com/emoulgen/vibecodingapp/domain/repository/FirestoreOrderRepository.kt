package com.emoulgen.vibecodingapp.domain.repository

import com.emoulgen.vibecodingapp.domain.model.CreateOrderRequest
import com.emoulgen.vibecodingapp.domain.model.Order
import com.emoulgen.vibecodingapp.domain.model.OrderData
import com.emoulgen.vibecodingapp.utils.Resource
import kotlinx.coroutines.flow.Flow

interface FirestoreOrderRepository {

    fun getOrdersRealtime(status: String? = null, search: String? = null): Flow<Resource<List<Order>>>

    fun getOrders(status: String? = null, search: String? = null): Flow<Resource<List<Order>>>

    fun createOrder(request: CreateOrderRequest): Flow<Resource<Order>>

    suspend fun deleteOrder(orderId: String): Resource<Unit>

    fun completeOrder(orderId: String): Flow<Resource<Order>>

    suspend fun getOrderById(orderId: String): Resource<OrderData>

    suspend fun updateOrder(orderId: String, updates: Map<String, Any>): Resource<Unit>
}