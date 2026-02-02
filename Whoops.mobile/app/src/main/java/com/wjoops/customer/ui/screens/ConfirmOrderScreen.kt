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
import com.wjoops.customer.domain.models.Order
import com.wjoops.customer.ui.components.UiStateContainer
import com.wjoops.customer.ui.viewmodel.ConfirmOrderViewModel
import com.wjoops.customer.util.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmOrderScreen(
    viewModel: ConfirmOrderViewModel,
    onBack: () -> Unit,
    onOrderPlaced: (Order) -> Unit,
) {
    val state = viewModel.state.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirm") },
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
            UiStateContainer(state.draft) { draft ->
                Text("Items: ${draft.basket.items.size}")
                Text("Total: ${draft.basket.totals.total} ${draft.basket.totals.currency}")
                Text("Pickup: ${draft.pickupType}")
            }

            Button(
                onClick = { viewModel.placeOrder(onOrderPlaced) },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.placing !is UiState.Loading,
            ) {
                Text(if (state.placing is UiState.Loading) "Placing…" else "Place order")
            }

            if (state.placing is UiState.Error) {
                Text((state.placing as UiState.Error).message)
            }
        }
    }
}
