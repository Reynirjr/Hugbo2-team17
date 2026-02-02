package com.wjoops.customer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wjoops.customer.data.repositories.AuthRepository
import com.wjoops.customer.util.ApiResult
import com.wjoops.customer.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginScreenState(
    val phone: String = "",
    val requestState: UiState<Unit>? = null,
)

data class OtpScreenState(
    val otp: String = "",
    val verifyState: UiState<Unit>? = null,
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _loginState = MutableStateFlow(LoginScreenState())
    val loginState: StateFlow<LoginScreenState> = _loginState.asStateFlow()

    private val _otpState = MutableStateFlow(OtpScreenState())
    val otpState: StateFlow<OtpScreenState> = _otpState.asStateFlow()

    fun onPhoneChanged(value: String) {
        _loginState.value = _loginState.value.copy(phone = value)
    }

    fun requestOtp(onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _loginState.value = _loginState.value.copy(requestState = UiState.Loading)
            when (val res = authRepository.requestOtp(_loginState.value.phone)) {
                is ApiResult.Success -> {
                    _loginState.value = _loginState.value.copy(requestState = UiState.Success(Unit))
                    onSuccess(_loginState.value.phone)
                }
                is ApiResult.Error -> {
                    _loginState.value = _loginState.value.copy(requestState = UiState.Error("Could not request OTP"))
                }
            }
        }
    }

    fun onOtpChanged(value: String) {
        _otpState.value = _otpState.value.copy(otp = value)
    }

    fun verifyOtp(phone: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _otpState.value = _otpState.value.copy(verifyState = UiState.Loading)
            when (val res = authRepository.verifyOtp(phone, _otpState.value.otp)) {
                is ApiResult.Success -> {
                    _otpState.value = _otpState.value.copy(verifyState = UiState.Success(Unit))
                    onSuccess()
                }
                is ApiResult.Error -> {
                    _otpState.value = _otpState.value.copy(verifyState = UiState.Error("Invalid OTP"))
                }
            }
        }
    }

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onDone()
        }
    }
}
