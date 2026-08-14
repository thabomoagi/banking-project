package com.howsoutha.app.data.network

data class BaseResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)

data class AuthRequest(
    val username: String? = null,
    val email: String? = null,
    val identifier: String? = null,
    val password: String
)

data class RefreshRequest(
    val refreshToken: String
)

data class AuthData(
    val user: User,
    val accessToken: String,
    val refreshToken: String
)

data class User(
    val id: String,
    val username: String,
    val email: String
)

data class StatsData(
    val totalQnaAttempts: Int,
    val qnaBestScore: Int,
    val totalThirtySecondsGames: Int
)

data class ThirtySecondsStartRequest(
    val mode: String,
    val playerNames: List<String>,
    val roundsPerPlayer: Int
)

data class ThirtySecondsGameData(
    val gameId: String,
    val rounds: List<ThirtySecondsRound>
)

data class ThirtySecondsRound(
    val roundId: Int,
    val roundNumber: Int,
    val playerName: String,
    val prompt: String
)

data class ThirtySecondsScoreRequest(
    val roundId: Int,
    val score: Int
)

data class QnaStartRequest(
    val questionCount: Int,
    val durationSeconds: Int
)

data class QnaGameData(
    val attemptId: String,
    val questions: List<QnaQuestion>
)

data class QnaQuestion(
    val id: Int,
    val prompt: String,
    val options: List<QnaOption>
)

data class QnaOption(
    val id: Int,
    val optionText: String
)

data class QnaSubmitRequest(
    val answers: List<QnaAnswer>
)

data class QnaAnswer(
    val questionId: Int,
    val selectedOptionId: Int,
    val timeTakenMs: Int
)

data class QnaResultData(
    val score: Int,
    val correctCount: Int,
    val totalQuestions: Int
)