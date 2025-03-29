package com.example.stepforward.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stepforward.data.model.LoggedInUser

class UserViewModel : ViewModel() {
    private val _user = MutableLiveData<LoggedInUser?>()
    val user: LiveData<LoggedInUser?> = _user

    fun updateUser (newUser:  LoggedInUser ) {
        _user.value = newUser
        Log.d("User ViewModel", "User  updated: $newUser ")
    }
}