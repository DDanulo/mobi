package com.donchik.akadeska.presentation.shopItemDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.donchik.akadeska.data.FirebaseRepository
import com.donchik.akadeska.data.PostDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ShopDetailsState(
    val loading: Boolean = true,
    val item: PostDetails? = null,
    val error: String? = null,
    val isDeleted: Boolean = false
)

class ShopDetailsViewModel(
    private val repo: FirebaseRepository,
    private val postId: String
) : ViewModel() {

    val state = MutableStateFlow(ShopDetailsState())

    init {
        viewModelScope.launch {
            repo.observePost(postId).collect { details ->
                if (details == null) {
                    state.update { it.copy(loading = false, error = "Item not found") }
                } else {
                    state.update { it.copy(loading = false, item = details, error = null) }
                }
            }
        }
    }

    fun reserveItem() = viewModelScope.launch {
        repo.reservePost(postId).onFailure { e ->
            state.update { it.copy(error = e.message) }
        }
    }

    fun cancelReservation() = viewModelScope.launch {
        repo.cancelReservation(postId).onFailure { e ->
            state.update { it.copy(error = e.message) }
        }
    }
    fun deleteItem() = viewModelScope.launch {
        repo.deletePost(postId)
            .onSuccess {
                state.update { it.copy(isDeleted = true) }
            }
            .onFailure { e ->
                state.update { it.copy(error = e.message) }
            }
    }
}