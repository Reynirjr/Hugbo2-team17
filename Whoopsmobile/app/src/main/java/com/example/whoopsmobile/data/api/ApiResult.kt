package com.example.whoopsmobile.data.api
import com.example.whoopsmobile.model.Item

sealed class ApiResult {
    data class Success(val items: List<Item>) : ApiResult()
    object Empty : ApiResult()
    data class Error(val message: String) : ApiResult()
}