package com.example.whoopsmobile.model

data class BasketItem(
    val id: String,
    val item: Item,
    val quantity: Int,
    val addedIngredients: List<Ingredient> = emptyList(),
    val removedIngredients: List<Ingredient> = emptyList(),
    val comboSelections: List<ComboSelection> = emptyList()
) {
    val isCombo: Boolean get() = comboSelections.isNotEmpty()

    val extrasTotal: Int get() = addedIngredients.sumOf { it.extraPriceIsk }

    val comboExtrasTotal: Int get() = comboSelections.sumOf { sel ->
        sel.priceDelta + sel.addedIngredients.sumOf { it.extraPriceIsk }
    }

    val lineTotal: Int get() = (item.priceIsk + extrasTotal + comboExtrasTotal) * quantity
}
