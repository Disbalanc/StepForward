package com.example.stepforward.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stepforward.R
import com.example.stepforward.data.ScheduleGenerator
import com.example.stepforward.data.model.LoggedInUser
import com.example.stepforward.data.model.Role
import com.example.stepforward.data.model.SchedulePattern
import com.example.stepforward.data.model.Teacher
import java.util.UUID

class UserViewModel : ViewModel() {
    private val _user = MutableLiveData<LoggedInUser?>()
    val user: LiveData<LoggedInUser?> = _user

    fun updateUser(updatedUser: LoggedInUser) {
        Log.d("UserViewModel", "Updating user: $updatedUser")
        _user.value = updatedUser
    }

    fun clearUserData() {
        Log.d("UserViewModel", "Clearing user data")
        _user.value = null
    }

    fun getTeacher(): Teacher? {
        return _user.value?.teacher
    }

    fun updateUserSchedule(daysOfWeek: List<Int>, timeOfDay: String, durationWeeks: Int) {
        _user.value?.let { currentUser ->
            val newPattern = SchedulePattern(daysOfWeek, timeOfDay, durationWeeks)
            val newSchedule = ScheduleGenerator.generateSchedule(newPattern)

            val updatedUser = currentUser.copy(
                schedulePattern = newPattern,
                daySession = newSchedule
            )

            _user.value = updatedUser
        }
    }
}