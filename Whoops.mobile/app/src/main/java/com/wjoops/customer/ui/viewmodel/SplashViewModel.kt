package com.wjoops.customer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wjoops.customer.data.repositories.AuthRepository
import com.wjoops.customer.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SplashScreenState(
    val auth: UiState<Boolean> = UiState.Loading,
)

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(SplashScreenState())
    val state: StateFlow<SplashScreenState> = _state.asStateFlow()

    fun checkAuth() {
        viewModelScope.launch {
            val isLoggedIn = authRepository.observeAuthState().first().isLoggedIn
            _state.value = SplashScreenState(auth = UiState.Success(isLoggedIn))
        }
    }
}
