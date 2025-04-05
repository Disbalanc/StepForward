package com.example.stepforward.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stepforward.data.model.LoggedInUser
import com.example.stepforward.data.model.Teacher

class UserViewModel : ViewModel() {
    // 1. Изменяем тип на nullable
    private val _user = MutableLiveData<LoggedInUser?>()
    val user: LiveData<LoggedInUser?> = _user

    fun updateUser(updatedUser: LoggedInUser) {
        Log.d("UserViewModel", "Updating user: $updatedUser")
        _user.value = updatedUser
    }

    fun clearUserData() {
        Log.d("UserViewModel", "Clearing user data")
        _user.value = null // Теперь это допустимо
    }

    fun getTeacher(): Teacher? {
        return _user.value?.teacher
    }
}