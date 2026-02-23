package com.example.whoopsmobile.service

import com.example.whoopsmobile.data.api.ApiHelper
import com.example.whoopsmobile.data.api.OrderLine
import java.util.Calendar
import java.util.TimeZone
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * US3: Place order. Creates order in Supabase with customer_phone, menu_id, items, total, estimated_ready_at.
 */
object OrderService {

    private const val DEFAULT_QUEUE_MINUTES = 20

    /**
     * Creates order from current basket. Returns order id or null on failure.
     */
    fun createOrder(): Long? {
        val phone = SessionManager.customerPhone ?: return null
        val menuId = SessionManager.currentMenuId
        val basket = BasketService.getBasket()
        if (basket.items.isEmpty()) return null
        val totalIsk = BasketService.calculateTotal()
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        cal.add(Calendar.MINUTE, DEFAULT_QUEUE_MINUTES)
        val estimatedReadyAt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }.format(cal.time)
        val lines = basket.items.map { bi ->
            OrderLine(
                itemId = bi.item.id.toLong(),
                itemName = bi.item.name,
                priceIsk = bi.item.priceIsk,
                quantity = bi.quantity
            )
        }
        return ApiHelper().createOrder(phone, menuId, totalIsk, estimatedReadyAt, lines)
    }
}
