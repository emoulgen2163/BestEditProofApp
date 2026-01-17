package com.emoulgen.vibecodingapp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emoulgen.vibecodingapp.domain.model.CreateOrderRequest
import com.emoulgen.vibecodingapp.domain.model.Order
import com.emoulgen.vibecodingapp.domain.model.OrderData
import com.emoulgen.vibecodingapp.domain.repository.FirestoreOrderRepository
import com.emoulgen.vibecodingapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FirestoreOrderViewModel @Inject constructor(
    private val repo: FirestoreOrderRepository
) : ViewModel() {

    private val _ordersState = MutableStateFlow<Resource<List<Order>>>(Resource.Loading())
    val ordersState: StateFlow<Resource<List<Order>>> = _ordersState

    private val _orderState = MutableStateFlow<Resource<OrderData>?>(null)
    val orderState: StateFlow<Resource<OrderData>?> = _orderState

    private val _createOrderState = MutableStateFlow<Resource<Order>?>(null)
    val createOrderState: StateFlow<Resource<Order>?> = _createOrderState

    private val _orderDetailsState = MutableStateFlow<Resource<OrderData>>(Resource.Loading())
    val orderDetailsState: StateFlow<Resource<OrderData>> = _orderDetailsState.asStateFlow()

    private val _updateOrderState = MutableStateFlow<Resource<Unit>>(Resource.Loading())
    val updateOrderState: StateFlow<Resource<Unit>> = _updateOrderState.asStateFlow()


    // Get all orders realtime or one-shot, with optional status/search
    fun getOrdersRealtime(status: String? = null, search: String? = null) {
        viewModelScope.launch {
            repo.getOrdersRealtime(status, search)
                .collect { resource ->
                    _ordersState.value = resource
                }
        }
    }

    fun getOrders(status: String? = null, search: String? = null) {
        viewModelScope.launch {
            repo.getOrders(status, search)
                .collect { resource ->
                    _ordersState.value = resource
                }
        }
    }

    fun createOrder(request: CreateOrderRequest) {
        viewModelScope.launch {
            repo.createOrder(request)
                .collect { resource ->
                    _createOrderState.value = resource
                }
        }
    }

    fun deleteOrder(orderId: String, onResult: (Resource<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = repo.deleteOrder(orderId)
            onResult(result)
        }
    }

    fun completeOrder(orderId: String) {
        viewModelScope.launch {
            repo.completeOrder(orderId)
                .collect { /* handle result as needed, e.g. refresh orders */ }
        }
    }

    fun getOrderById(orderId: String) {
        viewModelScope.launch {
            _orderDetailsState.value = Resource.Loading()
            val result = repo.getOrderById(orderId)
            _orderDetailsState.value = result
        }
    }

    fun updateOrder(orderId: String, updates: Map<String, Any>) {
        viewModelScope.launch {
            _updateOrderState.value = Resource.Loading()
            val result = repo.updateOrder(orderId, updates)
            _updateOrderState.value = result
        }
    }

    fun resetCreateOrderState() {
        _createOrderState.value = null
    }
    fun resetOrderState() {
        _orderState.value = null
    }
}