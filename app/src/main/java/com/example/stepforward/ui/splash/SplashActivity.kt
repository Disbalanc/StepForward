package com.example.stepforward.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.stepforward.ui.login.LoginActivity
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CoroutineScope(Dispatchers.Main).launch {
            checkAuth()
            delay(2000) // Лучше использовать delay вместо Thread.sleep
            navigateToLogin()
        }
    }

    private suspend fun checkAuth() {
        try {
            val currentUser = SupabaseManager.client.auth.currentUserOrNull()
            if (currentUser != null) {
                // Пользователь авторизован - переходим на главный экран
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            }
        } catch (e: Exception) {
            Log.e("SplashActivity", "Auth check error", e)
        }
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}