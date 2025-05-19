package com.example.financeapp

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.financeapp.ui.screens.*
import com.example.financeapp.ui.theme.FinanceAppTheme
import com.example.financeapp.ui.viewmodels.SettingsViewModel
import com.example.financeapp.ui.viewmodel.TransactionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val transactionViewModel: TransactionViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            lifecycleScope.launch(Dispatchers.IO) {
                transactionViewModel.importSmsTransactions()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val transactionViewModel: TransactionViewModel = hiltViewModel()
            val settingsViewModel: SettingsViewModel = hiltViewModel()

            val isDarkMode by settingsViewModel.isDarkMode.collectAsState()
            val isPinEnabled by settingsViewModel.isPinEnabled.collectAsState()

            var showPinDialog by remember { mutableStateOf(true) }
            var isAuthenticated by remember { mutableStateOf(false) }

            // Если PIN выключен — сразу авторизуем
            LaunchedEffect(isPinEnabled) {
                if (!isPinEnabled) {
                    isAuthenticated = true
                    showPinDialog = false
                }
            }

            FinanceAppTheme(
                darkTheme = isDarkMode,
                dynamicColor = false
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (!isAuthenticated && showPinDialog && isPinEnabled) {
                        PinEntryDialog(
                            onPinEntered = { enteredPin ->
                                if (settingsViewModel.validatePin(enteredPin)) {
                                    isAuthenticated = true
                                    showPinDialog = false
                                }
                            }
                        )
                    } else {
                        val navController = rememberNavController()

                        NavHost(
                            navController = navController,
                            startDestination = "welcome"
                        ) {
                            composable("welcome") {
                                WelcomeScreen(
                                    onContinueClicked = {
                                        navController.navigate("home") {
                                            popUpTo("welcome") { inclusive = true }
                                        }
                                    }
                                )
                            }
                            composable("home") {
                                HomeScreen(
                                    onNavigateToStatistics = { navController.navigate("statistics") },
                                    onNavigateToSettings = { navController.navigate("settings") }
                                )
                            }
                            composable("statistics") {
                                StatisticsScreen(
                                    onNavigateBack = { navController.navigateUp() }
                                )
                            }
                            composable("settings") {
                                SettingsScreen(
                                    onNavigateBack = { navController.navigateUp() },
                                    onNavigateToAbout = { navController.navigate("about") },
                                    onRequestSmsPermission = {
                                        requestPermissionLauncher.launch(Manifest.permission.READ_SMS)
                                    }
                                )
                            }
                            composable("about") {
                                AboutScreen(
                                    onNavigateBack = { navController.navigateUp() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PinEntryDialog(
    onPinEntered: (String) -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = {},
        title = { Text("Введите PIN-код") },
        text = {
            Column {
                OutlinedTextField(
                    value = pin,
                    onValueChange = {
                        if (it.length <= 4 && it.all(Char::isDigit)) {
                            pin = it
                            error = ""
                        }
                    },
                    label = { Text("PIN-код") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    isError = error.isNotEmpty(),
                    supportingText = {
                        if (error.isNotEmpty()) Text(error, color = MaterialTheme.colorScheme.error)
                    }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (pin.length == 4) {
                    onPinEntered(pin)
                    pin = ""
                } else {
                    error = "Введите 4 цифры"
                }
            }) {
                Text("Войти")
            }
        }
    )
}
