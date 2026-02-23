package com.example.whoopsmobile.model

/** Order as returned from API (UML: id, status, etc.). */
data class Order(
    val id: Long,
    val status: String
)