package com.example.whoopsmobile.model

/**
 * Represents one sub-item chosen within a combo/tilbod meal.
 * E.g. the burger, the fries, and the drink each become a ComboSelection.
 */
data class ComboSelection(
    val stepLabel: String,
    val item: Item,
    val priceDelta: Int = 0,
    val addedIngredients: List<Ingredient> = emptyList(),
    val removedIngredients: List<Ingredient> = emptyList()
)
