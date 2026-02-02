package com.wjoops.customer.domain.models

import com.wjoops.customer.util.Money
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class Restaurant(
    val id: String,
    val name: String,
    val lat: Double,
    val lon: Double,
)

@Serializable
data class MenuCategory(
    val id: String,
    val name: String,
    val sortOrder: Int,
)

@Serializable
data class MenuItem(
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
data class BasketItem(
    val menuItem: MenuItem,
    val quantity: Int,
)

@Serializable
data class BasketTotals(
    val subtotal: Long,
    val total: Long,
    val currency: String,
)

@Serializable
data class Basket(
    val items: List<BasketItem>,
    val totals: BasketTotals,
)

@Serializable
data class OrderDraft(
    val basket: Basket,
    val pickupType: PickupType = PickupType.Pickup,
    val notes: String? = null,
)

@Serializable
enum class PickupType { Pickup }

@Serializable
data class Order(
    val id: String,
    val createdAt: Long,
    val status: OrderStatus,
    val items: List<BasketItem>,
    val totals: BasketTotals,
    val estimatedReadyAt: Long,
)

@Serializable
enum class OrderStatus { PENDING, PREPARING, READY }

@Serializable
data class WaitTime(
    val estimatedMinutes: Int,
)

@Serializable
data class AuthState(
    val phone: String? = null,
    val isLoggedIn: Boolean = false,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val userId: String? = null,
)

fun basketTotals(items: List<BasketItem>, currency: String = "ISK"): BasketTotals {
    val subtotal = items.sumOf { it.menuItem.price * it.quantity }
    return BasketTotals(subtotal = subtotal, total = subtotal, currency = currency)
}
