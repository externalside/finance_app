package com.example.financeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.financeapp.ui.screens.*
import com.example.financeapp.ui.theme.FinanceAppTheme
import com.example.financeapp.ui.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.foundation.layout.Column

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Оптимизация отрисовки окна
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val isDarkMode by settingsViewModel.isDarkMode.collectAsState()
            val isPinEnabled by settingsViewModel.isPinEnabled.collectAsState()
            var showPinDialog by remember { mutableStateOf(isPinEnabled) }
            var isAuthenticated by remember { mutableStateOf(!isPinEnabled) }

            DisposableEffect(Unit) {
                onDispose {
                    // Сохраняем состояние темы при закрытии приложения
                    settingsViewModel.saveDarkModeState(isDarkMode)
                }
            }

            FinanceAppTheme(
                darkTheme = isDarkMode,
                dynamicColor = false // Отключаем динамические цвета для консистентности
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (showPinDialog && !isAuthenticated) {
                        PinEntryDialog(
                            onPinEntered = { pin ->
                                if (settingsViewModel.validatePin(pin)) {
                                    isAuthenticated = true
                                    showPinDialog = false
                                }
                            }
                        )
                    } else {
                        FinanceAppNavigation()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceAppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    // Определение пунктов навигации
    val navigationItems = listOf(
        NavigationItem(
            route = "home",
            icon = Icons.Default.Home,
            label = "Главная"
        ),
        NavigationItem(
            route = "settings",
            icon = Icons.Default.Settings,
            label = "Настройки"
        )
    )
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                navigationItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                            // Если переходим на главную, закрываем все остальные экраны
                            if (item.route == "home") {
                                navController.popBackStack("statistics", true)
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") { 
                HomeScreen(
                    onNavigateToStatistics = { navController.navigate("statistics") }
                )
            }
            composable("statistics") {
                StatisticsScreen(
                    navController = navController
                )
            }
            composable("settings") {
                SettingsScreen(
                    navController = navController
                )
            }
            composable("about") {
                AboutScreen(
                    navController = navController
                )
            }
        }
    }
}

@Composable
private fun PinEntryDialog(
    onPinEntered: (String) -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { /* Нельзя закрыть */ },
        title = { Text("Введите PIN-код") },
        text = {
            Column {
                OutlinedTextField(
                    value = pin,
                    onValueChange = {
                        if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                            pin = it
                            error = ""
                        }
                    },
                    label = { Text("PIN-код") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword
                    ),
                    singleLine = true,
                    isError = error.isNotEmpty(),
                    supportingText = if (error.isNotEmpty()) {
                        { Text(error) }
                    } else null
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (pin.length == 4) {
                        onPinEntered(pin)
                        pin = ""
                    } else {
                        error = "PIN-код должен состоять из 4 цифр"
                    }
                }
            ) {
                Text("Войти")
            }
        }
    )
}

private data class NavigationItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) 