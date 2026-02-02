package com.wjoops.customer.viewmodels

import com.wjoops.customer.data.repositories.AuthRepository
import com.wjoops.customer.domain.models.AuthState
import com.wjoops.customer.testutil.MainDispatcherRule
import com.wjoops.customer.ui.viewmodel.AuthViewModel
import com.wjoops.customer.util.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class AuthViewModelTest {
    @get:Rule val mainRule = MainDispatcherRule()

    @Test
    fun `requestOtp invokes success callback`() = runTest {
        val repo = FakeAuthRepo()
        val vm = AuthViewModel(authRepository = repo)
        vm.onPhoneChanged("+3545551234")

        var navigatedPhone: String? = null
        vm.requestOtp { navigatedPhone = it }

        // runTest waits for coroutines; callback should be invoked.
        assertEquals("+3545551234", navigatedPhone)
    }
}

private class FakeAuthRepo : AuthRepository {
    private val state = MutableStateFlow(AuthState())
    override fun observeAuthState() = state.asStateFlow()
    override suspend fun requestOtp(phone: String) = ApiResult.Success(Unit)
    override suspend fun verifyOtp(phone: String, otp: String) = ApiResult.Success(Unit)
    override suspend fun logout() {}
}
