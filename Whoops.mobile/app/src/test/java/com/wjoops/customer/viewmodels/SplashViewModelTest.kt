package com.wjoops.customer.viewmodels

import app.cash.turbine.test
import com.wjoops.customer.data.repositories.AuthRepository
import com.wjoops.customer.domain.models.AuthState
import com.wjoops.customer.testutil.MainDispatcherRule
import com.wjoops.customer.ui.viewmodel.SplashViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SplashViewModelTest {
    @get:Rule val mainRule = MainDispatcherRule()

    @Test
    fun `checkAuth emits success`() = runTest {
        val repo = FakeAuthRepository(AuthState(isLoggedIn = true))
        val vm = SplashViewModel(authRepository = repo)

        vm.state.test {
            vm.checkAuth()
            val s1 = awaitItem()
            val s2 = awaitItem()
            assertTrue(s2.auth is com.wjoops.customer.util.UiState.Success)
            cancelAndConsumeRemainingEvents()
        }
    }
}

private class FakeAuthRepository(initial: AuthState) : AuthRepository {
    private val state = MutableStateFlow(initial)
    override fun observeAuthState() = state.asStateFlow()
    override suspend fun requestOtp(phone: String) = com.wjoops.customer.util.ApiResult.Success(Unit)
    override suspend fun verifyOtp(phone: String, otp: String) = com.wjoops.customer.util.ApiResult.Success(Unit)
    override suspend fun logout() { state.value = state.value.copy(isLoggedIn = false) }
}
