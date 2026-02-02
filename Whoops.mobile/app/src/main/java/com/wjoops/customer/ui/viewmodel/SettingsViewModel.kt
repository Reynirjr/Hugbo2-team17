package com.wjoops.customer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wjoops.customer.data.datastore.AppDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsState(
    val baseUrl: String = "",
    val saved: Boolean = false,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStore: AppDataStore,
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            dataStore.debugBaseUrl.collectLatest { url ->
                _state.value = _state.value.copy(baseUrl = url.orEmpty(), saved = false)
            }
        }
    }

    fun onBaseUrlChanged(value: String) {
        _state.value = _state.value.copy(baseUrl = value, saved = false)
    }

    fun saveBaseUrl() {
        viewModelScope.launch {
            dataStore.setDebugBaseUrl(_state.value.baseUrl)
            _state.value = _state.value.copy(saved = true)
        }
    }
}
