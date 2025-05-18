package com.example.financeapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.model.Transaction
import com.example.financeapp.data.model.TransactionType
import com.example.financeapp.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    val allTransactions = repository.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val incomeTransactions = repository.getTransactionsByType(TransactionType.INCOME)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val expenseTransactions = repository.getTransactionsByType(TransactionType.EXPENSE)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val totalIncome = repository.getTotalByType(TransactionType.INCOME)
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    val totalExpense = repository.getTotalByType(TransactionType.EXPENSE)
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    val balance = combine(totalIncome, totalExpense) { income, expense ->
        (income ?: 0.0) - (expense ?: 0.0)
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    fun getTransactionsByDateRange(startDate: Date, endDate: Date) =
        repository.getTransactionsByDateRange(startDate, endDate)

    fun getCategoryTotals(type: TransactionType) =
        repository.getCategoryTotals(type)

    fun addTransaction(
        amount: Double,
        type: TransactionType,
        category: String,
        description: String,
        date: Date = Date(),
        isRecurring: Boolean = false
    ) {
        viewModelScope.launch {
            val transaction = Transaction(
                amount = amount,
                type = type,
                category = category,
                description = description,
                date = date,
                isRecurring = isRecurring
            )
            repository.insertTransaction(transaction)
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.updateTransaction(transaction)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>> =
        repository.getTransactionsByType(type)
} 