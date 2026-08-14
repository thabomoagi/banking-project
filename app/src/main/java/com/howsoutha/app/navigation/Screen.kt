package com.howsoutha.app.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object ThirtySecondsRules : Screen("thirty_seconds_rules")
    object ThirtySecondsGame : Screen("thirty_seconds_game")
    object ThirtySecondsResult : Screen("thirty_seconds_result")
    object MultipleChoiceRules : Screen("multiple_choice_rules")
    object MultipleChoiceGame : Screen("multiple_choice_game")
    object MultipleChoiceResult : Screen("multiple_choice_result/{score}/{total}")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
}