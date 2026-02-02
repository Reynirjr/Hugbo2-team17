package com.wjoops.customer.data.mappers

import com.wjoops.customer.data.network.dto.MenuCategoryDto
import com.wjoops.customer.data.network.dto.MenuItemDto
import com.wjoops.customer.data.network.dto.RestaurantDto
import com.wjoops.customer.data.network.dto.WaitTimeDto
import com.wjoops.customer.domain.models.MenuCategory
import com.wjoops.customer.domain.models.MenuItem
import com.wjoops.customer.domain.models.Restaurant
import com.wjoops.customer.domain.models.WaitTime

fun RestaurantDto.toDomain() = Restaurant(id = id, name = name, lat = lat, lon = lon)

fun MenuCategoryDto.toDomain() = MenuCategory(id = id, name = name, sortOrder = sortOrder)

fun MenuItemDto.toDomain() = MenuItem(
    id = id,
    name = name,
    description = description,
    price = price,
    isVegetarian = isVegetarian,
    isAvailable = isAvailable,
    categoryId = categoryId,
    imageUrl = imageUrl,
)

fun WaitTimeDto.toDomain() = WaitTime(estimatedMinutes = estimatedMinutes)
