package com.emoulgen.vibecodingapp.ui.viewModel

import androidx.lifecycle.ViewModel
import com.emoulgen.vibecodingapp.domain.model.Order
import com.emoulgen.vibecodingapp.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AppStateViewModel : ViewModel() {
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> get() = _users

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> get() = _orders

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> get() = _currentUser

    fun registerUser(user: User): Boolean {
        if (_users.value.any { it.email == user.email }) return false
        _users.value += user
        return true
    }

    fun login(email: String, password: String): Boolean {
        val user = _users.value.find { it.email == email && it.password == password }
        _currentUser.value = user
        return user != null
    }

    fun logout() {
        _currentUser.value = null
    }

    fun addOrder(order: Order) {
        _orders.value += order
    }
}