package com.example.whoopsmobile.data

import com.example.whoopsmobile.model.Item

/**
 * Default menu data for Haga-vagninn (Kjöt eða Vegan).
 * Used when the API returns no items so US1 (view menu) still works.
 */
object HagaMenuData {

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
