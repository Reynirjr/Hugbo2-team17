package com.wjoops.customer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wjoops.customer.data.repositories.OrderRepository
import com.wjoops.customer.domain.models.Order
import com.wjoops.customer.util.ApiResult
import com.wjoops.customer.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OrderStatusState(
    val latest: UiState<Order?> = UiState.Loading,
    val refreshState: UiState<Unit>? = null,
)

@HiltViewModel
class OrderStatusViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(OrderStatusState())
    val state: StateFlow<OrderStatusState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(latest = UiState.Loading)
            when (val res = orderRepository.getLatestOrder()) {
                is ApiResult.Success -> _state.value = _state.value.copy(latest = UiState.Success(res.value))
                is ApiResult.Error -> _state.value = _state.value.copy(latest = UiState.Error("Failed to load latest order"))
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            val current = (_state.value.latest as? UiState.Success)?.data
            val id = current?.id ?: run {
                _state.value = _state.value.copy(refreshState = UiState.Error("No order to refresh", canRetry = false))
                return@launch
            }
            _state.value = _state.value.copy(refreshState = UiState.Loading)
            when (val res = orderRepository.refreshOrderStatus(id)) {
                is ApiResult.Success -> {
                    _state.value = _state.value.copy(
                        latest = UiState.Success(res.value),
                        refreshState = UiState.Success(Unit),
                    )
                }
                is ApiResult.Error -> {
                    _state.value = _state.value.copy(refreshState = UiState.Error("Failed to refresh"))
                }
            }
        }
    }
}
