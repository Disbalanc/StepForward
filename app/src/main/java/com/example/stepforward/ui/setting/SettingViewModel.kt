package com.example.stepforward.ui.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {
    private val _notificationsEnabled = MutableLiveData<Boolean>().apply { value = true }
    val notificationsEnabled: LiveData<Boolean> = _notificationsEnabled

    private val _language = MutableLiveData<String>().apply { value = "ru" }
    val language: LiveData<String> = _language

    fun setNotificationsEnabled(enabled: Boolean) {
        _notificationsEnabled.value = enabled
    }

    fun setLanguage(lang: String) {
        _language.value = lang
    }
}