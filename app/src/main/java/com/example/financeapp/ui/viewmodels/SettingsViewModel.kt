package com.example.financeapp.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    private val _isDarkMode = MutableStateFlow(sharedPreferences.getBoolean("dark_mode", false))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _isPinEnabled = MutableStateFlow(sharedPreferences.getBoolean("pin_enabled", false))
    val isPinEnabled: StateFlow<Boolean> = _isPinEnabled.asStateFlow()

    private val _pin = MutableStateFlow(sharedPreferences.getString("pin", "") ?: "")
    val pin: StateFlow<String> = _pin.asStateFlow()

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            _isDarkMode.value = enabled
            saveDarkModeState(enabled)
        }
    }

    fun saveDarkModeState(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("dark_mode", enabled).apply()
    }

    fun setPinEnabled(enabled: Boolean) {
        if (!enabled) {
            // Если PIN отключается, очищаем его
            viewModelScope.launch {
                _isPinEnabled.value = false
                _pin.value = ""
                sharedPreferences.edit()
                    .putBoolean("pin_enabled", false)
                    .putString("pin", "")
                    .apply()
            }
        }
        // Если PIN включается, не меняем состояние до успешного ввода PIN-кода
    }

    fun setPin(newPin: String): Boolean {
        if (newPin.length == 4) {
            viewModelScope.launch {
                _pin.value = newPin
                _isPinEnabled.value = true
                sharedPreferences.edit()
                    .putString("pin", newPin)
                    .putBoolean("pin_enabled", true)
                    .apply()
            }
            return true
        }
        return false
    }

    fun validatePin(enteredPin: String): Boolean {
        return enteredPin == _pin.value
    }

    fun isPinSet(): Boolean {
        return _pin.value.isNotEmpty()
    }
} 