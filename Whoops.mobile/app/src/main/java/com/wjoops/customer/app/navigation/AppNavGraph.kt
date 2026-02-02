package com.wjoops.customer.app.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wjoops.customer.ui.screens.BasketScreen
import com.wjoops.customer.ui.screens.ConfirmOrderScreen
import com.wjoops.customer.ui.screens.LoginScreen
import com.wjoops.customer.ui.screens.MenuScreen
import com.wjoops.customer.ui.screens.OtpScreen
import com.wjoops.customer.ui.screens.OrderStatusScreen
import com.wjoops.customer.ui.screens.SettingsScreen
import com.wjoops.customer.ui.screens.SplashScreen
import com.wjoops.customer.ui.viewmodel.AuthViewModel
import com.wjoops.customer.ui.viewmodel.BasketViewModel
import com.wjoops.customer.ui.viewmodel.ConfirmOrderViewModel
import com.wjoops.customer.ui.viewmodel.MenuViewModel
import com.wjoops.customer.ui.viewmodel.OrderStatusViewModel
import com.wjoops.customer.ui.viewmodel.SettingsViewModel
import com.wjoops.customer.ui.viewmodel.SplashViewModel

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Splash,
    ) {
        composable(Routes.Splash) {
            val vm: SplashViewModel = hiltViewModel()
            SplashScreen(
                viewModel = vm,
                onNavigateAuthed = {
                    navController.navigate(Routes.Menu) {
                        popUpTo(Routes.Splash) { inclusive = true }
                    }
                },
                onNavigateUnauthed = {
                    navController.navigate(Routes.Login) {
                        popUpTo(Routes.Splash) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.Login) {
            val vm: AuthViewModel = hiltViewModel()
            LoginScreen(
                viewModel = vm,
                onOtpRequested = { phone ->
                    navController.navigate(Routes.otpRoute(phone))
                },
            )
        }

        composable(
            route = Routes.otpRoutePattern,
            arguments = Routes.otpArguments,
        ) { entry ->
            val phone = entry.arguments?.getString(Routes.ARG_PHONE).orEmpty()
            val vm: AuthViewModel = hiltViewModel()
            OtpScreen(
                viewModel = vm,
                phone = phone,
                onVerified = {
                    navController.navigate(Routes.Menu) {
                        popUpTo(Routes.Login) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.Menu) {
            val vm: MenuViewModel = hiltViewModel()
            MenuScreen(
                viewModel = vm,
                onOpenBasket = { navController.navigate(Routes.Basket) },
                onOpenSettings = { navController.navigate(Routes.Settings) },
                onOpenOrderStatus = { navController.navigate(Routes.OrderStatus) },
            )
        }

        composable(Routes.Basket) {
            val vm: BasketViewModel = hiltViewModel()
            BasketScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() },
                onProceed = { navController.navigate(Routes.Confirm) },
            )
        }

        composable(Routes.Confirm) {
            val vm: ConfirmOrderViewModel = hiltViewModel()
            ConfirmOrderScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() },
                onOrderPlaced = {
                    navController.navigate(Routes.OrderStatus) {
                        popUpTo(Routes.Menu)
                    }
                },
            )
        }

        composable(Routes.OrderStatus) {
            val vm: OrderStatusViewModel = hiltViewModel()
            OrderStatusScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.Settings) {
            val authVm: AuthViewModel = hiltViewModel()
            val settingsVm: SettingsViewModel = hiltViewModel()
            SettingsScreen(
                authViewModel = authVm,
                settingsViewModel = settingsVm,
                onBack = { navController.popBackStack() },
                onLoggedOut = {
                    navController.navigate(Routes.Login) {
                        popUpTo(Routes.Menu) { inclusive = true }
                    }
                },
            )
        }
    }
}

