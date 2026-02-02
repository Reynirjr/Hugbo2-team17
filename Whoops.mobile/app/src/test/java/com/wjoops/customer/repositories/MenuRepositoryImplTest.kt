package com.wjoops.customer.repositories

import com.wjoops.customer.data.repositories.MenuRepositoryImpl
import com.wjoops.customer.testutil.MainDispatcherRule
import com.wjoops.customer.util.ApiResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class MenuRepositoryImplTest {
    @get:Rule val mainRule = MainDispatcherRule()

    @Test
    fun `getMenu returns mock data`() = runTest {
        val repo = MenuRepositoryImpl()
        val res = repo.getMenu()
        assertTrue(res is ApiResult.Success)
    }
}
