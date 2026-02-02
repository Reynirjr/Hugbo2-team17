package com.wjoops.customer.data.network

import com.wjoops.customer.data.network.dto.AuthRequestOtpDto
import com.wjoops.customer.data.network.dto.AuthVerifyOtpDto
import com.wjoops.customer.data.network.dto.MenuResponseDto
import com.wjoops.customer.data.network.dto.OrderDto
import com.wjoops.customer.data.network.dto.PlaceOrderDto
import com.wjoops.customer.data.network.dto.RestaurantDto
import com.wjoops.customer.data.network.dto.WaitTimeDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface WjoopsApiService {
    @GET("restaurant")
    suspend fun getRestaurant(): RestaurantDto

    @GET("menu")
    suspend fun getMenu(): MenuResponseDto

    @GET("wait-time")
    suspend fun getWaitTime(): WaitTimeDto

    @POST("auth/request-otp")
    suspend fun requestOtp(@Body body: AuthRequestOtpDto)

    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body body: AuthVerifyOtpDto): Map<String, String>

    @POST("orders")
    suspend fun placeOrder(@Body body: PlaceOrderDto): OrderDto

    @GET("orders/latest")
    suspend fun getLatestOrder(): OrderDto

    @GET("orders/{id}")
    suspend fun getOrder(@Path("id") id: String): OrderDto
}
