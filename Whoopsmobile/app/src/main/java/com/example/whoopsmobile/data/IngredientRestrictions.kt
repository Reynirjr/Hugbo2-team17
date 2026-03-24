package com.example.whoopsmobile.data

/**
 * Defines which items get no customization, restricted ingredients, or special UI modes.
 */
object IngredientRestrictions {

    /** Items with NO "Breyta" button at all */
    val noCustomization: Set<Int> = setOf(
        13, // Franskar lítill
        14, // Franskar stór
        17, // Sósur
        18, // Gos í dós
        19, // Safi
        20, 21, 22, 23, 24, 25, 26, 27, // Individual combo drinks
        1, 2, 3, 4  // Tilboð (combos handled by ComboBuilderFragment)
    )

    /**
     * Items with restricted ingredient lists.
     * Maps item ID -> set of allowed ingredient IDs.
     * Only these ingredients will show in the Breyta panel.
     */
    val allowedIngredientIds: Map<Int, Set<Int>> = mapOf(
        // Ostafranskar: cheddar ostasósa, siracha, vorlaukur, pikklaður chili
        16 to setOf(21, 8, 22, 23),
        // Spæsí franskar: mæjó, siracha, vorlaukur, pikklaður chili, karmellíseraður laukur
        15 to setOf(5, 8, 22, 23, 11),
        // Spæsí vegan vængir: hvitlauksmæjó, kokteilsósa, siracha, vorlaukur, pikklaður chili
        12 to setOf(24, 25, 8, 22, 23)
    )

    /** Items where sauce selection is radio-button (pick one) instead of checkboxes */
    val sauceRadioItemIds: Set<Int> = setOf(12)  // Spæsí vegan vængir

    /** Barnabörger: special customization rules */
    val kidsBurgerItemId: Int = 11
    val kidsBurgerAllowedExtraIds: Set<Int> = setOf(1, 2, 17) // Auka kjöt, Auka ostur, Auka vegan ostur
    val kidsBurgerSauceIds: Set<Int> = setOf(7, 5, 6) // Tómatsósa, Mæjó, Sinnep (radio)

    /**
     * Context-dependent price overrides.
     * Maps item ID -> (ingredient ID -> override price).
     * Karmellíseraður laukur costs +50 on spæsí franskar but is free on burgers.
     */
    val priceOverrides: Map<Int, Map<Int, Int>> = mapOf(
        15 to mapOf(11 to 50) // Spæsí franskar: karmellíseraður laukur +50kr
    )
}
