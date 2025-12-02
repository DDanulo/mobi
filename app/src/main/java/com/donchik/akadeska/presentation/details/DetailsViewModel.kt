package com.donchik.akadeska.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.donchik.akadeska.data.FirebaseRepository
import com.donchik.akadeska.data.PostDetails
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DetailsState(
    val loading: Boolean = true,
    val post: PostDetails? = null,
    val error: String? = null
)

class DetailsViewModel(
    private val repo: FirebaseRepository,
    private val postId: String
) : ViewModel() {
    val state = MutableStateFlow(DetailsState())

    init {
        viewModelScope.launch {
            repo.observePost(postId).collect { p ->
                if (p == null) state.value = DetailsState(loading = false, error = "Not found")
                else state.value = DetailsState(loading = false, post = p)
            }
        }
    }
}