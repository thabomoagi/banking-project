package com.howsoutha.app.data.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface HowSouthaApi {

    @GET("health")
    suspend fun checkHealth(): BaseResponse<String>

    @POST("auth/register")
    suspend fun register(@Body request: AuthRequest): BaseResponse<AuthData>

    @POST("auth/login")
    suspend fun login(@Body request: AuthRequest): BaseResponse<AuthData>

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshRequest): BaseResponse<AuthData>

    @GET("users/me/stats")
    suspend fun getStats(): BaseResponse<StatsData>

    @POST("thirty-seconds/games/start")
    suspend fun startThirtySecondsGame(@Body request: ThirtySecondsStartRequest): BaseResponse<ThirtySecondsGameData>

    @POST("thirty-seconds/games/{gameId}/rounds/score")
    suspend fun scoreThirtySecondsRound(
        @Path("gameId") gameId: String,
        @Body request: ThirtySecondsScoreRequest
    ): BaseResponse<String>

    @POST("thirty-seconds/games/{gameId}/complete")
    suspend fun completeThirtySecondsGame(@Path("gameId") gameId: String): BaseResponse<String>

    @POST("qna/attempts/start")
    suspend fun startQnaGame(@Body request: QnaStartRequest): BaseResponse<QnaGameData>

    @POST("qna/attempts/{attemptId}/submit")
    suspend fun submitQnaGame(
        @Path("attemptId") attemptId: String,
        @Body request: QnaSubmitRequest
    ): BaseResponse<QnaResultData>
}