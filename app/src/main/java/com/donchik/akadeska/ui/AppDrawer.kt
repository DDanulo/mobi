package com.donchik.akadeska.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.donchik.akadeska.R
import navigation.Screen
@Composable
fun DrawerContent(
    isAdmin: Boolean,
    onOpenAdmin: () -> Unit,
    notificationsEnabled: Boolean,
    onToggleNotifications: (Boolean) -> Unit,
    onOpenArchive: () -> Unit
) {
    ModalDrawerSheet {
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.menu_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        NavigationDrawerItem(
            label = { Text(stringResource(R.string.menu_archive)) },
            selected = false,
            onClick = onOpenArchive,
            icon = { Icon(Icons.Default.DateRange, contentDescription = null) }, // You might need to import DateRange or use another icon like 'List'
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        if (isAdmin) {
            NavigationDrawerItem(
                label = { Text(stringResource(R.string.menu_admin)) },
                selected = false,
                onClick = onOpenAdmin,
                icon = { Icon(Icons.Default.Verified, contentDescription = null) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Notifications", // You can add to strings.xml: <string name="notifications">Notifications</string>
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = notificationsEnabled,
                onCheckedChange = onToggleNotifications
            )
        }
        // Add other optional items here (settings, about, etc.)
    }
}