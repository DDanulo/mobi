package com.donchik.akadeska.presentation.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.donchik.akadeska.data.FirebaseRepository

class ShopVmFactory(private val repo: FirebaseRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        ShopViewModel(repo) as T
}