package com.donchik.akadeska.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.donchik.akadeska.data.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AdminGateViewModel(private val repo: FirebaseRepository) : ViewModel() {
    val isAdmin = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            repo.isAdminFlow().collect { isUserAdmin ->
                isAdmin.value = isUserAdmin

                // Logic: If Admin, subscribe. If not, unsubscribe.
                if (isUserAdmin) {
                    repo.subscribeToAdminTopic()
                } else {
                    repo.unsubscribeFromAdminTopic()
                }
            }
        }
    }
}

class AdminGateVmFactory(private val repo: FirebaseRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = AdminGateViewModel(repo) as T
}