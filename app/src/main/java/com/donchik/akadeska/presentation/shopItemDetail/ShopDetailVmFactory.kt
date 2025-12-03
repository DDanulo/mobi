package com.donchik.akadeska.presentation.shopItemDetail


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.donchik.akadeska.data.FirebaseRepository

class ShopDetailsVmFactory(
    private val repo: FirebaseRepository,
    private val postId: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        ShopDetailsViewModel(repo, postId) as T
}