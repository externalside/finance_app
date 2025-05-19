package com.example.financeapp.util

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.Telephony
import com.example.financeapp.data.model.Transaction
import com.example.financeapp.data.model.TransactionType
import com.example.financeapp.data.model.Category
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject

class SmsParser @Inject constructor(
    private val context: Context
) {
    companion object {
        private val BANK_NUMBERS = listOf(
            "900", // Сбербанк
            "2265", // ВТБ
            "3434", // Альфа-Банк
            "7733"  // Тинькофф
        )

        private val EXPENSE_PATTERNS = listOf(
            Pattern.compile("списание|покупка|оплата|перевод", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(\\d+[.,]\\d{2})\\s*(?:RUB|USD|EUR|₽|\\$|€)", Pattern.CASE_INSENSITIVE)
        )

        private val INCOME_PATTERNS = listOf(
            Pattern.compile("зачисление|поступление|пополнение", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(\\d+[.,]\\d{2})\\s*(?:RUB|USD|EUR|₽|\\$|€)", Pattern.CASE_INSENSITIVE)
        )
    }

    fun parseSms(): List<Transaction> {
        val transactions = mutableListOf<Transaction>()
        val cursor = querySms() ?: return transactions

        cursor.use {
            if (cursor.moveToFirst()) {
                do {
                    val address = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS))
                    if (BANK_NUMBERS.contains(address)) {
                        val body = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY))
                        val date = Date(cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE)))

                        parseTransaction(body, date)?.let { transactions.add(it) }
                    }
                } while (cursor.moveToNext())
            }
        }

        return transactions
    }

    private fun querySms(): Cursor? {
        return context.contentResolver.query(
            Uri.parse("content://sms/inbox"),
            arrayOf(
                Telephony.Sms.ADDRESS,
                Telephony.Sms.BODY,
                Telephony.Sms.DATE
            ),
            null,
            null,
            "${Telephony.Sms.DATE} DESC"
        )
    }

    private fun parseTransaction(body: String, date: Date): Transaction? {
        // Расход
        if (EXPENSE_PATTERNS[0].matcher(body).find()) {
            val matcher = EXPENSE_PATTERNS[1].matcher(body)
            if (matcher.find() && matcher.groupCount() >= 1) {
                val amount = matcher.group(1)?.replace(",", ".")?.toDoubleOrNull() ?: return null
                return Transaction(
                    amount = amount,
                    category = parseSms(body),
                    description = body,
                    date = date,
                    type = TransactionType.EXPENSE
                )
            }
        }

        // Доход
        if (INCOME_PATTERNS[0].matcher(body).find()) {
            val matcher = INCOME_PATTERNS[1].matcher(body)
            if (matcher.find() && matcher.groupCount() >= 1) {
                val amount = matcher.group(1)?.replace(",", ".")?.toDoubleOrNull() ?: return null
                return Transaction(
                    amount = amount,
                    category = parseSms(body),
                    description = body,
                    date = date,
                    type = TransactionType.INCOME
                )
            }
        }

        return null
    }

    fun parseSms(body: String): Category {
        return when {
            body.contains(Regex("(продукты|еда|супермаркет)", RegexOption.IGNORE_CASE)) -> Category.FOOD
            body.contains(Regex("(транспорт|метро|такси|автобус)", RegexOption.IGNORE_CASE)) -> Category.TRANSPORT
            body.contains(Regex("(развлечения|кино|театр)", RegexOption.IGNORE_CASE)) -> Category.ENTERTAINMENT
            body.contains(Regex("(коммунальные|жкх|квартплата)", RegexOption.IGNORE_CASE)) -> Category.HOUSING
            else -> Category.OTHER
        }
    }
}