package com.example.whoopsmobile.data.api

import com.example.whoopsmobile.model.Item
import com.example.whoopsmobile.model.Restaurant

sealed class ApiResult {
    data class Success(val items: List<Item>) : ApiResult()
    data class Restaurants(val restaurants: List<Restaurant>) : ApiResult()
    object Empty : ApiResult()
    data class Error(val message: String) : ApiResult()
}