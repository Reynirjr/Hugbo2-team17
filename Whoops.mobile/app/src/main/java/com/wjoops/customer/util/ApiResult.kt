package com.wjoops.customer.util

sealed class ApiResult<out T> {
    data class Success<T>(val value: T) : ApiResult<T>()
    data class Error(val error: ApiError) : ApiResult<Nothing>()
}

sealed class ApiError {
    data class Http(val code: Int, val body: String? = null) : ApiError()
    data object Network : ApiError()
    data class Unknown(val message: String? = null, val cause: Throwable? = null) : ApiError()
}
