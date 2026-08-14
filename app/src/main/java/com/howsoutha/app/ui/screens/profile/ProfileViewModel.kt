package com.howsoutha.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.howsoutha.app.data.network.HowSouthaApi
import com.howsoutha.app.data.network.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val isLoading: Boolean = false,
    val totalQnaAttempts: Int = 0,
    val qnaBestScore: Int = 0,
    val totalThirtySecondsGames: Int = 0,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val api: HowSouthaApi,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state

    init {
        fetchStats()
    }

    fun fetchStats() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val response = api.getStats()
                if (response.success && response.data != null) {
                    _state.value = ProfileState(
                        isLoading = false,
                        totalQnaAttempts = response.data.totalQnaAttempts,
                        qnaBestScore = response.data.qnaBestScore,
                        totalThirtySecondsGames = response.data.totalThirtySecondsGames
                    )
                } else {
                    _state.value = _state.value.copy(isLoading = false, error = response.message)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenManager.clearTokens()
        }
    }
}