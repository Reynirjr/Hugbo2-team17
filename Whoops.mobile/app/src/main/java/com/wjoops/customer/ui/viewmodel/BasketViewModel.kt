package com.wjoops.customer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wjoops.customer.data.repositories.BasketRepository
import com.wjoops.customer.domain.models.Basket
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BasketScreenState(
    val basket: Basket? = null,
)

@HiltViewModel
class BasketViewModel @Inject constructor(
    private val basketRepository: BasketRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(BasketScreenState())
    val state: StateFlow<BasketScreenState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            basketRepository.observeBasket().collect { basket ->
                _state.value = BasketScreenState(basket = basket)
            }
        }
    }

    fun inc(menuItemId: String) {
        val item = _state.value.basket?.items?.firstOrNull { it.menuItem.id == menuItemId } ?: return
        viewModelScope.launch { basketRepository.updateQty(menuItemId, item.quantity + 1) }
    }

    fun dec(menuItemId: String) {
        val item = _state.value.basket?.items?.firstOrNull { it.menuItem.id == menuItemId } ?: return
        viewModelScope.launch { basketRepository.updateQty(menuItemId, item.quantity - 1) }
    }

    fun remove(menuItemId: String) {
        viewModelScope.launch { basketRepository.removeItem(menuItemId) }
    }
}
