package com.example.financeapp.data.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.financeapp.data.model.Transaction
import com.example.financeapp.util.SmsParser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmsService @Inject constructor(
    private val context: Context,
    private val smsParser: SmsParser
) {
    fun hasRequiredPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun parseTransactions(): List<Transaction> {
        if (!hasRequiredPermissions()) {
            return emptyList()
        }
        return smsParser.parseSms()
    }
} 