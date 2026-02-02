package com.wjoops.customer.app.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument

object Routes {
    const val Splash = "splash"
    const val Login = "login"
    const val Otp = "otp"
    const val Menu = "menu"
    const val Basket = "basket"
    const val Confirm = "confirm"
    const val OrderStatus = "order_status"
    const val Settings = "settings"

    const val ARG_PHONE = "phone"

    val otpArguments = listOf(
        navArgument(ARG_PHONE) { type = NavType.StringType },
    )

    fun otpRoute(phone: String) = "$Otp/$phone"
    const val otpRoutePattern = "$Otp/{$ARG_PHONE}"
}
