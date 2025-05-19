package com.example.financeapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.financeapp.data.model.Category
import com.example.financeapp.data.model.Transaction
import com.example.financeapp.data.model.TransactionType
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDialog(
    transaction: Transaction? = null,
    onDismiss: () -> Unit,
    onConfirm: (Transaction) -> Unit
) {
    var amount by remember { mutableStateOf(transaction?.amount?.toString() ?: "") }
    var description by remember { mutableStateOf(transaction?.description ?: "") }
    var type by remember { mutableStateOf(transaction?.type ?: TransactionType.EXPENSE) }
    var category by remember { mutableStateOf(transaction?.category ?: Category.OTHER) }
    var showCategoryDropdown by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(if (transaction == null) "Новая транзакция" else "Редактировать транзакцию") 
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Тип транзакции
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FilterChip(
                        selected = type == TransactionType.EXPENSE,
                        onClick = { type = TransactionType.EXPENSE },
                        label = { Text("Расход") }
                    )
                    FilterChip(
                        selected = type == TransactionType.INCOME,
                        onClick = { type = TransactionType.INCOME },
                        label = { Text("Доход") }
                    )
                }

                // Сумма
                OutlinedTextField(
                    value = amount,
                    onValueChange = { 
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*\$"))) {
                            amount = it
                        }
                    },
                    label = { Text("Сумма") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Категория
                ExposedDropdownMenuBox(
                    expanded = showCategoryDropdown,
                    onExpandedChange = { showCategoryDropdown = it }
                ) {
                    OutlinedTextField(
                        value = category.title,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Категория") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCategoryDropdown) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = showCategoryDropdown,
                        onDismissRequest = { showCategoryDropdown = false }
                    ) {
                        Category.values().forEach { categoryItem ->
                            DropdownMenuItem(
                                text = { Text(categoryItem.title) },
                                onClick = {
                                    category = categoryItem
                                    showCategoryDropdown = false
                                }
                            )
                        }
                    }
                }

                // Описание
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amountValue = amount.toDoubleOrNull()
                    if (amountValue != null && description.isNotBlank()) {
                        onConfirm(
                            Transaction(
                                id = transaction?.id ?: 0,
                                amount = amountValue,
                                type = type,
                                category = category,
                                description = description,
                                date = transaction?.date ?: Date()
                            )
                        )
                    }
                },
                enabled = amount.isNotEmpty() && description.isNotBlank()
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
} 