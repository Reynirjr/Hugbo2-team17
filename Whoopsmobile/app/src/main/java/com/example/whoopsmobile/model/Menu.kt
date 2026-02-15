package com.example.whoopsmobile.model

data class Menu(
    val id: Int,
    val name: String,
    val currency: String,
    val sections: List<MenuSection>
)