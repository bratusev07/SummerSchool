package ru.bratusev.summerschool

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.bratusev.summerschool.ui.theme.SummerSchoolTheme

// Определение sealed класса для навигационных экранов приложения
sealed class Screen(val route: String) {
    // Экран домашней страницы с маршрутом "home"
    object Home : Screen("home")
    // Экран входа с маршрутом "login"
    object Login : Screen("login")
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SummerSchoolTheme {
                // Инициализация NavController для управления навигацией
                val navController = rememberNavController()

                // Настройка NavHost - корневой контейнер для навигационных графов
                // navController - используемый контрлер
                // startDestination - стартовый экран
                NavHost(
                    navController = navController,
                    startDestination = Screen.Login.route
                ) {
                    // Регистрация экрана Home в навигационном графе
                    composable(Screen.Home.route) {
                        HomeScreen()
                    }

                    // Регистрация экрана Login в навигационном графе
                    // Передача NavController позволяет управлять навигацией из LoginScreen
                    composable(Screen.Login.route) {
                        LoginScreen(navController = navController)
                    }
                }
            }
        }
    }
}