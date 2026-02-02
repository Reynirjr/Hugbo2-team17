package com.wjoops.customer.ui.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wjoops.customer.data.repositories.BasketRepository
import com.wjoops.customer.data.repositories.MenuRepository
import com.wjoops.customer.domain.models.MenuCategory
import com.wjoops.customer.domain.models.MenuItem
import com.wjoops.customer.domain.models.Restaurant
import com.wjoops.customer.domain.models.WaitTime
import com.wjoops.customer.location.LocationService
import com.wjoops.customer.util.ApiResult
import com.wjoops.customer.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MenuScreenState(
    val restaurant: UiState<Restaurant> = UiState.Loading,
    val menu: UiState<Pair<List<MenuCategory>, List<MenuItem>>> = UiState.Loading,
    val waitTime: UiState<WaitTime> = UiState.Loading,
    val vegetarianOnly: Boolean = false,
    val basketCount: Int = 0,
    val basketTotal: String = "0 ISK",
    val distanceLabel: String? = null,
)

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val menuRepository: MenuRepository,
    private val basketRepository: BasketRepository,
    private val locationService: LocationService,
) : ViewModel() {
    private val _state = MutableStateFlow(MenuScreenState())
    val state: StateFlow<MenuScreenState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            basketRepository.observeBasket().collect { basket ->
                _state.value = _state.value.copy(
                    basketCount = basket.items.sumOf { it.quantity },
                    basketTotal = "${basket.totals.total} ${basket.totals.currency}",
                )
            }
        }
        refresh()
    }

    fun toggleVegetarianOnly() {
        _state.value = _state.value.copy(vegetarianOnly = !_state.value.vegetarianOnly)
    }

    fun refresh() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                restaurant = UiState.Loading,
                menu = UiState.Loading,
                waitTime = UiState.Loading,
            )

            when (val res = menuRepository.getRestaurant()) {
                is ApiResult.Success -> _state.value = _state.value.copy(restaurant = UiState.Success(res.value))
                is ApiResult.Error -> _state.value = _state.value.copy(restaurant = UiState.Error("Failed to load restaurant"))
            }
            when (val res = menuRepository.getMenu()) {
                is ApiResult.Success -> _state.value = _state.value.copy(menu = UiState.Success(res.value))
                is ApiResult.Error -> _state.value = _state.value.copy(menu = UiState.Error("Failed to load menu"))
            }
            when (val res = menuRepository.getWaitTime()) {
                is ApiResult.Success -> _state.value = _state.value.copy(waitTime = UiState.Success(res.value))
                is ApiResult.Error -> _state.value = _state.value.copy(waitTime = UiState.Error("Failed to load wait time"))
            }
        }
    }

    fun addToBasket(item: MenuItem) {
        viewModelScope.launch { basketRepository.addItem(item) }
    }

    fun requestDistanceUpdate(restaurant: Restaurant) {
        viewModelScope.launch {
            val location = locationService.getLastKnownLocation().getOrNull()
            _state.value = _state.value.copy(distanceLabel = location?.let { computeDistanceLabel(it, restaurant) })
        }
    }

    private fun computeDistanceLabel(location: Location, restaurant: Restaurant): String {
        val result = FloatArray(1)
        Location.distanceBetween(location.latitude, location.longitude, restaurant.lat, restaurant.lon, result)
        val meters = result[0]
        return if (meters >= 1000f) {
            String.format("%.1f km", meters / 1000f)
        } else {
            "${meters.toInt()} m"
        }
    }
}
