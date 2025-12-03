package com.donchik.akadeska.presentation.archive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.donchik.akadeska.data.FirebaseRepository

class ArchiveVmFactory(private val repo: FirebaseRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        ArchiveViewModel(repo) as T
}