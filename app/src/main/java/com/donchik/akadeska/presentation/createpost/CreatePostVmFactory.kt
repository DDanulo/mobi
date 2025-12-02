package com.donchik.akadeska.presentation.createpost

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.donchik.akadeska.data.FirebaseRepository

class CreatePostVmFactory(
    private val repo: FirebaseRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CreatePostViewModel(repo) as T
    }
}
