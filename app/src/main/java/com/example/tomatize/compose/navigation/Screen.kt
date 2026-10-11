package com.example.tomatize.compose.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Statistics : Screen("statistics")
    data object Shop : Screen("shop")
    data object Profile : Screen("profile")
    data object HabitDetailStats : Screen("habit_detail_stats/{habitId}") {
        fun createRoute(habitId: Long) = "habit_detail_stats/$habitId"
    }
}
