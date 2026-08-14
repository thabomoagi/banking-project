package com.howsoutha.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.howsoutha.app.data.network.AuthRequest
import com.howsoutha.app.data.network.HowSouthaApi
import com.howsoutha.app.data.network.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

data class AuthState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val api: HowSouthaApi,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state

    fun login(identifier: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthState(isLoading = true)
            try {
                val response = api.login(AuthRequest(identifier = identifier, password = password))
                if (response.success && response.data != null) {
                    tokenManager.saveTokens(response.data.accessToken, response.data.refreshToken)
                    _state.value = AuthState(isSuccess = true)
                } else {
                    _state.value = AuthState(error = response.message)
                }
            } catch (e: HttpException) {
                val message = when (e.code()) {
                    401 -> "Incorrect username or password"
                    403 -> "Access denied"
                    404 -> "User not found"
                    500 -> "Server error, please try again later"
                    else -> "Something went wrong, please try again"
                }
                _state.value = AuthState(error = message)
            } catch (e: IOException) {
                _state.value = AuthState(error = "No internet connection, please check your network")
            } catch (e: Exception) {
                _state.value = AuthState(error = "Something went wrong, please try again")
            }
        }
    }

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthState(isLoading = true)
            try {
                val response = api.register(AuthRequest(username = username, email = email, password = password))
                if (response.success && response.data != null) {
                    tokenManager.saveTokens(response.data.accessToken, response.data.refreshToken)
                    _state.value = AuthState(isSuccess = true)
                } else {
                    _state.value = AuthState(error = response.message)
                }
            } catch (e: HttpException) {
                val message = when (e.code()) {
                    400 -> "Please check your details and try again"
                    409 -> "Username or email already exists"
                    500 -> "Server error, please try again later"
                    else -> "Something went wrong, please try again"
                }
                _state.value = AuthState(error = message)
            } catch (e: IOException) {
                _state.value = AuthState(error = "No internet connection, please check your network")
            } catch (e: Exception) {
                _state.value = AuthState(error = "Something went wrong, please try again")
            }
        }
    }
}