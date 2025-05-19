package com.example.financeapp.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    private val _isPinEnabled = MutableStateFlow(false)
    val isPinEnabled: StateFlow<Boolean> = _isPinEnabled

    private val _pin = MutableStateFlow("")
    val pin: StateFlow<String> = _pin

    init {
        loadDarkModeState()
        loadPinSettings()
    }

    private fun loadPinSettings() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
                val savedPin = prefs.getString("pin", "") ?: ""
                val pinEnabled = prefs.getBoolean("pin_enabled", false)
                _pin.value = savedPin
                _isPinEnabled.value = pinEnabled
            }
        }
    }

    private fun loadDarkModeState() {
        viewModelScope.launch {
            _isDarkMode.value = withContext(Dispatchers.IO) {
                context.getSharedPreferences("settings", Context.MODE_PRIVATE)
                    .getBoolean("dark_mode", false)
            }
        }
    }

    fun toggleDarkMode() {
        viewModelScope.launch {
            val newValue = !_isDarkMode.value
            withContext(Dispatchers.IO) {
                context.getSharedPreferences("settings", Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean("dark_mode", newValue)
                    .apply()
            }
            _isDarkMode.value = newValue
        }
    }

    fun setPinEnabled(enabled: Boolean) {
        viewModelScope.launch {
            _isPinEnabled.value = enabled
            withContext(Dispatchers.IO) {
                context.getSharedPreferences("settings", Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean("pin_enabled", enabled)
                    .apply()
            }
        }
    }

    fun setPin(pin: String): Boolean {
        if (pin.length == 4) {
            viewModelScope.launch {
                _pin.value = pin
                _isPinEnabled.value = true
                withContext(Dispatchers.IO) {
                    context.getSharedPreferences("settings", Context.MODE_PRIVATE)
                        .edit()
                        .putString("pin", pin)
                        .putBoolean("pin_enabled", true)
                        .apply()
                }
            }
            return true
        }
        return false
    }

    fun validatePin(pin: String): Boolean {
        return pin == _pin.value
    }

    fun isPinSet(): Boolean {
        return _pin.value.isNotEmpty()
    }
} 