package com.donchik.akadeska.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.donchik.akadeska.data.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AdminViewModel(private val repo: FirebaseRepository) : ViewModel() {
    data class Item(val id: String, val title: String, val type: String)

    val isAdmin = MutableStateFlow(false)
    val pending = MutableStateFlow<List<Item>>(emptyList())
    val loading = MutableStateFlow(true)
    val error = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch {
            repo.isAdminFlow().collect { isAdmin.value = it }
        }
        viewModelScope.launch {
            repo.observePendingPosts().collect { list ->
                pending.value = list.map { (id, data) ->
                    Item(
                        id = id,
                        title = data["title"] as? String ?: "(no title)",
                        type = (data["type"] as? String ?: "").uppercase()
                    )
                }
                loading.value = false
            }
        }
    }

    fun approve(id: String) = viewModelScope.launch {
        repo.approvePost(id).onFailure { error.value = it.message }
    }
    fun reject(id: String) = viewModelScope.launch {
        repo.rejectPost(id).onFailure { error.value = it.message }
    }
}