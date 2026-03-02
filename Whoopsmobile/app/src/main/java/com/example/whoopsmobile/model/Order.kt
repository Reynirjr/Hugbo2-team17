package com.example.whoopsmobile.model

/** Order as returned from API (UML: id, status, created_at, total_isk, etc.). */
data class Order(
    val id: Long,
    val status: String,
    val createdAt: String? = null,
    val customerPhone: String? = null,
    val menuId: Long? = null,
    val totalIsk: Int? = null,
    val estimatedReadyAt: String? = null
)