package com.donchik.akadeska.presentation.profile
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.donchik.akadeska.R

@Composable
fun ProfileScreen(
    vm: ProfileViewModel,
    onNavigateToAuth: () -> Unit
) {
    val state by vm.state.collectAsState()

    // Local state for the text field editing
    var nameInput by remember(state.displayName) { mutableStateOf(state.displayName) }

    // If user signs out or isn't logged in, redirect
    LaunchedEffect(state.isSignedOut) {
        if (state.isSignedOut) {
            onNavigateToAuth()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(16.dp))

        // Role Badge
        Surface(
            color = if (state.isAdmin) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = if (state.isAdmin) "ADMIN" else "STUDENT",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelLarge,
                color = if (state.isAdmin) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
            )
        }

        Spacer(Modifier.height(24.dp))

        // Email (Read only)
        OutlinedTextField(
            value = state.email,
            onValueChange = {},
            label = { Text(stringResource(R.string.email)) },
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        // Name (Editable)
        OutlinedTextField(
            value = nameInput,
            onValueChange = { nameInput = it },
            label = { Text(stringResource(R.string.display_name)) },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                if (state.loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }
        )

        Spacer(Modifier.height(8.dp))

        // Save Button (only enabled if changed)
        Button(
            onClick = { vm.updateName(nameInput) },
            enabled = !state.loading && nameInput != state.displayName,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.update_name))
        }

        if (state.error != null) {
            Spacer(Modifier.height(8.dp))
            Text(state.error!!, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.weight(1f))

        // Logout
        OutlinedButton(
            onClick = { vm.signOut() },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.sign_out))
        }
    }
}