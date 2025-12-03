package com.donchik.akadeska.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.donchik.akadeska.R

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
        Text(text = stringResource(if (state.mode == AuthMode.SIGN_IN) R.string.sign_in else R.string.sign_up), style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = state.email, onValueChange = vm::setEmail,
            label = { Text(stringResource(R.string.email)) }, singleLine = true, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = state.password, onValueChange = vm::setPassword,
            label = { Text(stringResource(R.string.password)) }, singleLine = true, visualTransformation = PasswordVisualTransformation(),
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
        ) {
            Text(stringResource(if (state.loading) R.string.please_wait else if (state.mode == AuthMode.SIGN_IN) R.string.sign_in else R.string.sign_up))
        }

        TextButton(onClick = vm::switchMode, modifier = Modifier.padding(top = 8.dp)) {
            Text(stringResource(if (state.mode == AuthMode.SIGN_IN) R.string.no_account else R.string.have_account))
        }
    }
}