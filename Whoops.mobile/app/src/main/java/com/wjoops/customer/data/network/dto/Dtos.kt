package com.wjoops.customer.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class RestaurantDto(
    val id: String,
    val name: String,
    val lat: Double,
    val lon: Double,
)

@Serializable
data class MenuCategoryDto(
    val id: String,
    val name: String,
    val sortOrder: Int,
)

@Serializable
data class MenuItemDto(
    val id: String,
    val name: String,
    val description: String,
    val price: Long,
    val isVegetarian: Boolean,
    val isAvailable: Boolean,
    val categoryId: String,
    val imageUrl: String? = null,
)

@Serializable
data class MenuResponseDto(
    val categories: List<MenuCategoryDto>,
    val items: List<MenuItemDto>,
)

@Serializable
data class WaitTimeDto(
    val estimatedMinutes: Int,
)

@Serializable
data class AuthRequestOtpDto(
    val phone: String,
)

@Serializable
data class AuthVerifyOtpDto(
    val phone: String,
    val otp: String,
)

@Serializable
data class PlaceOrderItemDto(
    val menuItemId: String,
    val quantity: Int,
)

@Serializable
data class PlaceOrderDto(
    val items: List<PlaceOrderItemDto>,
    val notes: String? = null,
)

@Serializable
data class OrderDto(
    val id: String,
    val createdAt: Long,
    val status: String,
    val items: List<PlaceOrderItemDto>,
    val subtotal: Long,
    val total: Long,
    val currency: String,
    val estimatedReadyAt: Long,
)
