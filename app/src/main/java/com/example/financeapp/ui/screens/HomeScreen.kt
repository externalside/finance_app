package com.example.financeapp.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.financeapp.ui.components.TransactionDialog
import com.example.financeapp.ui.components.TransactionItem
import com.example.financeapp.ui.viewmodel.TransactionViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onNavigateToStatistics: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: TransactionViewModel = hiltViewModel()
) {
    val transactions by viewModel.allTransactions.collectAsState(initial = emptyList())
    val totalIncome by viewModel.totalIncome.collectAsState(initial = null)
    val totalExpense by viewModel.totalExpense.collectAsState(initial = null)
    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Множество выбранных транзакций по id
    var selectedTransactionIds by remember { mutableStateOf(setOf<Long>()) }

    val numberFormat = remember { NumberFormat.getCurrencyInstance(Locale("ru", "RU")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Финансы") },
                actions = {
                    IconButton(onClick = onNavigateToStatistics) {
                        Icon(Icons.Default.BarChart, contentDescription = "Статистика")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Настройки")
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedTransactionIds.isEmpty()) {
                FloatingActionButton(
                    onClick = { showAddDialog = true }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Добавить")
                }
            } else {
                // Кнопка удаления при выделенных элементах
                FloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.error,
                    onClick = { showDeleteDialog = true }
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Удалить выделенные")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Карточка с балансом
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Баланс",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = numberFormat.format((totalIncome ?: 0.0) - (totalExpense ?: 0.0)),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Доходы",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = numberFormat.format(totalIncome ?: 0.0),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Расходы",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = numberFormat.format(totalExpense ?: 0.0),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            // Список транзакций с поддержкой множественного выбора
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = transactions,
                    key = { it.id }
                ) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        isSelected = selectedTransactionIds.contains(transaction.id),
                        onItemClick = {
                            if (selectedTransactionIds.isNotEmpty()) {
                                // Если режим множественного выбора активен — переключаем выделение
                                selectedTransactionIds = if (selectedTransactionIds.contains(transaction.id)) {
                                    selectedTransactionIds - transaction.id
                                } else {
                                    selectedTransactionIds + transaction.id
                                }
                            } else {
                                // Пока ничего не делаем при клике, если не выбраны другие элементы
                            }
                        },
                        onLongClick = {
                            // При долгом нажатии начинаем режим множественного выбора, выделяем элемент
                            selectedTransactionIds = selectedTransactionIds + transaction.id
                        }
                    )
                }
            }
        }

        // Диалог добавления транзакции
        if (showAddDialog) {
            TransactionDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { transaction ->
                    viewModel.insertTransaction(transaction)
                    showAddDialog = false
                }
            )
        }

        // Диалог удаления выделенных транзакций
        if (showDeleteDialog && selectedTransactionIds.isNotEmpty()) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                },
                title = { Text("Удалить транзакции?") },
                text = { Text("Вы уверены, что хотите удалить выделенные транзакции?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            // Удаляем все выбранные транзакции
                            selectedTransactionIds.forEach { id ->
                                transactions.find { it.id == id }?.let { viewModel.deleteTransaction(it) }
                            }
                            selectedTransactionIds = emptySet()
                            showDeleteDialog = false
                        }
                    ) {
                        Text("Удалить")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                        }
                    ) {
                        Text("Отмена")
                    }
                }
            )
        }
    }
}

enum class TransactionType {
    INCOME, EXPENSE
} 