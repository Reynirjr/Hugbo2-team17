package com.wjoops.customer.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wjoops.customer.util.UiState

@Composable
fun <T> UiStateContainer(
    state: UiState<T>,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
    content: @Composable (T) -> Unit,
) {
    when (state) {
        is UiState.Loading -> {
            Column(modifier = modifier.padding(16.dp)) {
                Text("Loading…", style = MaterialTheme.typography.bodyLarge)
            }
        }
        is UiState.Error -> {
            Column(
                modifier = modifier.padding(16.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(state.message, color = MaterialTheme.colorScheme.error)
                if (state.canRetry && onRetry != null) {
                    Button(onClick = onRetry) { Text("Retry") }
                }
            }
        }
        is UiState.Success -> content(state.data)
    }
}
