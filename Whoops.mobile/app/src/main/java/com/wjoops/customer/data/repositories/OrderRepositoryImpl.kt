package com.wjoops.customer.data.repositories

import com.wjoops.customer.domain.models.Order
import com.wjoops.customer.domain.models.OrderDraft
import com.wjoops.customer.domain.models.OrderStatus
import com.wjoops.customer.util.ApiResult
import kotlinx.coroutines.delay
import java.util.UUID
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor() : OrderRepository {
    private var latest: Order? = null

    override suspend fun placeOrder(draft: OrderDraft): ApiResult<Order> {
        // TODO: call POST /orders
        delay(600)
        val now = System.currentTimeMillis()
        val order = Order(
            id = "ord-${UUID.randomUUID()}".take(18),
            createdAt = now,
            status = OrderStatus.PENDING,
            items = draft.basket.items,
            totals = draft.basket.totals,
            estimatedReadyAt = now + 20 * 60_000,
        )
        latest = order
        return ApiResult.Success(order)
    }

    override suspend fun getLatestOrder(): ApiResult<Order?> {
        // TODO: call GET /orders/latest
        delay(200)
        return ApiResult.Success(latest)
    }

    override suspend fun refreshOrderStatus(orderId: String): ApiResult<Order> {
        // Stub: advances status on each refresh.
        delay(250)
        val order = latest
        if (order == null || order.id != orderId) {
            return ApiResult.Error(com.wjoops.customer.util.ApiError.Unknown("Order not found"))
        }
        val nextStatus = when (order.status) {
            OrderStatus.PENDING -> OrderStatus.PREPARING
            OrderStatus.PREPARING -> OrderStatus.READY
            OrderStatus.READY -> OrderStatus.READY
        }
        val next = order.copy(status = nextStatus)
        latest = next
        return ApiResult.Success(next)
    }
}
