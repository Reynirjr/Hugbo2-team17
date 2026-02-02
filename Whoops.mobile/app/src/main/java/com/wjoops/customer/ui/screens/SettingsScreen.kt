package com.wjoops.customer.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalContext
import com.wjoops.customer.ui.viewmodel.AuthViewModel
import com.wjoops.customer.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    authViewModel: AuthViewModel,
    settingsViewModel: SettingsViewModel,
    onBack: () -> Unit,
    onLoggedOut: () -> Unit,
) {
    val ctx = LocalContext.current
    val state = settingsViewModel.state.collectAsState().value
    val coarseGranted = ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    val fineGranted = ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = { IconButton(onClick = onBack) { androidx.compose.material3.Icon(Icons.Default.ArrowBack, contentDescription = "Back") } },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Permissions")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Location:")
                Text(if (fineGranted || coarseGranted) "Granted" else "Denied")
            }

            Text("Debug")
            OutlinedTextField(
                value = state.baseUrl,
                onValueChange = settingsViewModel::onBaseUrlChanged,
                label = { Text("Base URL (stub)") },
                supportingText = { Text("Stored in DataStore. Retrofit wiring is TODO; restart may be required.") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            Button(onClick = settingsViewModel::saveBaseUrl, modifier = Modifier.fillMaxWidth()) {
                Text(if (state.saved) "Saved" else "Save base URL")
            }

            Button(
                onClick = { authViewModel.logout(onLoggedOut) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Logout")
            }
        }
    }
}

