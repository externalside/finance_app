package com.example.financeapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val type: TransactionType,
    val category: Category,
    val description: String,
    val date: Date,
    val source: String = "MANUAL" // MANUAL или SMS
)

enum class TransactionType {
    INCOME,
    EXPENSE
} 