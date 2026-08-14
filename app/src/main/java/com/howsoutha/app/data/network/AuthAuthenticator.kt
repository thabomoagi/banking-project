package com.howsoutha.app.data.network

import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val api: dagger.Lazy<HowSouthaApi>
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val currentToken = runBlocking { tokenManager.getAccessToken() }
        val requestToken = response.request.header("Authorization")?.removePrefix("Bearer ")

        if (currentToken != requestToken) {
            return response.request.newBuilder()
                .header("Authorization", "Bearer $currentToken")
                .build()
        }

        val refreshToken = runBlocking { tokenManager.getRefreshToken() }
        if (refreshToken.isNullOrEmpty()) return null

        val refreshResponse = runBlocking {
            try {
                api.get().refreshToken(RefreshRequest(refreshToken))
            } catch (e: Exception) {
                null
            }
        }

        if (refreshResponse?.success == true && refreshResponse.data != null) {
            runBlocking {
                tokenManager.saveTokens(
                    refreshResponse.data.accessToken,
                    refreshResponse.data.refreshToken
                )
            }
            return response.request.newBuilder()
                .header("Authorization", "Bearer ${refreshResponse.data.accessToken}")
                .build()
        }

        runBlocking { tokenManager.clearTokens() }
        return null
    }
}