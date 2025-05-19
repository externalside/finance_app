package com.example.financeapp.data.repository

import com.example.financeapp.data.db.TransactionDao
import com.example.financeapp.data.model.CategoryTotal
import com.example.financeapp.data.model.Transaction
import com.example.financeapp.data.model.TransactionType
import com.example.financeapp.data.service.SmsService
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val smsService: SmsService
) {
    fun getAllTransactions(): Flow<List<Transaction>> = 
        transactionDao.getAllTransactions()

    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>> =
        transactionDao.getTransactionsByType(type)

    fun getTransactionsByDateRange(startDate: Date, endDate: Date): Flow<List<Transaction>> =
        transactionDao.getTransactionsByDateRange(startDate.time, endDate.time)

    fun getTotalByType(type: TransactionType): Flow<Double?> =
        transactionDao.getTotalByType(type)

    suspend fun insertTransaction(transaction: Transaction) =
        transactionDao.insertTransaction(transaction)

    suspend fun updateTransaction(transaction: Transaction) =
        transactionDao.updateTransaction(transaction)

    suspend fun deleteTransaction(transaction: Transaction) =
        transactionDao.deleteTransaction(transaction)

    suspend fun getTransactionById(id: Long): Transaction? =
        transactionDao.getTransactionById(id)

    fun getCategoryTotals(type: TransactionType): Flow<List<CategoryTotal>> =
        transactionDao.getCategoryTotals(type)

    fun hasSmsPermissions(): Boolean = smsService.hasRequiredPermissions()

    suspend fun importSmsTransactions() {
        val transactions = smsService.parseTransactions()
        transactions.forEach { transaction ->
            insertTransaction(transaction)
        }
    }
} 