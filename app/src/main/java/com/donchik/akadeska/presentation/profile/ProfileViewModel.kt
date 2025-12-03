package com.donchik.akadeska.presentation.profile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.donchik.akadeska.data.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileState(
    val email: String = "",
    val displayName: String = "",
    val isAdmin: Boolean = false,
    val loading: Boolean = false,
    val error: String? = null,
    val isSignedOut: Boolean = false
)

class ProfileViewModel(private val repo: FirebaseRepository) : ViewModel() {
    val state = MutableStateFlow(ProfileState())

    init {
        loadUserData()
    }

    private fun loadUserData() {
        val user = repo.currentUser
        if (user != null) {
            state.update {
                it.copy(
                    email = user.email ?: "",
                    displayName = user.displayName ?: ""
                )
            }
            // Check admin status
            viewModelScope.launch {
                repo.isAdminFlow().collect { isAdmin ->
                    state.update { it.copy(isAdmin = isAdmin) }
                }
            }
        } else {
            // Not logged in
            state.update { it.copy(isSignedOut = true) }
        }
    }

    fun updateName(newName: String) = viewModelScope.launch {
        if (newName.isBlank()) return@launch
        state.update { it.copy(loading = true, error = null) }

        repo.updateUserName(newName)
            .onSuccess {
                state.update { it.copy(loading = false, displayName = newName, error = null) }
            }
            .onFailure { e ->
                state.update { it.copy(loading = false, error = e.message) }
            }
    }

    fun signOut() {
        repo.signOut()
        state.update { it.copy(isSignedOut = true) }
    }
}