package com.wjoops.customer.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.filled.Refresh
import androidx.compose.material3.icons.filled.Settings
import androidx.compose.material3.icons.filled.ShoppingBasket
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.wjoops.customer.domain.models.MenuItem
import com.wjoops.customer.domain.models.Restaurant
import com.wjoops.customer.ui.components.UiStateContainer
import com.wjoops.customer.ui.viewmodel.MenuViewModel
import com.wjoops.customer.util.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    viewModel: MenuViewModel,
    onOpenBasket: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenOrderStatus: () -> Unit,
) {
    val state = viewModel.state.collectAsState().value
    var locationGranted by remember { mutableStateOf(false) }

    val requestPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> locationGranted = granted },
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Menu") },
                actions = {
                    IconButton(onClick = viewModel::refresh) { Icon(Icons.Default.Refresh, contentDescription = "Refresh") }
                    IconButton(onClick = onOpenSettings) { Icon(Icons.Default.Settings, contentDescription = "Settings") }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onOpenBasket) {
                Row(modifier = Modifier.padding(horizontal = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.ShoppingBasket, contentDescription = "Basket")
                    Text("${state.basketCount} · ${state.basketTotal}")
                }
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            UiStateContainer(state.restaurant, onRetry = viewModel::refresh) { restaurant ->
                Header(
                    restaurant = restaurant,
                    waitTimeState = state.waitTime,
                    distanceLabel = state.distanceLabel,
                    onRequestLocation = {
                        requestPermission.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                    },
                    onRefreshDistance = {
                        if (locationGranted) viewModel.requestDistanceUpdate(restaurant)
                    },
                    locationGranted = locationGranted,
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = state.vegetarianOnly, onCheckedChange = { viewModel.toggleVegetarianOnly() })
                    Text("Vegetarian only")
                    Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.End) {
                        Button(onClick = onOpenOrderStatus) { Text("Order Status") }
                    }
                }

                UiStateContainer(state.menu, onRetry = viewModel::refresh) { (_, items) ->
                    val filtered = if (state.vegetarianOnly) items.filter { it.isVegetarian } else items
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(filtered) { item ->
                            MenuItemCard(item = item, onAdd = { viewModel.addToBasket(item) })
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun Header(
    restaurant: Restaurant,
    waitTimeState: UiState<com.wjoops.customer.domain.models.WaitTime>,
    distanceLabel: String?,
    locationGranted: Boolean,
    onRequestLocation: () -> Unit,
    onRefreshDistance: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(restaurant.name, style = MaterialTheme.typography.headlineSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            when (waitTimeState) {
                is UiState.Success -> AssistChip(onClick = {}, label = { Text("~${waitTimeState.data.estimatedMinutes} min") })
                is UiState.Loading -> AssistChip(onClick = {}, label = { Text("Wait…") })
                is UiState.Error -> AssistChip(onClick = {}, label = { Text("Wait time N/A") })
            }

            if (distanceLabel != null) {
                AssistChip(onClick = onRefreshDistance, label = { Text(distanceLabel) })
            } else {
                AssistChip(
                    onClick = {
                        if (locationGranted) onRefreshDistance() else onRequestLocation()
                    },
                    label = { Text(if (locationGranted) "Get distance" else "Enable location") },
                )
            }
        }
    }
}

@Composable
private fun MenuItemCard(
    item: MenuItem,
    onAdd: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(item.name, style = MaterialTheme.typography.titleMedium)
                Text("${item.price} ISK", style = MaterialTheme.typography.titleMedium)
            }
            Text(item.description, style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = {}, label = { Text(if (item.isVegetarian) "Veg" else "Meat") })
                AssistChip(onClick = {}, label = { Text(if (item.isAvailable) "Available" else "Sold out") })
            }
            Button(
                onClick = onAdd,
                enabled = item.isAvailable,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (item.isAvailable) "Add to basket" else "Unavailable")
            }
        }
    }
}
