package com.example.financeapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.financeapp.ui.viewmodels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    var showPinDialog by remember { mutableStateOf(false) }
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val isPinEnabled by viewModel.isPinEnabled.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Настройки темы
            ThemeSettingsCard(
                isDarkMode = isDarkMode,
                onThemeChange = { viewModel.setDarkMode(it) }
            )
            
            // Настройки безопасности
            SecuritySettingsCard(
                isPinEnabled = isPinEnabled,
                onPinSettingChange = { enabled ->
                    if (enabled) {
                        showPinDialog = true
                    } else {
                        viewModel.setPinEnabled(false)
                    }
                }
            )

            // Кнопка "О приложении"
            AboutButton(
                onClick = { navController.navigate("about") }
            )
        }
    }

    // Диалог установки PIN-кода
    if (showPinDialog) {
        PinDialog(
            onDismiss = { 
                showPinDialog = false
                // Возвращаем переключатель в исходное положение
                if (!viewModel.isPinSet()) {
                    viewModel.setPinEnabled(false)
                }
            },
            onPinSet = { newPin ->
                if (viewModel.setPin(newPin)) {
                    showPinDialog = false
                }
            }
        )
    }
}

@Composable
private fun ThemeSettingsCard(
    isDarkMode: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                    contentDescription = "Тема"
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Темная тема",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Switch(
                checked = isDarkMode,
                onCheckedChange = onThemeChange
            )
        }
    }
}

@Composable
private fun SecuritySettingsCard(
    isPinEnabled: Boolean,
    onPinSettingChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "PIN-код"
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "PIN-код",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Switch(
                checked = isPinEnabled,
                onCheckedChange = onPinSettingChange
            )
        }
    }
}

@Composable
private fun AboutButton(
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "О приложении",
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "О приложении",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Перейти",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun PinDialog(
    onDismiss: () -> Unit,
    onPinSet: (String) -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Установка PIN-кода") },
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
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword
                    ),
                    singleLine = true,
                    isError = error.isNotEmpty(),
                    supportingText = if (error.isNotEmpty()) {
                        { Text(error) }
                    } else {
                        { Text("Введите 4 цифры") }
                    }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (pin.length == 4) {
                        onPinSet(pin)
                    } else {
                        error = "PIN-код должен состоять из 4 цифр"
                    }
                }
            ) {
                Text("Установить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
} 