package com.wjoops.customer.util

import java.math.BigDecimal
import java.math.RoundingMode

data class Money(
    val amount: BigDecimal,
    val currency: String = "ISK",
) {
    fun format(): String {
        val scaled = amount.setScale(0, RoundingMode.HALF_UP)
        return "${scaled.toPlainString()} $currency"
    }
}
