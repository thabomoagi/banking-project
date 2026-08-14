package com.howsoutha.app.ui.thirtyseconds

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.howsoutha.app.data.network.HowSouthaApi
import com.howsoutha.app.data.network.ThirtySecondsGameData
import com.howsoutha.app.data.network.ThirtySecondsRound
import com.howsoutha.app.data.network.ThirtySecondsScoreRequest
import com.howsoutha.app.data.network.ThirtySecondsStartRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

data class ThirtySecondsState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val gameData: ThirtySecondsGameData? = null,
    val currentRound: ThirtySecondsRound? = null,
    val words: List<String> = emptyList(),
    val isGameOver: Boolean = false
)

@HiltViewModel
class ThirtySecondsViewModel @Inject constructor(
    private val api: HowSouthaApi
) : ViewModel() {

    private val _state = MutableStateFlow(ThirtySecondsState())
    val state: StateFlow<ThirtySecondsState> = _state

    fun startGame() {
        viewModelScope.launch {
            _state.value = ThirtySecondsState(isLoading = true)
            try {
                val request = ThirtySecondsStartRequest(
                    mode = "SOLO",
                    playerNames = listOf("Player 1"),
                    roundsPerPlayer = 1
                )
                val response = api.startThirtySecondsGame(request)
                if (response.success && response.data != null) {
                    val firstRound = response.data.rounds.firstOrNull()
                    val words = firstRound?.prompt?.split(",")?.map { it.trim() } ?: emptyList()
                    _state.value = ThirtySecondsState(
                        gameData = response.data,
                        currentRound = firstRound,
                        words = words
                    )
                } else {
                    _state.value = ThirtySecondsState(error = response.message)
                }
            } catch (e: HttpException) {
                _state.value = ThirtySecondsState(error = "Failed to start game")
            } catch (e: IOException) {
                _state.value = ThirtySecondsState(error = "No internet connection")
            } catch (e: Exception) {
                _state.value = ThirtySecondsState(error = "Something went wrong")
            }
        }
    }

    fun submitScore(score: Int) {
        viewModelScope.launch {
            val gameData = _state.value.gameData ?: return@launch
            val currentRound = _state.value.currentRound ?: return@launch
            try {
                val request = ThirtySecondsScoreRequest(
                    roundId = currentRound.roundId,
                    score = score
                )
                api.scoreThirtySecondsRound(gameData.gameId, request)
                api.completeThirtySecondsGame(gameData.gameId)
                _state.value = _state.value.copy(isGameOver = true)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Failed to submit score")
            }
        }
    }
}