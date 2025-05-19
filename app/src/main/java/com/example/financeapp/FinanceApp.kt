package com.example.financeapp

import android.app.Application
import android.os.StrictMode
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel

@HiltAndroidApp
class FinanceApp : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        
        // Настройка StrictMode
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyLog()
                    .build()
            )
        }

        // Инициализация в фоновом потоке
        applicationScope.launch {
            // Здесь можно разместить тяжелые операции инициализации
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        applicationScope.cancel()
    }
} 