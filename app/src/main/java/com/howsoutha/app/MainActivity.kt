package com.howsoutha.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.howsoutha.app.data.local.SettingsManager
import com.howsoutha.app.navigation.AppNavGraph
import com.howsoutha.app.ui.theme.HowSouthaTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsManager: SettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDarkMode by settingsManager.isDarkModeFlow.collectAsState(initial = false)
            HowSouthaTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()
                AppNavGraph(navController = navController)
            }
        }
    }
}