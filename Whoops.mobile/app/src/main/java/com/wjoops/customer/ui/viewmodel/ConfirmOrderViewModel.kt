package com.wjoops.customer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wjoops.customer.data.repositories.BasketRepository
import com.wjoops.customer.data.repositories.OrderRepository
import com.wjoops.customer.domain.models.Order
import com.wjoops.customer.domain.models.OrderDraft
import com.wjoops.customer.util.ApiResult
import com.wjoops.customer.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ConfirmOrderState(
    val draft: UiState<OrderDraft> = UiState.Loading,
    val placing: UiState<Order>? = null,
)

@HiltViewModel
class ConfirmOrderViewModel @Inject constructor(
    private val basketRepository: BasketRepository,
    private val orderRepository: OrderRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(ConfirmOrderState())
    val state: StateFlow<ConfirmOrderState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val basket = basketRepository.observeBasket().first()
            _state.value = ConfirmOrderState(draft = UiState.Success(OrderDraft(basket = basket)))
        }
    }

    fun placeOrder(onSuccess: (Order) -> Unit) {
        viewModelScope.launch {
            val draft = when (val d = _state.value.draft) {
                is UiState.Success -> d.data
                else -> return@launch
            }
            if (draft.basket.items.isEmpty()) {
                _state.value = _state.value.copy(placing = UiState.Error("Basket is empty", canRetry = false))
                return@launch
            }
            _state.value = _state.value.copy(placing = UiState.Loading)
            when (val res = orderRepository.placeOrder(draft)) {
                is ApiResult.Success -> {
                    _state.value = _state.value.copy(placing = UiState.Success(res.value))
                    onSuccess(res.value)
                }
                is ApiResult.Error -> {
                    _state.value = _state.value.copy(placing = UiState.Error("Failed to place order"))
                }
            }
        }
    }
}
