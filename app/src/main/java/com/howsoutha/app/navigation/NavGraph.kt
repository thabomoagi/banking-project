package com.howsoutha.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.howsoutha.app.ui.screens.auth.LoginScreen
import com.howsoutha.app.ui.screens.auth.RegisterScreen
import com.howsoutha.app.ui.screens.home.HomeScreen
import com.howsoutha.app.ui.screens.multiplechoice.MultipleChoiceGameScreen
import com.howsoutha.app.ui.screens.multiplechoice.MultipleChoiceResultScreen
import com.howsoutha.app.ui.screens.multiplechoice.MultipleChoiceRulesScreen
import com.howsoutha.app.ui.screens.profile.ProfileScreen
import com.howsoutha.app.ui.screens.settings.SettingsScreen
import com.howsoutha.app.ui.screens.thirtyseconds.ThirtySecondsGameScreen
import com.howsoutha.app.ui.screens.thirtyseconds.ThirtySecondsResultScreen
import com.howsoutha.app.ui.screens.thirtyseconds.ThirtySecondsRulesScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Register.route) { RegisterScreen(navController) }
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.ThirtySecondsRules.route) { ThirtySecondsRulesScreen(navController) }
        composable(Screen.ThirtySecondsGame.route) { ThirtySecondsGameScreen(navController) }
        composable(Screen.ThirtySecondsResult.route) { ThirtySecondsResultScreen(navController) }
        composable(Screen.MultipleChoiceRules.route) { MultipleChoiceRulesScreen(navController) }
        composable(Screen.MultipleChoiceGame.route) { MultipleChoiceGameScreen(navController) }
        composable(
            route = Screen.MultipleChoiceResult.route,
            arguments = listOf(
                navArgument("score") { type = NavType.IntType },
                navArgument("total") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val score = backStackEntry.arguments?.getInt("score") ?: 0
            val total = backStackEntry.arguments?.getInt("total") ?: 0
            MultipleChoiceResultScreen(navController, score, total)
        }
        composable(Screen.Profile.route) { ProfileScreen(navController) }
        composable(Screen.Settings.route) { SettingsScreen(navController) }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title)
    }
}