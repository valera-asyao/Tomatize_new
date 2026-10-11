package com.example.tomatize.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tomatize.compose.components.BottomNavBar
import com.example.tomatize.compose.components.TopNotificationBar
import com.example.tomatize.compose.components.TopNotificationData
import com.example.tomatize.compose.dialogs.AddHabitComposeDialog
import com.example.tomatize.compose.dialogs.DeleteHabitComposeDialog
import com.example.tomatize.compose.dialogs.EditHabitComposeDialog
import com.example.tomatize.compose.navigation.Screen
import com.example.tomatize.compose.screens.HabitDetailStatisticsScreen
import com.example.tomatize.compose.screens.HomeScreen
import com.example.tomatize.compose.screens.ProfileScreen
import com.example.tomatize.compose.screens.ShopScreen
import com.example.tomatize.compose.screens.StatisticsScreen
import com.example.tomatize.compose.theme.BackgroundLight
import com.example.tomatize.compose.theme.Black
import com.example.tomatize.compose.theme.GnfFontFamily
import com.example.tomatize.compose.theme.TomatizeTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ui.Habit
import ui.HabitDatabaseHelper

@Composable
fun TomatizeApp(
    databaseHelper: HabitDatabaseHelper,
    modifier: Modifier = Modifier
) {
    TomatizeTheme {
        val navController = rememberNavController()
        val coroutineScope = rememberCoroutineScope()

        var topNotification by remember { mutableStateOf<TopNotificationData?>(null) }
        var showAddHabitDialog by remember { mutableStateOf(false) }
        var editingHabit by remember { mutableStateOf<Habit?>(null) }
        var deletingHabit by remember { mutableStateOf<Habit?>(null) }
        var showSplash by remember { mutableStateOf(true) }

        // Home refresh trigger counter
        var homeRefreshTrigger by remember { mutableIntStateOf(0) }

        fun triggerNotification(message: String) {
            coroutineScope.launch {
                topNotification = TopNotificationData(message)
                delay(2200)
                topNotification = null
            }
        }

        // Auto-dismiss splash screen
        LaunchedEffect(Unit) {
            delay(1200)
            showSplash = false
        }

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        val selectedTabIndex = when {
            currentRoute == Screen.Home.route -> 0
            currentRoute == Screen.Statistics.route -> 1
            currentRoute == Screen.Shop.route -> 3
            currentRoute == Screen.Profile.route -> 4
            else -> -1
        }

        val showBottomBar = selectedTabIndex != -1

        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                contentWindowInsets = WindowInsets.systemBars,
                bottomBar = {
                    if (showBottomBar) {
                        BottomNavBar(
                            selectedIndex = selectedTabIndex,
                            onTabSelected = { index ->
                                val targetRoute = when (index) {
                                    0 -> Screen.Home.route
                                    1 -> Screen.Statistics.route
                                    3 -> Screen.Shop.route
                                    4 -> Screen.Profile.route
                                    else -> Screen.Home.route
                                }
                                if (currentRoute != targetRoute) {
                                    navController.navigate(targetRoute) {
                                        popUpTo(Screen.Home.route) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            onAddClick = { showAddHabitDialog = true }
                        )
                    }
                },
                containerColor = BackgroundLight
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route,
                    modifier = Modifier.padding(innerPadding),
                    enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) + fadeIn(animationSpec = tween(300)) },
                    exitTransition = { slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300)) + fadeOut(animationSpec = tween(300)) },
                    popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300)) + fadeIn(animationSpec = tween(300)) },
                    popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) + fadeOut(animationSpec = tween(300)) }
                ) {
                    composable(Screen.Home.route) {
                        HomeScreen(
                            databaseHelper = databaseHelper,
                            onShowNotification = ::triggerNotification,
                            onGoToStats = { habitId ->
                                navController.navigate(Screen.HabitDetailStats.createRoute(habitId))
                            },
                            onEditHabitRequest = { editingHabit = it },
                            onDeleteHabitRequest = { deletingHabit = it }
                        )
                    }

                    composable(Screen.Statistics.route) {
                        StatisticsScreen(
                            databaseHelper = databaseHelper,
                            onHabitClick = { habitId ->
                                navController.navigate(Screen.HabitDetailStats.createRoute(habitId))
                            }
                        )
                    }

                    composable(Screen.Shop.route) {
                        ShopScreen(onShowNotification = ::triggerNotification)
                    }

                    composable(Screen.Profile.route) {
                        ProfileScreen(onShowNotification = ::triggerNotification)
                    }

                    composable(
                        route = Screen.HabitDetailStats.route,
                        arguments = listOf(navArgument("habitId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val habitId = backStackEntry.arguments?.getLong("habitId") ?: -1L
                        HabitDetailStatisticsScreen(
                            habitId = habitId,
                            databaseHelper = databaseHelper,
                            onBackClick = { navController.popBackStack() },
                            onShowNotification = ::triggerNotification
                        )
                    }
                }
            }

            // Top Notification Bar
            TopNotificationBar(
                data = topNotification,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            )

            // Splash Screen
            AnimatedVisibility(
                visible = showSplash,
                enter = fadeIn(),
                exit = fadeOut(animationSpec = tween(500)),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BackgroundLight)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {},
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "TOMATIZE",
                        fontFamily = GnfFontFamily,
                        fontSize = 48.sp,
                        color = Black
                    )
                }
            }
        }

        // --- Dialogs ---
        if (showAddHabitDialog) {
            AddHabitComposeDialog(
                onDismiss = { showAddHabitDialog = false },
                onAddHabit = { newHabit ->
                    val id = databaseHelper.addHabit(newHabit)
                    showAddHabitDialog = false
                    if (id != -1L) {
                        homeRefreshTrigger++
                        triggerNotification("Привычка добавлена!")
                    }
                },
                checkNameExists = { databaseHelper.existsByName(it) }
            )
        }

        editingHabit?.let { habitToEdit ->
            EditHabitComposeDialog(
                habit = habitToEdit,
                onDismiss = { editingHabit = null },
                onSaveHabit = { newName, newDesc ->
                    databaseHelper.updateHabit(habitToEdit.id, newName, newDesc)
                    editingHabit = null
                    homeRefreshTrigger++
                    triggerNotification("Привычка обновлена")
                },
                checkNameExistsExceptId = { name, id ->
                    databaseHelper.existsByNameExceptId(name, id)
                }
            )
        }

        deletingHabit?.let { habitToDelete ->
            DeleteHabitComposeDialog(
                onDismiss = { deletingHabit = null },
                onConfirmDelete = {
                    databaseHelper.deleteHabit(habitToDelete.id)
                    deletingHabit = null
                    homeRefreshTrigger++
                    triggerNotification("Привычка удалена")
                }
            )
        }
    }
}
