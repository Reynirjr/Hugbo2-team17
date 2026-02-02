package com.wjoops.customer.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.filled.Add
import androidx.compose.material3.icons.filled.ArrowBack
import androidx.compose.material3.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wjoops.customer.ui.viewmodel.BasketViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasketScreen(
    viewModel: BasketViewModel,
    onBack: () -> Unit,
    onProceed: () -> Unit,
) {
    val state = viewModel.state.collectAsState().value
    val basket = state.basket

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Basket") },
                navigationIcon = {
                    IconButton(onClick = onBack) { androidx.compose.material3.Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
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
            if (basket == null || basket.items.isEmpty()) {
                Text("Your basket is empty.")
                return@Column
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
                items(basket.items) { item ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(item.menuItem.name, style = MaterialTheme.typography.titleMedium)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text("${item.menuItem.price} ISK")
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    IconButton(onClick = { viewModel.dec(item.menuItem.id) }) {
                                        androidx.compose.material3.Icon(Icons.Default.Remove, contentDescription = "Decrease")
                                    }
                                    Text("${item.quantity}")
                                    IconButton(onClick = { viewModel.inc(item.menuItem.id) }) {
                                        androidx.compose.material3.Icon(Icons.Default.Add, contentDescription = "Increase")
                                    }
                                }
                            }
                            Button(onClick = { viewModel.remove(item.menuItem.id) }, modifier = Modifier.fillMaxWidth()) {
                                Text("Remove")
                            }
                        }
                    }
                }
            }

            Text("Subtotal: ${basket.totals.subtotal} ${basket.totals.currency}")
            Button(onClick = onProceed, modifier = Modifier.fillMaxWidth()) { Text("Proceed to confirm") }
        }
    }
}
