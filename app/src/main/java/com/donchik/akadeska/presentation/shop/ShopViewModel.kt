package com.donchik.akadeska.presentation.shop
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.donchik.akadeska.data.FirebaseRepository
import com.donchik.akadeska.data.ShopItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ShopState(
    val items: List<ShopItem> = emptyList(),
    val loading: Boolean = true,
    val error: String? = null
)

class ShopViewModel(private val repo: FirebaseRepository) : ViewModel() {
    val state = MutableStateFlow(ShopState())

    init {
        viewModelScope.launch {
            repo.observeShopListings().collect { list ->
                state.update { it.copy(items = list, loading = false, error = null) }
            }
        }
    }
}