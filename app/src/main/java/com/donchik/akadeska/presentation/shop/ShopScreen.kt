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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.donchik.akadeska.R
import com.donchik.akadeska.data.ShopItem

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
            item { Text(stringResource(R.string.no_listings)) }
        }

        items(state.items) { item ->
            // Pass the reserve function from VM
            ShopItemCard(
                item = item,
                onOpenDetails = onOpenDetails,
                onContactSeller = onContactSeller,
                onReserve = { vm.reserveItem(item.id) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(70.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopItemCard(
    item: ShopItem,
    onOpenDetails: (String) -> Unit,
    onContactSeller: (String) -> Unit,
    onReserve: () -> Unit
) {
    Card(
        onClick = { onOpenDetails(item.id) },
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                // Image
                if (item.imageUrl != null) {
                    Image(
                        painter = rememberAsyncImagePainter(item.imageUrl),
                        contentDescription = null,
                        modifier = Modifier.size(70.dp).clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(modifier = Modifier.size(70.dp).background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp)))
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Content
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        // RESERVED BADGE
                        if (item.isReserved) {
                            Surface(
                                color = Color(0xFFFF9800), // Orange
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.padding(start = 4.dp)
                            ) {
                                Text(
                                    "RESERVED",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
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

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val canContact = !item.isReserved || item.isReservedByMe

                OutlinedButton(
                    onClick = { item.sellerId?.let { onContactSeller(it) } },
                    enabled = canContact, // <--- APPLY LOGIC
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(stringResource(R.string.btn_contact), fontSize = 12.sp)
                }

                // Reserve Button
                Button(
                    onClick = onReserve,
                    // Gray out if reserved
                    enabled = !item.isReserved,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (item.isReserved) Color.Gray else Color(0xFFC62828)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        if (item.isReserved) "Reserved" else stringResource(R.string.btn_reservation),
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }
            }
        }
    }
}