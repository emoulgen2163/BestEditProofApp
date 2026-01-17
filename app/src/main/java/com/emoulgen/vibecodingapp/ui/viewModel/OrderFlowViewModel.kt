package com.emoulgen.vibecodingapp.ui.viewModel

import androidx.lifecycle.ViewModel
import com.emoulgen.vibecodingapp.domain.model.OrderDraft
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class OrderFlowViewModel @Inject constructor() : ViewModel() {

    private val _orderDraft = MutableStateFlow(OrderDraft())
    val orderDraft = _orderDraft.asStateFlow()

    fun updateDraft(updated: OrderDraft) {
        _orderDraft.value = updated
    }

    fun resetDraft() {
        _orderDraft.value = OrderDraft()
    }
}
