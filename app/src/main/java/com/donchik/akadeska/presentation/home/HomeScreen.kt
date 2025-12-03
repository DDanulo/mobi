package com.donchik.akadeska.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.donchik.akadeska.R
import com.donchik.akadeska.data.FirebaseRepository


@Composable
fun HomeScreen(
    vm: HomeViewModel,
    onOpenDetails: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by vm.state.collectAsState()

    LazyColumn(
        modifier = modifier
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
        items(state.items) { item ->
            // If your EventCard can't take URL, create a simple Card here with Coil:
            EventCardFromUrl(
                title = item.title,
                imageUrl = item.imageUrl,
                onClick = { onOpenDetails(item.id) }
            )
        }
    }
}

@Composable
fun EventCardFromUrl(
    title: String,
    imageUrl: String?,
    onClick: () -> Unit
) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column {
            if (imageUrl != null) {
                Image(
                    painter = coil.compose.rememberAsyncImagePainter(imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                )
                Spacer(Modifier.height(8.dp))
            } else {
                Image(
                    painter = painterResource(id = R.drawable.beer),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(MaterialTheme.shapes.medium)
                )
                Spacer(Modifier.height(8.dp))
            }
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

data class EventUi(val imageRes: Int, val title: String)

@Preview
@Composable
fun HomeScreenPreview() {
//    HomeScreen(onOpenDetails = { /* no-op in preview */ })
}