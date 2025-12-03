package com.donchik.akadeska.presentation.archive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.donchik.akadeska.data.FeedItem
import com.donchik.akadeska.data.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// State specifically for the Archive screen
data class ArchiveState(
    val items: List<FeedItem> = emptyList(),
    val loading: Boolean = true,
    val error: String? = null
)

class ArchiveViewModel(private val repo: FirebaseRepository) : ViewModel() {
    val state = MutableStateFlow(ArchiveState())

    init {
        viewModelScope.launch {
            // We call the new function observeArchivedPosts (defined in step 2)
            repo.observeArchivedPosts().collect { list ->
                state.update { it.copy(items = list, loading = false, error = null) }
            }
        }
    }
}