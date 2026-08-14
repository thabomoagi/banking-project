package com.howsoutha.app.ui.screens.multiplechoice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.howsoutha.app.navigation.Screen
import com.howsoutha.app.ui.multiplechoice.MultipleChoiceViewModel
import kotlinx.coroutines.delay

@Composable
fun MultipleChoiceGameScreen(
    navController: NavHostController,
    viewModel: MultipleChoiceViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var timeLeft by remember { mutableIntStateOf(30) }
    var timerStarted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.startGame()
    }

    LaunchedEffect(state.gameData) {
        if (state.gameData != null && !timerStarted) {
            timerStarted = true
            while (timeLeft > 0 && !state.isGameOver) {
                delay(1000L)
                timeLeft--
            }
            if (!state.isGameOver) {
                viewModel.timeUp()
            }
        }
    }

    LaunchedEffect(state.isGameOver) {
        if (state.isGameOver) {
            navController.navigate("multiple_choice_result/${state.score}/${state.totalQuestions}") {
                popUpTo(Screen.MultipleChoiceGame.route) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$timeLeft",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
                state.error != null -> {
                    Text(text = state.error!!, color = Color.Red)
                }
                state.currentQuestion != null -> {
                    Text(
                        text = "Question ${state.currentQuestionIndex + 1} of ${state.totalQuestions}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = state.currentQuestion!!.prompt,
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    HorizontalDivider(color = MaterialTheme.colorScheme.primary)

                    Spacer(modifier = Modifier.height(24.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        state.currentQuestion!!.options.forEach { option ->
                            OutlinedButton(
                                onClick = { viewModel.selectAnswer(option.id) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                )
                            ) {
                                Text(option.optionText, style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Answer as quickly as possible!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}