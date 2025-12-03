@file:OptIn(ExperimentalMaterial3Api::class)
package com.donchik.akadeska

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import com.donchik.akadeska.ui.theme.AkaDeskaTheme

import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.donchik.akadeska.data.FirebaseRepository
import androidx.activity.result.contract.ActivityResultContracts
import com.donchik.akadeska.ui.MainScaffold
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Permission result logic (optional)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
        setContent {
            AkaDeskaTheme {
                var selectedTab by remember { mutableStateOf(BottomTab.HOME) }

                MainScaffold(
//                    selectedTab = selectedTab,
//                    onTabSelected = { selectedTab = it }
                )
            }
        }
    }
}

enum class BottomTab { HOME, SHOP, CHAT }