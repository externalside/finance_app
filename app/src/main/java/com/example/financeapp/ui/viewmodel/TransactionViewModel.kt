package com.example.financeapp.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.model.CategoryTotal
import com.example.financeapp.data.model.Transaction
import com.example.financeapp.data.model.TransactionType
import com.example.financeapp.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val repository: TransactionRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _smsImportState = MutableStateFlow<SmsImportState>(SmsImportState.Initial)
    val smsImportState: StateFlow<SmsImportState> = _smsImportState.asStateFlow()

    private val _totalIncome = MutableStateFlow<Double?>(null)
    val totalIncome: StateFlow<Double?> = _totalIncome.asStateFlow()

    private val _totalExpense = MutableStateFlow<Double?>(null)
    val totalExpense: StateFlow<Double?> = _totalExpense.asStateFlow()

    val allTransactions = repository.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        viewModelScope.launch {
            repository.getTotalByType(TransactionType.INCOME).collect { income ->
                _totalIncome.value = income
            }
        }

        viewModelScope.launch {
            repository.getTotalByType(TransactionType.EXPENSE).collect { expense ->
                _totalExpense.value = expense
            }
        }
    }

    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>> =
        repository.getTransactionsByType(type)

    fun getTransactionsByDateRange(startDate: Date, endDate: Date): Flow<List<Transaction>> =
        repository.getTransactionsByDateRange(startDate, endDate)

    fun getCategoryTotals(type: TransactionType): Flow<List<CategoryTotal>> =
        repository.getCategoryTotals(type)

    fun insertTransaction(transaction: Transaction) {
        viewModelScope.launch {
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

    fun hasSmsPermissions(): Boolean {
        return repository.hasSmsPermissions()
    }

    fun importSmsTransactions() {
        viewModelScope.launch {
            _smsImportState.value = SmsImportState.Loading
            try {
                repository.importSmsTransactions()
                _smsImportState.value = SmsImportState.Success
            } catch (e: Exception) {
                _smsImportState.value = SmsImportState.Error(e.message ?: "Неизвестная ошибка")
            }
        }
    }
}

sealed class SmsImportState {
    object Initial : SmsImportState()
    object Loading : SmsImportState()
    object Success : SmsImportState()
    data class Error(val message: String) : SmsImportState()
} 