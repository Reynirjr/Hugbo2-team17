package com.example.whoopsmobile.data

import com.example.whoopsmobile.model.ComboChoice
import com.example.whoopsmobile.model.ComboStepConfig
import com.example.whoopsmobile.model.CustomizationMode

/**
 * Defines the multi-step combo configurations for each tilbod item.
 * References items from HagaMenuData by ID.
 */
object ComboDefinitions {

    private val comboItemIds = setOf(1, 2, 3, 4)

    fun isCombo(itemId: Int): Boolean = itemId in comboItemIds

    /** Standard drink choices for adult combos - all included at no extra cost */
    private fun adultDrinkChoices(): List<ComboChoice> {
        val items = HagaMenuData.items
        fun itemById(id: Int) = items.first { it.id == id }
        return listOf(
            ComboChoice(itemById(20), priceDelta = 0, customizationMode = CustomizationMode.NONE),  // Coca Cola
            ComboChoice(itemById(21), priceDelta = 0, customizationMode = CustomizationMode.NONE),  // Coca Cola Zero
            ComboChoice(itemById(22), priceDelta = 0, customizationMode = CustomizationMode.NONE),  // Sprite
            ComboChoice(itemById(23), priceDelta = 0, customizationMode = CustomizationMode.NONE),  // Appelsín
            ComboChoice(itemById(24), priceDelta = 0, customizationMode = CustomizationMode.NONE),  // Toppur
            ComboChoice(itemById(25), priceDelta = 0, customizationMode = CustomizationMode.NONE),  // Bon Aqua
            ComboChoice(itemById(26), priceDelta = 0, customizationMode = CustomizationMode.NONE),  // Eplasafi
            ComboChoice(itemById(27), priceDelta = 0, customizationMode = CustomizationMode.NONE)   // Appelsínusafi
        )
    }

    /** Kids drink choices - juice included, 330ml drinks cost +100kr */
    private fun kidsDrinkChoices(): List<ComboChoice> {
        val items = HagaMenuData.items
        fun itemById(id: Int) = items.first { it.id == id }
        return listOf(
            ComboChoice(itemById(26), priceDelta = 0, customizationMode = CustomizationMode.NONE),    // Eplasafi (included)
            ComboChoice(itemById(27), priceDelta = 0, customizationMode = CustomizationMode.NONE),    // Appelsínusafi (included)
            ComboChoice(itemById(20), priceDelta = 100, customizationMode = CustomizationMode.NONE),  // Coca Cola (+100)
            ComboChoice(itemById(21), priceDelta = 100, customizationMode = CustomizationMode.NONE),  // Coca Cola Zero (+100)
            ComboChoice(itemById(22), priceDelta = 100, customizationMode = CustomizationMode.NONE),  // Sprite (+100)
            ComboChoice(itemById(23), priceDelta = 100, customizationMode = CustomizationMode.NONE),  // Appelsín (+100)
            ComboChoice(itemById(24), priceDelta = 100, customizationMode = CustomizationMode.NONE),  // Toppur (+100)
            ComboChoice(itemById(25), priceDelta = 100, customizationMode = CustomizationMode.NONE)   // Bon Aqua (+100)
        )
    }

    /** Standard fries choices for combos */
    private fun friesChoices(): List<ComboChoice> {
        val items = HagaMenuData.items
        fun itemById(id: Int) = items.first { it.id == id }
        return listOf(
            ComboChoice(itemById(13), priceDelta = 0, customizationMode = CustomizationMode.NONE),    // Franskar lítill (included)
            ComboChoice(itemById(14), priceDelta = 700, customizationMode = CustomizationMode.NONE),  // Franskar stór (+700)
            ComboChoice(itemById(15), priceDelta = 600, customizationMode = CustomizationMode.NONE),  // Spæsí franskar (+600)
            ComboChoice(itemById(16), priceDelta = 600, customizationMode = CustomizationMode.NONE)   // Osta franskar (+600)
        )
    }

    fun getSteps(comboItemId: Int): List<ComboStepConfig> {
        val items = HagaMenuData.items
        fun itemById(id: Int) = items.first { it.id == id }

        return when (comboItemId) {
            // Börger Tilboð (2690kr) - single patty burgers
            2 -> listOf(
                ComboStepConfig(
                    stepLabel = "Veldu börger",
                    choices = listOf(
                        ComboChoice(itemById(5), customizationMode = CustomizationMode.FULL),  // Hagabörger einfaldur
                        ComboChoice(itemById(7), customizationMode = CustomizationMode.FULL),  // Laukbörger einfaldur
                        ComboChoice(itemById(9), customizationMode = CustomizationMode.FULL)   // Ostabörger einfaldur
                    )
                ),
                ComboStepConfig(
                    stepLabel = "Veldu franskar",
                    choices = friesChoices(),
                    allowCustomization = false
                ),
                ComboStepConfig(
                    stepLabel = "Veldu drykk",
                    choices = adultDrinkChoices(),
                    allowCustomization = false
                )
            )

            // Double Börger Tilboð (3290kr) - double patty burgers
            1 -> listOf(
                ComboStepConfig(
                    stepLabel = "Veldu börger",
                    choices = listOf(
                        ComboChoice(itemById(6), customizationMode = CustomizationMode.FULL),  // Hagabörger tvöfaldur
                        ComboChoice(itemById(8), customizationMode = CustomizationMode.FULL),  // Laukbörger tvöfaldur
                        ComboChoice(itemById(10), customizationMode = CustomizationMode.FULL)  // Ostabörger tvöfaldur
                    )
                ),
                ComboStepConfig(
                    stepLabel = "Veldu franskar",
                    choices = friesChoices(),
                    allowCustomization = false
                ),
                ComboStepConfig(
                    stepLabel = "Veldu drykk",
                    choices = adultDrinkChoices(),
                    allowCustomization = false
                )
            )

            // Barna börger tilboð (1590kr)
            4 -> listOf(
                ComboStepConfig(
                    stepLabel = "Barnabörger",
                    choices = listOf(
                        ComboChoice(itemById(11), customizationMode = CustomizationMode.KIDS)
                    )
                ),
                ComboStepConfig(
                    stepLabel = "Veldu franskar",
                    choices = friesChoices(),
                    allowCustomization = false
                ),
                ComboStepConfig(
                    stepLabel = "Veldu drykk",
                    choices = kidsDrinkChoices(),
                    allowCustomization = false
                )
            )

            // Fjölskyldutilboð (6990kr)
            3 -> listOf(
                ComboStepConfig(
                    stepLabel = "Veldu borgara",
                    choices = listOf(
                        ComboChoice(itemById(5), customizationMode = CustomizationMode.FULL),
                        ComboChoice(itemById(7), customizationMode = CustomizationMode.FULL),
                        ComboChoice(itemById(9), customizationMode = CustomizationMode.FULL)
                    ),
                    count = 2
                ),
                ComboStepConfig(
                    stepLabel = "Veldu barnaborgara",
                    choices = listOf(
                        ComboChoice(itemById(11), priceDelta = 0, customizationMode = CustomizationMode.KIDS),
                        // Upgrade options: pay +300 each to get an adult burger instead
                        ComboChoice(itemById(5), priceDelta = 300, customizationMode = CustomizationMode.FULL),
                        ComboChoice(itemById(7), priceDelta = 300, customizationMode = CustomizationMode.FULL),
                        ComboChoice(itemById(9), priceDelta = 300, customizationMode = CustomizationMode.FULL)
                    ),
                    count = 2
                )
                // No fries step - large fries included automatically, no changes
            )

            else -> emptyList()
        }
    }
}
