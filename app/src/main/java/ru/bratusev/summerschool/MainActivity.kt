package ru.bratusev.summerschool

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.bratusev.summerschool.data.AppDatabase
import ru.bratusev.summerschool.ui.theme.SummerSchoolTheme

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Login : Screen("login")
}

class MainActivity : ComponentActivity() {

    companion object {
        lateinit var database: AppDatabase
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        database = AppDatabase.getDatabase(applicationContext)
        setContent {
            SummerSchoolTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Screen.Login.route
                ) {
                    composable(Screen.Home.route) {
                        HomeScreen()
                    }

                    composable(Screen.Login.route) {
                        LoginScreen(navController = navController)
                    }
                }
            }
        }
    }
}