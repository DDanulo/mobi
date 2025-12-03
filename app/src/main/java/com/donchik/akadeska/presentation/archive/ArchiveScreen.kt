package com.donchik.akadeska.presentation.archive

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.donchik.akadeska.presentation.home.EventCardFromUrl

@Composable
fun ArchiveScreen(
    vm: ArchiveViewModel,
    onOpenDetails: (String) -> Unit
) {
    val state by vm.state.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (state.loading) {
            item { CircularProgressIndicator() }
        }

        state.error?.let { msg ->
            item { Text(msg, color = MaterialTheme.colorScheme.error) }
        }

        if (!state.loading && state.items.isEmpty()) {
            item {
                Text("No archived events found.", style = MaterialTheme.typography.bodyLarge)
            }
        }

        items(state.items) { item ->
            // Reusing the EventCard from your HomeScreen
            EventCardFromUrl(
                title = item.title,
                imageUrl = item.imageUrl,
                onClick = { onOpenDetails(item.id) }
            )
        }
    }
}