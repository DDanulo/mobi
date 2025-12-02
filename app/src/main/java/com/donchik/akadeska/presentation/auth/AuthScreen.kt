package com.donchik.akadeska.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun AuthScreen(
    vm: AuthViewModel,
    onSignedIn: () -> Unit
) {
    val state by vm.state.collectAsState()

    LaunchedEffect(state.signedIn) {
        if (state.signedIn) onSignedIn()
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(if (state.mode == AuthMode.SIGN_IN) "Sign in" else "Sign up", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = state.email, onValueChange = vm::setEmail,
            label = { Text("Email") }, singleLine = true, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = state.password, onValueChange = vm::setPassword,
            label = { Text("Password") }, singleLine = true, visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        if (state.error != null) {
            Spacer(Modifier.height(8.dp))
            Text(state.error!!, color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = vm::submit,
            enabled = !state.loading,
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (state.loading) "Please wait…" else if (state.mode == AuthMode.SIGN_IN) "Sign in" else "Sign up") }

        TextButton(onClick = vm::switchMode, modifier = Modifier.padding(top = 8.dp)) {
            Text(if (state.mode == AuthMode.SIGN_IN) "No account? Sign up" else "Have an account? Sign in")
        }
    }
}