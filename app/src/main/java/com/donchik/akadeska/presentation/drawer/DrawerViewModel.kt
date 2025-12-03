package com.donchik.akadeska.com.donchik.akadeska.presentation.drawer

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.donchik.akadeska.data.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class DrawerViewModel(
    app: Application,
    private val repo: FirebaseRepository
) : AndroidViewModel(app) {

    private val prefs = app.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    // State
    val isAdmin = MutableStateFlow(false)
    val areNotificationsEnabled = MutableStateFlow(prefs.getBoolean("notifications_enabled", true))

    init {
        // 1. Monitor Admin Status
        viewModelScope.launch {
            repo.isAdminFlow().collect { isUserAdmin ->
                isAdmin.value = isUserAdmin
                // Re-sync subscriptions when role changes
                syncSubscriptions(areNotificationsEnabled.value, isUserAdmin)
            }
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        areNotificationsEnabled.value = enabled
        prefs.edit().putBoolean("notifications_enabled", enabled).apply()
        syncSubscriptions(enabled, isAdmin.value)
    }

    private fun syncSubscriptions(enabled: Boolean, isUserAdmin: Boolean) {
        if (enabled) {
            // Enable topics
            repo.subscribeToTopic("general_announcements") // Example general topic
            if (isUserAdmin) {
                repo.subscribeToTopic("admin_notifications")
            }
        } else {
            // Disable ALL topics
            repo.unsubscribeFromTopic("general_announcements")
            repo.unsubscribeFromTopic("admin_notifications")
        }
    }
}

class DrawerVmFactory(
    private val app: Application,
    private val repo: FirebaseRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        DrawerViewModel(app, repo) as T
}