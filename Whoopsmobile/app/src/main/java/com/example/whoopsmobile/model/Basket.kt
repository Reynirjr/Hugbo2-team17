package com.example.whoopsmobile.model

import java.util.UUID

data class Basket(
    val id: String = UUID.randomUUID().toString(),
    val items: MutableList<BasketItem> = mutableListOf()
) {
    val totalItemCount: Int get() = items.sumOf { it.quantity }
}
