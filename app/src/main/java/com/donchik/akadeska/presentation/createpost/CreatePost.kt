package com.donchik.akadeska.presentation.createpost

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.donchik.akadeska.data.FirebaseRepository
import com.donchik.akadeska.domain.model.PostType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class CreatePostState(
    val type: PostType = PostType.INFO,
    val title: String = "",
    val body: String = "",
    val price: String = "",
    val imageBytes: ByteArray? = null,
    val loading: Boolean = false,
    val error: String? = null,
    val createdId: String? = null
)

class CreatePostViewModel(
    private val repo: FirebaseRepository
) : ViewModel() {

    val state = MutableStateFlow(CreatePostState())

    fun setType(t: PostType) = state.update { it.copy(type = t) }
    fun setTitle(s: String) = state.update { it.copy(title = s) }
    fun setBody(s: String) = state.update { it.copy(body = s) }
    fun setPrice(s: String) = state.update { it.copy(price = s) }
    fun setImage(bytes: ByteArray?) = state.update { it.copy(imageBytes = bytes) }

    fun submit() = viewModelScope.launch {
        val s = state.value
        if (s.title.isBlank()) { state.update { it.copy(error = "Title required") }; return@launch }
        if (s.type == PostType.LISTING && s.price.toDoubleOrNull() == null) {
            state.update { it.copy(error = "Price required") }; return@launch
        }

        state.update { it.copy(loading = true, error = null) }
        val result = repo.createPost(
            type = s.type,
            title = s.title,
            body = s.body,
            price = s.price.toDoubleOrNull(),
            imageBytes = s.imageBytes
        )
        result.onSuccess { id ->
            state.update { it.copy(loading = false, createdId = id) }
        }.onFailure { e ->
            state.update { it.copy(loading = false, error = e.message ?: "Error") }
        }
    }
}