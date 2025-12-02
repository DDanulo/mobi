package com.donchik.akadeska.presentation.home
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.donchik.akadeska.data.FirebaseRepository

class HomeVmFactory(private val repo: FirebaseRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        HomeViewModel(repo) as T
}