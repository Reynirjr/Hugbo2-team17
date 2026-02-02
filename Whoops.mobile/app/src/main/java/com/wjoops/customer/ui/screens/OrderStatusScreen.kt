package com.wjoops.customer.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wjoops.customer.ui.components.UiStateContainer
import com.wjoops.customer.ui.viewmodel.OrderStatusViewModel
import com.wjoops.customer.util.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderStatusScreen(
    viewModel: OrderStatusViewModel,
    onBack: () -> Unit,
) {
    val state = viewModel.state.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order status") },
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
            UiStateContainer(state.latest, onRetry = viewModel::load) { order ->
                if (order == null) {
                    Text("No recent orders.")
                } else {
                    Text("Order: ${order.id}")
                    Text("Status: ${order.status}")
                    Text("Items: ${order.items.size}")
                    Text("Total: ${order.totals.total} ${order.totals.currency}")
                }
            }

            Button(
                onClick = viewModel::refresh,
                modifier = Modifier.fillMaxWidth(),
                enabled = state.latest is UiState.Success && state.refreshState !is UiState.Loading,
            ) {
                Text(if (state.refreshState is UiState.Loading) "Refreshing…" else "Refresh")
            }
            if (state.refreshState is UiState.Error) {
                Text((state.refreshState as UiState.Error).message)
            }
        }
    }
}
