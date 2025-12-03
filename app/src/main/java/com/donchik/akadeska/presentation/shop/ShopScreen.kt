package com.donchik.akadeska.presentation.shop
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.donchik.akadeska.data.ShopItem
import com.donchik.akadeska.presentation.shop.ShopViewModel

@Composable
fun ShopScreen(
    vm: ShopViewModel,
    onOpenDetails: (String) -> Unit,
    onContactSeller: (String) -> Unit
) {
    val state by vm.state.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        if (state.loading) {
            item { CircularProgressIndicator() }
        }

        state.error?.let { msg ->
            item { Text(msg, color = MaterialTheme.colorScheme.error) }
        }

        if (!state.loading && state.items.isEmpty()) {
            item { Text("Brak ogłoszeń.") }
        }

        items(state.items) { item ->
            ShopItemCard(item, onOpenDetails, onContactSeller)
        }

        item {
            Spacer(modifier = Modifier.height(70.dp)) // space above bottom bar
        }
    }
}

@Composable
fun ShopItemCard(item: ShopItem,
                 onOpenDetails: (String) -> Unit,
                 onContactSeller: (String) -> Unit ) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White) // White background as in screenshot
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                // 1. Image (70x70dp placeholder or actual image)
                if (item.imageUrl != null) {
                    Image(
                        painter = rememberAsyncImagePainter(item.imageUrl),
                        contentDescription = null,
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // 2. Text Content
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.Gray
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${item.price ?: 0.0} zł",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        // TRIGGER THE CHAT
                        item.sellerId?.let { onContactSeller(it) }
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Kontakt", fontSize = 12.sp)
                }

                Button(
                    onClick = { /* Placeholder */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFC62828) // Deep Red color
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Szybka rezerwacja", fontSize = 12.sp, maxLines = 1)
                }
            }
        }
    }
}