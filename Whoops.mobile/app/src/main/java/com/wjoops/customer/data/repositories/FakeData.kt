package com.wjoops.customer.data.repositories

import com.wjoops.customer.domain.models.MenuCategory
import com.wjoops.customer.domain.models.MenuItem
import com.wjoops.customer.domain.models.Restaurant
import com.wjoops.customer.domain.models.WaitTime

object FakeData {
    val restaurant = Restaurant(
        id = "rest-1",
        name = "WJOOPS Burgers",
        lat = 64.1466,
        lon = -21.9426,
    )

    val categories = listOf(
        MenuCategory(id = "cat-burgers", name = "Burgers", sortOrder = 1),
        MenuCategory(id = "cat-sides", name = "Sides", sortOrder = 2),
        MenuCategory(id = "cat-drinks", name = "Drinks", sortOrder = 3),
    )

    val items = listOf(
        MenuItem(
            id = "m-1",
            name = "Classic Burger",
            description = "Beef, cheese, pickles, house sauce",
            price = 1990,
            isVegetarian = false,
            isAvailable = true,
            categoryId = "cat-burgers",
        ),
        MenuItem(
            id = "m-2",
            name = "Veggie Burger",
            description = "Plant patty, lettuce, tomato, vegan mayo",
            price = 2090,
            isVegetarian = true,
            isAvailable = true,
            categoryId = "cat-burgers",
        ),
        MenuItem(
            id = "m-3",
            name = "Fries",
            description = "Crispy fries with sea salt",
            price = 690,
            isVegetarian = true,
            isAvailable = true,
            categoryId = "cat-sides",
        ),
        MenuItem(
            id = "m-4",
            name = "Cola",
            description = "330ml",
            price = 490,
            isVegetarian = true,
            isAvailable = false,
            categoryId = "cat-drinks",
        ),
    )

    val waitTime = WaitTime(estimatedMinutes = 18)
}
