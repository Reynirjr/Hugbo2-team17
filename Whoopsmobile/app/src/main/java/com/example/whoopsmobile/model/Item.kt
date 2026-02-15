package com.example.whoopsmobile.model

data class Item(
    val id: Int,
    val name: String,
    val description: String,
    val priceIsk: Int,
    val available: Boolean,
    val tags: String,
    val imageData: String?
)
