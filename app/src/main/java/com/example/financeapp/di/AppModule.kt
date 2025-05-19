package com.example.financeapp.di

import android.content.Context
import com.example.financeapp.data.db.TransactionDao
import com.example.financeapp.data.repository.TransactionRepository
import com.example.financeapp.data.service.SmsService
import com.example.financeapp.util.SmsParser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideTransactionRepository(
        transactionDao: TransactionDao,
        smsService: SmsService
    ): TransactionRepository {
        return TransactionRepository(transactionDao, smsService)
    }

    @Provides
    @Singleton
    fun provideSmsParser(
        @ApplicationContext context: Context
    ): SmsParser {
        return SmsParser(context)
    }

    @Provides
    @Singleton
    fun provideSmsService(
        @ApplicationContext context: Context,
        smsParser: SmsParser
    ): SmsService {
        return SmsService(context, smsParser)
    }
}
