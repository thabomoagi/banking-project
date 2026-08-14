package com.howsoutha.app.ui.multiplechoice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.howsoutha.app.data.network.HowSouthaApi
import com.howsoutha.app.data.network.QnaAnswer
import com.howsoutha.app.data.network.QnaGameData
import com.howsoutha.app.data.network.QnaQuestion
import com.howsoutha.app.data.network.QnaStartRequest
import com.howsoutha.app.data.network.QnaSubmitRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

data class MultipleChoiceState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val gameData: QnaGameData? = null,
    val currentQuestionIndex: Int = 0,
    val currentQuestion: QnaQuestion? = null,
    val answers: MutableList<QnaAnswer> = mutableListOf(),
    val score: Int = 0,
    val totalQuestions: Int = 0,
    val isGameOver: Boolean = false
)

@HiltViewModel
class MultipleChoiceViewModel @Inject constructor(
    private val api: HowSouthaApi
) : ViewModel() {

    private val _state = MutableStateFlow(MultipleChoiceState())
    val state: StateFlow<MultipleChoiceState> = _state

    fun startGame() {
        viewModelScope.launch {
            _state.value = MultipleChoiceState(isLoading = true)
            try {
                val request = QnaStartRequest(
                    questionCount = 5,
                    durationSeconds = 120
                )
                val response = api.startQnaGame(request)
                if (response.success && response.data != null) {
                    val firstQuestion = response.data.questions.firstOrNull()
                    _state.value = MultipleChoiceState(
                        gameData = response.data,
                        currentQuestion = firstQuestion,
                        totalQuestions = response.data.questions.size
                    )
                } else {
                    _state.value = MultipleChoiceState(error = response.message)
                }
            } catch (e: HttpException) {
                _state.value = MultipleChoiceState(error = "Failed to start game")
            } catch (e: IOException) {
                _state.value = MultipleChoiceState(error = "No internet connection")
            } catch (e: Exception) {
                _state.value = MultipleChoiceState(error = "Something went wrong")
            }
        }
    }

    fun selectAnswer(optionId: Int) {
        val currentQuestion = _state.value.currentQuestion ?: return
        val answer = QnaAnswer(
            questionId = currentQuestion.id,
            selectedOptionId = optionId,
            timeTakenMs = 0
        )
        _state.value.answers.add(answer)

        val nextIndex = _state.value.currentQuestionIndex + 1
        val questions = _state.value.gameData?.questions ?: emptyList()

        if (nextIndex < questions.size) {
            _state.value = _state.value.copy(
                currentQuestionIndex = nextIndex,
                currentQuestion = questions[nextIndex]
            )
        } else {
            submitAnswers()
        }
    }

    fun submitAnswers() {
        viewModelScope.launch {
            val gameData = _state.value.gameData ?: return@launch
            try {
                val request = QnaSubmitRequest(answers = _state.value.answers.toList())
                val response = api.submitQnaGame(gameData.attemptId, request)
                if (response.success && response.data != null) {
                    _state.value = _state.value.copy(
                        score = response.data.correctCount,
                        totalQuestions = response.data.totalQuestions,
                        isGameOver = true
                    )
                } else {
                    _state.value = _state.value.copy(error = response.message)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Failed to submit answers")
            }
        }
    }

    fun timeUp() {
        submitAnswers()
    }
}