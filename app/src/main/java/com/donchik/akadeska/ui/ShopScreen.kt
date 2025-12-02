package com.donchik.akadeska.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

//@Preview
@Composable
fun ShopScreen(
    modifier: Modifier = Modifier,
    onOpenDetails: (String) -> Unit
) {
    val items = List(5) {
        ShopItemUi(
            name = "item name",
            description = "item description",
            price = "0.0 zł"
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
    ) {
        items(items) { item ->
            ShopItemCard(item)
        }
        item {
            Spacer(modifier = Modifier.height(70.dp)) // space above bottom bar
        }
    }
}

@Composable
fun ShopItemCard(item: ShopItemUi) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(Color(0xFFFFFFFF))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                // image placeholder 70x70 like on mock
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .background(Color(0xFFE0E0E0), RoundedCornerShape(4.dp))
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = item.name)
                    Text(text = item.description)
                    Text(text = item.price)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { /* contact */ },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Kontakt")
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = { /* quick reservation */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Szybka rezerwacja")
                }
            }
        }
    }
}

data class ShopItemUi(
    val name: String,
    val description: String,
    val price: String
)