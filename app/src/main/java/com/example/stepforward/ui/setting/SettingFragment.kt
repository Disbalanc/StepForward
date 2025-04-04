package com.example.stepforward.ui.setting

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import com.example.stepforward.R
import com.example.stepforward.databinding.FragmentSettingBinding
import java.util.Locale

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Инициализация SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        // Установка текущих значений
        setupCurrentSettings()

        // Обработка нажатий на текстовые элементы
        binding.notificationOnText.setOnClickListener {
            setNotification(true)
        }

        binding.notificationOffText.setOnClickListener {
            setNotification(false)
        }

        binding.languageRuText.setOnClickListener {
            setLanguage("ru")
        }

        binding.languageEnText.setOnClickListener {
            setLanguage("en")
        }

        return root
    }

    private fun setupCurrentSettings() {
        // Установка текущих значений уведомлений
        val notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true)
        if (notificationsEnabled) {
            binding.notificationOnText.setBackgroundResource(R.drawable.selected_background)
            binding.notificationOffText.setBackgroundResource(R.drawable.default_background)
        } else {
            binding.notificationOnText.setBackgroundResource(R.drawable.default_background)
            binding.notificationOffText.setBackgroundResource(R.drawable.selected_background)
        }

        // Установка текущего языка
        val language = sharedPreferences.getString("language", "ru") ?: "ru"
        Log.d("SettingFragment", "Current language: $language")
        if (language == "ru") {
            binding.languageRuText.setBackgroundResource(R.drawable.selected_background)
            binding.languageEnText.setBackgroundResource(R.drawable.default_background)
        } else {
            binding.languageRuText.setBackgroundResource(R.drawable.default_background)
            binding.languageEnText.setBackgroundResource(R.drawable.selected_background)
        }
    }

    private fun saveUserSettings() {
        val username: String = "123";
        val email: String = "123";

        editor.putString("username", username)
        editor.putString("email", email)
        editor.apply()

        // Вы можете добавить уведомление о том, что настройки сохранены
        Toast.makeText(requireContext(), "Настройки сохранены", Toast.LENGTH_SHORT).show()
    }

    private fun setNotification(enabled: Boolean) {
        editor.putBoolean("notifications_enabled", enabled)
        editor.apply()
        setupCurrentSettings() // Обновляем интерфейс
    }

    private fun setLanguage(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        requireContext().resources.updateConfiguration(config, requireContext().resources.displayMetrics)

        // Сохранение выбранного языка в SharedPreferences
        editor.putString("language", language)
        editor.apply()

        // Перезапуск активности для применения изменений
        requireActivity().recreate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}