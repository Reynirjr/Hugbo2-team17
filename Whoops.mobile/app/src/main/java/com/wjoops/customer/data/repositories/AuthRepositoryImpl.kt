package com.wjoops.customer.data.repositories

import com.wjoops.customer.data.datastore.AppDataStore
import com.wjoops.customer.data.datastore.AuthStore
import com.wjoops.customer.domain.models.AuthState
import com.wjoops.customer.util.ApiResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authStore: AuthStore,
) : AuthRepository, TokenProvider {

    override fun observeAuthState(): Flow<AuthState> = authStore.authState

    override suspend fun getAccessToken(): String? {
        // Token is stored in DataStore; interceptor will read via TokenProvider.
        // In a real app you'd cache in memory.
        return authStore.authState.first().accessToken
    }

    override suspend fun requestOtp(phone: String): ApiResult<Unit> {
        // Mock OTP request. TODO: call /auth/request-otp
        delay(400)
        authStore.updateAuth(AuthState(phone = phone, isLoggedIn = false))
        return ApiResult.Success(Unit)
    }

    override suspend fun verifyOtp(phone: String, otp: String): ApiResult<Unit> {
        // Mock verify. Accept any 4-6 digit OTP.
        delay(400)
        val ok = otp.length in 4..6
        return if (ok) {
            authStore.updateAuth(
                AuthState(
                    phone = phone,
                    isLoggedIn = true,
                    accessToken = "fake_access_${UUID.randomUUID()}",
                    refreshToken = "fake_refresh_${UUID.randomUUID()}",
                    userId = "user-${phone.takeLast(4)}",
                ),
            )
            ApiResult.Success(Unit)
        } else {
            ApiResult.Error(com.wjoops.customer.util.ApiError.Unknown("Invalid OTP"))
        }
    }

    override suspend fun logout() {
        authStore.logout()
    }
}

