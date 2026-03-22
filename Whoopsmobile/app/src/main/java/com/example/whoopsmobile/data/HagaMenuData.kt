package com.example.whoopsmobile.data

import com.example.whoopsmobile.model.Ingredient
import com.example.whoopsmobile.model.Item

/**
 * Default menu data for Haga-vagninn (Kjöt eða Vegan).
 * Used when the API returns no items so US1 (view menu) still works.
 */
object HagaMenuData {

    val ingredients: List<Ingredient> = listOf(
        Ingredient(1, "Auka kjöt", "extra", 590, 1),
        Ingredient(2, "Auka ostur", "extra", 100, 2),
        Ingredient(17, "Auka vegan ostur", "extra", 100, 3),
        Ingredient(3, "Ostur", "ostur", 0, 4),
        Ingredient(4, "Vegan ostur", "ostur", 0, 5),
        Ingredient(5, "Mæjó", "sosur", 0, 6),
        Ingredient(18, "Lítið mæjó", "sosur", 0, 7),
        Ingredient(6, "Sinnep", "sosur", 0, 8),
        Ingredient(19, "Lítið sinnep", "sosur", 0, 9),
        Ingredient(7, "Tómatsósa", "sosur", 0, 10),
        Ingredient(20, "Lítil tómatsósa", "sosur", 0, 11),
        Ingredient(8, "Siracha", "sosur", 0, 12),
        Ingredient(9, "Vegan mæjó", "sosur", 0, 13),
        Ingredient(10, "Relish", "sosur", 0, 14),
        Ingredient(11, "Karmellíseraður laukur", "alegg", 0, 15),
        Ingredient(12, "Pikklaður laukur", "alegg", 0, 16),
        Ingredient(13, "Pikklaðar gúrkur", "alegg", 0, 17),
        Ingredient(14, "Kál", "alegg", 0, 18),
        Ingredient(15, "Tómatur", "alegg", 0, 19),
        Ingredient(16, "Rauður laukur", "alegg", 0, 20)
    )

    /** Default ingredient IDs per item (matching fallback item IDs) */
    val itemIngredientDefaults: Map<Int, List<Int>> = mapOf(
        5 to listOf(3, 12, 13, 5, 6),   // Hagabörger einfaldur
        6 to listOf(3, 12, 13, 5, 6),   // Hagabörger tvöfaldur
        7 to listOf(3, 10, 11, 5, 6),   // Laukbörger einfaldur
        8 to listOf(3, 10, 11, 5, 6),   // Laukbörger tvöfaldur
        9 to listOf(3, 16, 14, 15, 5, 6, 7),  // Ostabörger einfaldur
        10 to listOf(3, 16, 14, 15, 5, 6, 7), // Ostabörger tvöfaldur
        11 to listOf(3, 7),              // Barnabörger
        12 to listOf(8, 9),             // Spæsí vegan vængir
        15 to listOf(9, 8),             // Spæsí franskar
        16 to listOf(3)                 // Ostafranskar
    )

