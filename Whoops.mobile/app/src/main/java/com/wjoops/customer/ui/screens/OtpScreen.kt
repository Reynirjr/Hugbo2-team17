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
fun OtpScreen(
    viewModel: AuthViewModel,
    phone: String,
    onVerified: () -> Unit,
) {
    val state = viewModel.otpState.collectAsState().value

    Scaffold(
        topBar = { TopAppBar(title = { Text("Verify") }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Phone: $phone")
            Text("Enter any 4-6 digits to succeed.")

            OutlinedTextField(
                value = state.otp,
                onValueChange = viewModel::onOtpChanged,
                label = { Text("OTP") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            Button(
                onClick = { viewModel.verifyOtp(phone, onVerified) },
                enabled = state.otp.isNotBlank() && state.verifyState !is UiState.Loading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (state.verifyState is UiState.Loading) "Verifying…" else "Verify")
            }

            if (state.verifyState is UiState.Error) {
                Text((state.verifyState as UiState.Error).message)
            }
        }
    }
}
