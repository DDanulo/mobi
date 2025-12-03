package com.donchik.akadeska.presentation.shopItemDetail
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.donchik.akadeska.R

@Composable
fun ShopDetailsScreen(
    vm: ShopDetailsViewModel,
    onContactSeller: (String) -> Unit,
    onBack: () -> Unit
) {
    val state by vm.state.collectAsState()
    LaunchedEffect(state.isDeleted) {
        if (state.isDeleted) {
            onBack()
        }
    }
    if (state.loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (state.error != null) {
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text(state.error!!, color = MaterialTheme.colorScheme.error)
        }
        return
    }

    val item = state.item!!

    Column(Modifier.fillMaxSize()) {
        // --- Scrollable Content ---
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Image
            if (item.imageUrl != null) {
                Box {
                    Image(
                        painter = rememberAsyncImagePainter(item.imageUrl),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(300.dp),
                        contentScale = ContentScale.Crop
                    )
                    // Detail Screen Reserved Badge
                    if (item.isReserved) {
                        Surface(
                            color = Color(0xFFFF9800),
                            modifier = Modifier.padding(16.dp).align(androidx.compose.ui.Alignment.TopEnd),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                "RESERVED (1h)",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            } else {
                // Placeholder if no image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(16.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text("No image")
                }
            }

            Column(Modifier.padding(16.dp)) {
                // 2. Title
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(Modifier.height(8.dp))

                // 3. Price
                Text(
                    text = "${item.price ?: 0.0} zł",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )

                Divider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = Color.LightGray.copy(alpha = 0.5f)
                )

                // 4. Description
                Text(
                    text = item.body.ifBlank { "Brak opisu." },
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp
                )
            }
        }

        // --- Bottom Buttons ---
        Surface(tonalElevation = 8.dp, shadowElevation = 8.dp) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // If I am the seller: Show Delete button only
                if (item.isMine) {
                    Button(
                        onClick = { vm.deleteItem() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Delete Listing", fontWeight = FontWeight.Bold)
                    }
                } else {
                    // If I am a buyer: Show Contact & Reserve
                    OutlinedButton(
                        onClick = { item.createdBy?.let { onContactSeller(it) } },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black)
                    ) {
                        Text(stringResource(R.string.btn_contact), fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = { vm.reserveItem() },
                        enabled = !item.isReserved,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (item.isReserved) Color.Gray else Color(0xFFC62828)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text(
                            if (item.isReserved) "Reserved" else stringResource(R.string.btn_reservation),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}