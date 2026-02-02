package com.wjoops.customer.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wjoops.customer.ui.viewmodel.AuthViewModel
import com.wjoops.customer.util.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onOtpRequested: (String) -> Unit,
) {
    val state = viewModel.loginState.collectAsState().value

    Scaffold(
        topBar = { TopAppBar(title = { Text("Login") }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Enter your phone number. OTP is mocked.")

            OutlinedTextField(
                value = state.phone,
                onValueChange = viewModel::onPhoneChanged,
                label = { Text("Phone") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            Button(
                onClick = { viewModel.requestOtp(onOtpRequested) },
                enabled = state.phone.isNotBlank() && state.requestState !is UiState.Loading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (state.requestState is UiState.Loading) "Requesting…" else "Request OTP")
            }

            if (state.requestState is UiState.Error) {
                Text((state.requestState as UiState.Error).message)
            }
        }
    }
}
