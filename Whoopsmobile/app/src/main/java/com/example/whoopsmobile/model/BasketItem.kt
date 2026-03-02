package com.example.whoopsmobile.model

data class BasketItem(
    val id: String,
    val item: Item,
    val quantity: Int
) {
    val lineTotal: Int get() = item.priceIsk * quantity
}
