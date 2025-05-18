package com.example.financeapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.model.Transaction
import com.example.financeapp.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.Date

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getAllTransactions()
                    .catch { e -> 
                        // Обработка ошибок
                        _isLoading.value = false
                    }
                    .collect { transactions ->
                        _transactions.value = transactions
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                // Обработка ошибок
                _isLoading.value = false
            }
        }
    }

    fun onTransactionClick(transaction: Transaction) {
        viewModelScope.launch {
            // TODO: Реализовать обработку клика по транзакции
        }
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                repository.insertTransaction(transaction)
            } catch (e: Exception) {
                // Обработка ошибок
            }
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                repository.updateTransaction(transaction)
            } catch (e: Exception) {
                // Обработка ошибок
            }
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                repository.deleteTransaction(transaction)
            } catch (e: Exception) {
                // Обработка ошибок
            }
        }
    }
} 