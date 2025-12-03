package com.donchik.akadeska.presentation.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.donchik.akadeska.R

@Composable
fun AdminScreen(vm: AdminViewModel, onBack: () -> Unit) {
    val isAdmin by vm.isAdmin.collectAsState()
    val items by vm.pending.collectAsState()
    val loading by vm.loading.collectAsState()
    val err by vm.error.collectAsState()

    if (!isAdmin) {
        // guard
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            stringResource(R.string.no_admin_access)
            Spacer(Modifier.height(8.dp))
            Button(onClick = onBack) { Text("Back") }
        }
        return
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = stringResource(R.string.pending_posts), style = MaterialTheme.typography.headlineSmall)
        if (loading) { CircularProgressIndicator() }
        if (err != null) { Text(err!!, color = MaterialTheme.colorScheme.error) }
        Spacer(Modifier.height(8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(items) { it ->
                ElevatedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text(it.title, style = MaterialTheme.typography.titleMedium)
                        Text(it.type, style = MaterialTheme.typography.labelMedium)
                        Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { vm.approve(it.id) }) { stringResource(R.string.btn_approve) }
                            OutlinedButton(onClick = { vm.reject(it.id) }) { stringResource(R.string.btn_reject) }
                        }
                    }
                }
            }
        }
    }
}
