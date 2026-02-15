package com.example.whoopsmobile.model

data class MenuSection(
    val id: Int,
    val name: String,
    val displayOrder: Int,
    val items: List<Item>
)