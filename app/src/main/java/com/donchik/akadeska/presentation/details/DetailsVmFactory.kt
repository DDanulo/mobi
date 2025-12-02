package com.donchik.akadeska.presentation.details

import com.donchik.akadeska.data.FirebaseRepository

class DetailsVmFactory(
    private val repo: FirebaseRepository,
    private val postId: String
) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T =
        DetailsViewModel(repo, postId) as T
}