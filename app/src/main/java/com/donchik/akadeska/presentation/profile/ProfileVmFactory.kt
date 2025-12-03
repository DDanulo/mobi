package com.donchik.akadeska.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.donchik.akadeska.data.FirebaseRepository

class ProfileVmFactory(private val repo: FirebaseRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        ProfileViewModel(repo) as T
}