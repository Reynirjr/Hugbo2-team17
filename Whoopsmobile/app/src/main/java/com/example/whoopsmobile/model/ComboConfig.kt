package com.example.whoopsmobile.model

/**
 * Configuration for one step in a combo builder wizard.
 */
data class ComboStepConfig(
    val stepLabel: String,
    val choices: List<ComboChoice>,
    val count: Int = 1,
    val allowCustomization: Boolean = true
)

data class ComboChoice(
    val item: Item,
    val priceDelta: Int = 0,
    val customizationMode: CustomizationMode = CustomizationMode.FULL
)

enum class CustomizationMode {
    FULL,
    KIDS,
    NONE
}
