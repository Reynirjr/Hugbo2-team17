package com.wjoops.customer.repositories

import app.cash.turbine.test
import com.wjoops.customer.data.datastore.AuthStore
import com.wjoops.customer.data.repositories.AuthRepositoryImpl
import com.wjoops.customer.domain.models.AuthState
import com.wjoops.customer.testutil.MainDispatcherRule
import com.wjoops.customer.util.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AuthRepositoryImplTest {
    @get:Rule val mainRule = MainDispatcherRule()

    @Test
    fun `verifyOtp marks logged in`() = runTest {
        val store = InMemoryAuthStore()
        val repo = AuthRepositoryImpl(authStore = store)

        val request = repo.requestOtp("+3545551234")
        assertTrue(request is ApiResult.Success)

        val verify = repo.verifyOtp("+3545551234", "1234")
        assertTrue(verify is ApiResult.Success)

        store.authState.test {
            val state = awaitItem()
            assertTrue(state.isLoggedIn)
            assertEquals("+3545551234", state.phone)
            cancelAndConsumeRemainingEvents()
        }
    }
}

private class InMemoryAuthStore : AuthStore {
    private val state = MutableStateFlow(AuthState())
    override val authState = state.asStateFlow()

    override suspend fun updateAuth(state: AuthState) {
        this.state.value = state
    }

    override suspend fun logout() {
        this.state.value = this.state.value.copy(isLoggedIn = false, accessToken = null, refreshToken = null, userId = null)
    }
}
