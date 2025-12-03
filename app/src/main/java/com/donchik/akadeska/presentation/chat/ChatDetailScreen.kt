package com.donchik.akadeska.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Simple mock model for the UI
data class MockMessage(
    val id: String,
    val text: String,
    val isMe: Boolean,
    val senderName: String = "User"
)

@Composable
fun ChatDetailScreen(
    sellerId: String, // We will use this later for real DB
    onBack: () -> Unit
) {
    // Local state for the PoC (clears when you leave the screen)
    var messageText by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<MockMessage>(
        // Mock initial data to match your screenshot style
        MockMessage("1", "Messsssssage", false, "Seller"),
        MockMessage("2", "Messssssssssssssssssssssssssage", false, "Seller"),
        MockMessage("3", "Messsssssssssssssssssssssssssssssssssssssssssssssssssssssssage", false, "Seller")
    ) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Background color from design
    ) {
        // --- Messages List ---
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            reverseLayout = true // Start from bottom
        ) {
            items(messages.reversed()) { msg ->
                MessageBubble(msg)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // --- Bottom Input Bar ---
        Surface(
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Napisz wiadomość") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        focusedContainerColor = Color(0xFFF5F5F5),
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = {
                    if (messageText.isNotBlank()) {
                        messages.add(MockMessage(
                            id = System.currentTimeMillis().toString(),
                            text = messageText,
                            isMe = true
                        ))
                        messageText = ""
                    }
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun MessageBubble(msg: MockMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (msg.isMe) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        // Avatar for OTHER (Left side)
        if (!msg.isMe) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                tint = Color.Black
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        // Bubble
        Column(
            horizontalAlignment = if (msg.isMe) Alignment.End else Alignment.Start
        ) {
            if (!msg.isMe) {
                Text(
                    text = msg.senderName,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 2.dp, start = 4.dp)
                )
            }

            Surface(
                shape = if (msg.isMe) RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)
                else RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp),
                color = if (msg.isMe) Color(0xFFC62828) else Color(0xFFE0E0E0), // Red for me, Gray for other
                modifier = Modifier.widthIn(max = 280.dp)
            ) {
                Text(
                    text = msg.text,
                    modifier = Modifier.padding(12.dp),
                    color = if (msg.isMe) Color.White else Color.Black,
                    fontSize = 14.sp
                )
            }
        }

        // Avatar for ME (Right side)
        if (msg.isMe) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                tint = Color.Black
            )
        }
    }
}