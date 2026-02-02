package com.wjoops.customer.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wjoops.customer.ui.viewmodel.SplashViewModel
import com.wjoops.customer.util.UiState

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onNavigateAuthed: () -> Unit,
    onNavigateUnauthed: () -> Unit,
) {
    LaunchedEffect(Unit) { viewModel.checkAuth() }

    val state = viewModel.state.value
    LaunchedEffect(state.auth) {
        val s = state.auth
        if (s is UiState.Success) {
            if (s.data) onNavigateAuthed() else onNavigateUnauthed()
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "WJOOPS Customer",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp),
        )
    }
}
