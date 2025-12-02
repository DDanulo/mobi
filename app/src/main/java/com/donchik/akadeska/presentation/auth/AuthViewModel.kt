package com.donchik.akadeska.presentation.auth
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.donchik.akadeska.data.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AuthMode { SIGN_IN, SIGN_UP }

data class AuthState(
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val mode: AuthMode = AuthMode.SIGN_IN,
    val signedIn: Boolean = false
)

class AuthViewModel(private val repo: FirebaseRepository) : ViewModel() {
    val state = MutableStateFlow(AuthState())

    fun setEmail(s: String) = state.update { it.copy(email = s) }
    fun setPassword(s: String) = state.update { it.copy(password = s) }
    fun switchMode() = state.update {
        it.copy(mode = if (it.mode == AuthMode.SIGN_IN) AuthMode.SIGN_UP else AuthMode.SIGN_IN, error = null)
    }

    fun submit() = viewModelScope.launch {
        val s = state.value
        if (s.email.isBlank() || s.password.length < 6) {
            state.update { it.copy(error = "Email i min. 6 znaków hasła") }
            return@launch
        }
        state.update { it.copy(loading = true, error = null) }
        val res = if (s.mode == AuthMode.SIGN_IN)
            repo.signIn(s.email, s.password) else repo.signUp(s.email, s.password)

        res.onSuccess { state.update { it.copy(loading = false, signedIn = true) } }
            .onFailure { e -> state.update { it.copy(loading = false, error = e.message ?: "Auth error") } }
    }
}