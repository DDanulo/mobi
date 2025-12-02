package com.donchik.akadeska.presentation.home
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.donchik.akadeska.data.FeedItem
import com.donchik.akadeska.data.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class HomeState(
    val items: List<FeedItem> = emptyList(),
    val loading: Boolean = true,
    val error: String? = null
)

class HomeViewModel(private val repo: FirebaseRepository) : ViewModel() {
    val state = MutableStateFlow(HomeState())

    init {
        viewModelScope.launch {
            repo.observeApprovedAnnouncements().collect { list ->
                state.update { it.copy(items = list, loading = false, error = null) }
            }
        }
    }
}