    val items: List<Item> = listOf(
        // —— Máltíðir (Combo meals) ——
        Item(
            id = 1,
            name = "Double Börger",
            description = "Börger með tvöföldu kjöti og osti, franskar & gos",
            priceIsk = 3290,
            available = true,
            tags = "meat,vegan",
            imageData = null,
            estimatedWaitTimeMinutes = 10
        ),
        Item(
            id = 2,
            name = "Börger",
            description = "Börger, franskar & gos",
            priceIsk = 2690,
            available = true,
            tags = "meat,vegan",
            imageData = null,
            estimatedWaitTimeMinutes = 10
        ),
        Item(
            id = 3,
            name = "Fjölskyldu tilboð",
            description = "2x Börger, 2x barna börgerar, stórar franskar",
            priceIsk = 6990,
            available = true,
            tags = "meat,vegan",
            imageData = null,
            estimatedWaitTimeMinutes = 10
        ),
        Item(
            id = 4,
            name = "Barna börger",
            description = "Barna börger, franskar & djús (fyrir 12 ára og yngri)",
            priceIsk = 1590,
            available = true,
            tags = "meat,vegan",
            imageData = null,
            estimatedWaitTimeMinutes = 10
        ),
        // —— Borgarar (Burgers) ——
        Item(
            id = 5,
            name = "Hagabörger einfaldur",
            description = "Ostur, pikklaður laukur, pikklaðar gúrkur, mæjó & sinnep. Kjöt eða veganbuff.",
            priceIsk = 1690,
            available = true,
            tags = "meat,vegan",
            imageData = null,
            estimatedWaitTimeMinutes = 10
        ),
        Item(
            id = 6,
            name = "Hagabörger tvöfaldur",
            description = "Ostur, pikklaður laukur, pikklaðar gúrkur, mæjó & sinnep. Kjöt eða veganbuff.",
            priceIsk = 2490,
            available = true,
            tags = "meat,vegan",
            imageData = null,
            estimatedWaitTimeMinutes = 10
        ),
        Item(
            id = 7,
            name = "Laukbörger einfaldur",
            description = "Ostur, rautt relish, karmellíseraður laukur, mæjó & sinnep. Kjöt eða veganbuff.",
            priceIsk = 1690,
            available = true,
            tags = "meat,vegan",
            imageData = null,
            estimatedWaitTimeMinutes = 10
        ),
        Item(
            id = 8,
            name = "Laukbörger tvöfaldur",
            description = "Ostur, rautt relish, karmellíseraður laukur, mæjó & sinnep. Kjöt eða veganbuff.",
            priceIsk = 2490,
            available = true,
            tags = "meat,vegan",
            imageData = null,
            estimatedWaitTimeMinutes = 10
        ),
        Item(
            id = 9,
            name = "Ostabörger einfaldur",
            description = "Ostur, laukur, kál, tómatar, mæjó, sinnep & tómatSósa. Kjöt eða veganbuff.",
            priceIsk = 1690,
            available = true,
            tags = "meat,vegan",
            imageData = null,
            estimatedWaitTimeMinutes = 10
        ),
        Item(
            id = 10,
            name = "Ostabörger tvöfaldur",
            description = "Ostur, laukur, kál, tómatar, mæjó, sinnep & tómatSósa. Kjöt eða veganbuff.",
            priceIsk = 2490,
            available = true,
            tags = "meat,vegan",
            imageData = null,
            estimatedWaitTimeMinutes = 10
        ),
        Item(
            id = 11,
            name = "Barnabörger",
            description = "Kjöt eða veganbuff, ostur & tómatSósa",
            priceIsk = 1390,
            available = true,
            tags = "meat,vegan",
            imageData = null,
            estimatedWaitTimeMinutes = 10
        ),
        // —— Aukavörur (Sides) ——
        Item(
            id = 12,
            name = "Spæsí vegan vængir",
            description = "Blómkál, tempura, chili, graslaukur & sriracha",
            priceIsk = 1490,
            available = true,
            tags = "vegan",
            imageData = null,
            estimatedWaitTimeMinutes = 10
        ),
        Item(
            id = 13,
            name = "Franskar lítill",
            description = "Franskar",
            priceIsk = 790,
            available = true,
            tags = "vegan",
            imageData = null,
            estimatedWaitTimeMinutes = 10
        ),
        Item(
            id = 14,
            name = "Franskar stór",
            description = "Stórar franskar",
            priceIsk = 1690,
            available = true,
            tags = "vegan",
            imageData = null,
            estimatedWaitTimeMinutes = 10
        ),
        Item(
            id = 15,
            name = "Spæsí franskar",
            description = "Franskar með kryddi",
            priceIsk = 1490,
            available = true,
            tags = "vegan",
            imageData = null,
            estimatedWaitTimeMinutes = 10
        ),
        Item(
            id = 16,
            name = "Osta franskar",
            description = "Franskar með osti",
            priceIsk = 1490,
            available = true,
            tags = "vegan",
            imageData = null,
            estimatedWaitTimeMinutes = 10
        ),
        Item(
            id = 17,
            name = "Sósur",
            description = "Sósur á la carte",
            priceIsk = 390,
            available = true,
            tags = "",
            imageData = null,
            estimatedWaitTimeMinutes = 10
        ),
        // —— Drykkir (Drinks) ——
        Item(
            id = 18,
            name = "Gos í dós",
            description = "Coke, Coke Zero, Appelsín, Toppur",
            priceIsk = 390,
            available = true,
            tags = "vegan",
            imageData = null,
            estimatedWaitTimeMinutes = 10
        ),
        Item(
            id = 19,
            name = "Safi",
            description = "Djús",
            priceIsk = 290,
            available = true,
            tags = "vegan",
            imageData = null,
            estimatedWaitTimeMinutes = 10
        )
    )
}
