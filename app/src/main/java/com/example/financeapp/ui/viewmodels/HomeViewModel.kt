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

    private val _selectedTransactions = MutableStateFlow<Set<Transaction>>(emptySet())
    val selectedTransactions: StateFlow<Set<Transaction>> = _selectedTransactions.asStateFlow()

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getAllTransactions()
                    .catch { _isLoading.value = false }
                    .collect { loaded ->
                        _transactions.value = loaded
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _isLoading.value = false
            }
        }
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                repository.insertTransaction(transaction)
                loadTransactions()
            } catch (_: Exception) {}
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                repository.updateTransaction(transaction)
                loadTransactions()
            } catch (_: Exception) {}
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                repository.deleteTransaction(transaction)
                _selectedTransactions.value = _selectedTransactions.value - transaction
                loadTransactions()
            } catch (_: Exception) {}
        }
    }

    fun toggleSelection(transaction: Transaction) {
        val current = _selectedTransactions.value.toMutableSet()
        if (current.contains(transaction)) {
            current.remove(transaction)
        } else {
            current.add(transaction)
        }
        _selectedTransactions.value = current
    }

    fun clearSelection() {
        _selectedTransactions.value = emptySet()
    }

    fun deleteSelectedTransactions() {
        viewModelScope.launch {
            try {
                val selected = _selectedTransactions.value
                selected.forEach { repository.deleteTransaction(it) }
                _selectedTransactions.value = emptySet()
                loadTransactions()
            } catch (_: Exception) {}
        }
    }
}
