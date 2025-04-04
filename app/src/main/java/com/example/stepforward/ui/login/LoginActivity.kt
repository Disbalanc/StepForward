package com.example.stepforward.ui.login

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import com.example.stepforward.MainActivity
import com.example.stepforward.databinding.ActivityLoginBinding
import com.example.stepforward.data.Result
import com.example.stepforward.R
import com.example.stepforward.data.model.LoggedInUser
import java.io.ByteArrayOutputStream

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Инициализация ViewModel
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory(this)).get(LoginViewModel::class.java)

        val subscriptionNumber = binding.subscriptionNumber
        val name = binding.name
        val login = binding.login
        //login.setBackgroundColor(Color.parseColor("#CD0F6D"))

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory(this)) // Передаем контекст
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                subscriptionNumber?.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                name?.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer { loginResult ->
            val result = loginResult ?: return@Observer

            if (result.error != null) {
                showLoginFailed(result.error)
            }
            // LoginActivity.kt
            if (result.success != null) {
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("USER_DATA", result.success)
                }
                startActivity(intent)
                finish()
            }
        })

        subscriptionNumber?.afterTextChanged {
            loginViewModel.loginDataChanged(
                subscriptionNumber.text.toString(),
                name?.text.toString()
            )
        }

        name?.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    subscriptionNumber?.text.toString(),
                    name.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            subscriptionNumber?.text.toString(),
                            name.text.toString()
                        )
                }
                false
            }
        }
        login.isEnabled = true
        login.setOnClickListener {
            val abonement = subscriptionNumber?.text.toString()
            val password = name?.text.toString()

            // Вызываем метод login в ViewModel
            loginViewModel.login(abonement, password)
        }
    }

    fun fillUserData(user: LoggedInUser ) {
        // Здесь можно добавить логику для заполнения данных, если нужно
        // Например, можно использовать переданные данные для создания объекта LoggedInUser
        userViewModel.updateUser (user) // Обновляем UserViewModel
    }

    private fun updateUiWithUser(model: LoggedInUser) {
        val welcome = getString(R.string.Welcome)
        val displayName = model.displayName
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}