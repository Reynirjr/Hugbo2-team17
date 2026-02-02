package com.wjoops.customer.data.repositories

import com.wjoops.customer.domain.models.MenuCategory
import com.wjoops.customer.domain.models.MenuItem
import com.wjoops.customer.domain.models.Restaurant
import com.wjoops.customer.domain.models.WaitTime
import com.wjoops.customer.util.ApiResult
import kotlinx.coroutines.delay
import javax.inject.Inject

class MenuRepositoryImpl @Inject constructor() : MenuRepository {
    override suspend fun getRestaurant(): ApiResult<Restaurant> {
        // TODO: call GET /restaurant
        delay(150)
        return ApiResult.Success(FakeData.restaurant)
    }

    override suspend fun getMenu(): ApiResult<Pair<List<MenuCategory>, List<MenuItem>>> {
        // TODO: call GET /menu
        delay(250)
        return ApiResult.Success(FakeData.categories to FakeData.items)
    }

    override suspend fun getWaitTime(): ApiResult<WaitTime> {
        // TODO: call GET /wait-time
        delay(120)
        return ApiResult.Success(FakeData.waitTime)
    }
}
