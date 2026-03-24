package com.example.whoopsmobile.service

import com.example.whoopsmobile.data.api.ApiHelper
import com.example.whoopsmobile.data.api.OrderLine
import com.example.whoopsmobile.model.Order
import java.util.Calendar
import java.util.TimeZone
import java.text.SimpleDateFormat
import java.util.Locale

object OrderService {

    private const val DEFAULT_QUEUE_MINUTES = 20

    fun createOrder(): Order? {
        val phone = SessionManager.customerPhone ?: return null
        val menuId = SessionManager.currentMenuId
        val basket = BasketService.getBasket()
        if (basket.items.isEmpty()) return null
        val totalIsk = BasketService.calculateTotal()
        val queueMinutes = SessionManager.restaurantQueueMinutes ?: DEFAULT_QUEUE_MINUTES
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        cal.add(Calendar.MINUTE, queueMinutes)
        val estimatedReadyAt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }.format(cal.time)
        val lines = basket.items.map { bi ->
            if (bi.isCombo) {
                val details = bi.comboSelections.joinToString("; ") { sel ->
                    val parts = mutableListOf(sel.item.name)
                    if (sel.priceDelta > 0) parts.add("+${sel.priceDelta}kr")
                    sel.addedIngredients.forEach { parts.add("+${it.name}") }
                    sel.removedIngredients.forEach { parts.add("-${it.name}") }
                    parts.joinToString(" ")
                }
                OrderLine(
                    itemId = bi.item.id.toLong(),
                    itemName = "${bi.item.name} (${details})",
                    priceIsk = bi.item.priceIsk + bi.comboExtrasTotal,
                    quantity = bi.quantity
                )
            } else {
                OrderLine(
                    itemId = bi.item.id.toLong(),
                    itemName = bi.item.name,
                    priceIsk = bi.item.priceIsk + bi.extrasTotal,
                    quantity = bi.quantity
                )
            }
        }
        return ApiHelper().createOrder(phone, menuId, totalIsk, estimatedReadyAt, lines)
    }

    fun getOrderStatus(orderId: Long): Order? = ApiHelper().getOrderStatus(orderId)
}
