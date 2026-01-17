package com.emoulgen.vibecodingapp.data.repository

import com.emoulgen.vibecodingapp.domain.model.CreateOrderRequest
import com.emoulgen.vibecodingapp.domain.model.Order
import com.emoulgen.vibecodingapp.domain.model.OrderData
import com.emoulgen.vibecodingapp.domain.repository.FirestoreOrderRepository
import com.emoulgen.vibecodingapp.utils.OrderStatus
import com.emoulgen.vibecodingapp.utils.PriceCalculator
import com.emoulgen.vibecodingapp.utils.Resource
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class FirestoreOrderRepositoryImplementation @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): FirestoreOrderRepository {

    private val ordersCollection = firestore.collection("orders")

    // Get all orders for current user (real-time)
    override fun getOrdersRealtime(status: String?, search: String?): Flow<Resource<List<Order>>> = callbackFlow {
        trySend(Resource.Loading())

        val userId = auth.currentUser?.uid
        if (userId == null) {
            trySend(Resource.Error("Not authenticated"))
            close()
            return@callbackFlow
        }

        var query: Query = ordersCollection
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)

        // Apply status filter
        if (status != null) {
            query = query.whereEqualTo("status", status)
        }

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(Resource.Error(error.localizedMessage ?: "Failed to load orders"))
                return@addSnapshotListener
            }

            if (snapshot != null) {
                var orders = snapshot.documents.mapNotNull { doc ->
                    try {
                        val orderData = doc.toObject(OrderData::class.java)
                        orderData?.let {
                            Order(
                                id = doc.id,
                                title = it.projectTitle,
                                status = it.status,
                                price = String.format("%.2f", it.price),
                                rate = ""
                            )
                        }
                    } catch (e: Exception) {
                        null
                    }
                }

                // Apply search filter
                if (!search.isNullOrBlank()) {
                    orders = orders.filter { order ->
                        order.title.contains(search, ignoreCase = true) ||
                                order.id.contains(search, ignoreCase = true)
                    }
                }

                trySend(Resource.Success(orders))
            }
        }

        awaitClose { listener.remove() }
    }

    // Get orders (one-time fetch)
    override fun getOrders(status: String?, search: String?): Flow<Resource<List<Order>>> = flow {
        try {
            emit(Resource.Loading())

            val userId = auth.currentUser?.uid
            if (userId == null) {
                emit(Resource.Error("Not authenticated"))
                return@flow
            }

            var query: Query = ordersCollection
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)

            if (status != null) {
                query = query.whereEqualTo("status", status)
            }

            val snapshot = query.get().await()
            var orders = snapshot.documents.mapNotNull { doc ->
                try {
                    val orderData = doc.toObject(OrderData::class.java)
                    orderData?.let {
                        Order(
                            id = doc.id,
                            title = it.projectTitle,
                            status = it.status,
                            price = String.format("%.2f", it.price),
                            rate = ""
                        )
                    }
                } catch (e: Exception) {
                    null
                }
            }

            if (!search.isNullOrBlank()) {
                orders = orders.filter { order ->
                    order.title.contains(search, ignoreCase = true) ||
                            order.id.contains(search, ignoreCase = true)
                }
            }

            emit(Resource.Success(orders))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to load orders"))
        }
    }

    // Create new order
    override fun createOrder(request: CreateOrderRequest): Flow<Resource<Order>> = flow {
        try {
            emit(Resource.Loading())

            val userId = auth.currentUser?.uid
            if (userId == null) {
                emit(Resource.Error("Not authenticated"))
                return@flow
            }

            // Calculate price using the new pricing system
            val price = PriceCalculator.calculatePrice(
                serviceType = request.serviceType,
                deliveryTime = request.deliveryTimeLabel,
                wordCount = request.wordCount,
                promotionCode = request.promotionCode
            )

            // Calculate due date
            val dueDate = Calendar.getInstance().apply {
                add(Calendar.HOUR_OF_DAY, request.deliveryTime)
            }.time

            // Generate order number
            val orderNumber = "ORD${System.currentTimeMillis()}"

            // Create order data
            val orderData = OrderData(
                userId = userId,
                orderNumber = orderNumber,
                projectTitle = request.projectTitle,
                serviceType = request.serviceType,
                status = OrderStatus.INCOMPLETE,
                price = price,
                deliveryTime = request.deliveryTime,
                deliveryTimeLabel = request.deliveryTimeLabel,
                wordCount = request.wordCount,
                englishVariant = request.englishVariant,
                projectDescription = request.projectDescription,
                promotionCode = request.promotionCode,
                fileUrl = request.fileUrl,
                fileName = request.fileUrl?.substringAfterLast("/"),
                dueDate = Timestamp(dueDate)
            )

            // Save to Firestore
            val docRef = ordersCollection.add(orderData).await()

            val order = Order(
                id = docRef.id,
                title = request.projectTitle,
                status = OrderStatus.INCOMPLETE,
                price = String.format("%.2f", price),
                rate = ""
            )

            emit(Resource.Success(order))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to create order"))
        }
    }

    // Delete order
    override suspend fun deleteOrder(orderId: String): Resource<Unit> {
        return try {
            val userId = auth.currentUser?.uid
            if (userId == null) {
                return Resource.Error("Not authenticated")
            }

            // Verify order belongs to user
            val doc = ordersCollection.document(orderId).get().await()
            val orderData = doc.toObject(OrderData::class.java)

            if (orderData?.userId != userId) {
                return Resource.Error("Unauthorized")
            }

            ordersCollection.document(orderId).delete().await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to delete order")
        }
    }

    // Complete order (update status)
    override fun completeOrder(orderId: String): Flow<Resource<Order>> = flow {
        try {
            emit(Resource.Loading())

            val userId = auth.currentUser?.uid
            if (userId == null) {
                emit(Resource.Error("Not authenticated"))
                return@flow
            }

            // Verify order belongs to user
            val doc = ordersCollection.document(orderId).get().await()
            val orderData = doc.toObject(OrderData::class.java)

            if (orderData?.userId != userId) {
                emit(Resource.Error("Unauthorized"))
                return@flow
            }

            // Update status
            ordersCollection.document(orderId).update(
                mapOf(
                    "status" to OrderStatus.COMPLETE,
                    "updatedAt" to Timestamp.now()
                )
            ).await()

            val order = Order(
                id = orderId,
                title = orderData.projectTitle,
                status = OrderStatus.COMPLETE,
                price = String.format("%.2f", orderData.price),
                rate = ""
            )

            emit(Resource.Success(order))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to complete order"))
        }
    }

    // Get order by ID
    override suspend fun getOrderById(orderId: String): Resource<OrderData> {
        return try {
            val userId = auth.currentUser?.uid ?: return Resource.Error("Not authenticated")

            val doc = ordersCollection.document(orderId).get().await()
            val orderData = doc.toObject(OrderData::class.java)

            if (orderData != null && orderData.userId == userId) {
                Resource.Success(orderData)
            } else {
                Resource.Error("Order not found")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to fetch order")
        }
    }

    // Update order
    override suspend fun updateOrder(orderId: String, updates: Map<String, Any>): Resource<Unit> {
        return try {
            val userId = auth.currentUser?.uid
            if (userId == null) {
                return Resource.Error("Not authenticated")
            }

            // Verify order belongs to user
            val doc = ordersCollection.document(orderId).get().await()
            val orderData = doc.toObject(OrderData::class.java)

            if (orderData?.userId != userId) {
                return Resource.Error("Unauthorized")
            }

            val updatesWithTimestamp = updates.toMutableMap()
            updatesWithTimestamp["updatedAt"] = Timestamp.now()

            ordersCollection.document(orderId).update(updatesWithTimestamp).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to update order")
        }
    }
}