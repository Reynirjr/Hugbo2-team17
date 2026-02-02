package com.wjoops.customer.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.wjoops.customer.ui.theme.WjoopsCustomerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            WjoopsCustomerTheme {
                Surface(modifier = Modifier) {
                    WjoopsAppRoot()
                }
            }
        }
    }
}

@Composable
private fun WjoopsAppRoot() {
    com.wjoops.customer.app.navigation.AppNavGraph()
}
