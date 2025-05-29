package com.example.stepforward.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.stepforward.R
import com.example.stepforward.data.model.LoggedInUser
import com.example.stepforward.data.model.Role
import com.example.stepforward.data.model.SchedulePattern
import com.example.stepforward.data.model.Teacher
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.lang.reflect.Type
import java.net.URL
import java.util.Calendar
import java.util.Date
import java.util.UUID

class UserFileDataSource(private val context: Context) {
    private val gson = Gson()
    private val type: Type = object : TypeToken<List<LoggedInUser>>() {}.type
    private val fileName = "users.json"

    fun loadUsers(): List<LoggedInUser> {
        return try {
            val file = File(context.filesDir, fileName)
            if (file.exists()) {
                val reader = FileReader(file)
                gson.fromJson(reader, type)
            } else {
                // Создаем файл, если его нет
                createDefaultUsersFile()
                loadUsers()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun resetData() {
        try {
            // Удаляем существующий файл
            val file = File(context.filesDir, fileName)
            if (file.exists() && file.delete()) {
                Log.d("UserFileDataSource", "Existing data file deleted")
            }
            // Создаем новый файл с дефолтными данными
            createDefaultUsersFile()
            Log.d("UserFileDataSource", "Default data file created")
        } catch (e: IOException) {
            Log.e("UserFileDataSource", "Error resetting data: ${e.message}")
            throw IOException("Error resetting data", e)
        }
    }

    fun saveUsers(users: List<LoggedInUser>) {
        try {
            val file = File(context.filesDir, fileName)
            val writer = FileWriter(file)
            gson.toJson(users, writer)
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun createUser(user: LoggedInUser) {
        var users = loadUsers()
        users = users.filter { it.abonement != user.abonement }

        // Генерируем расписание при создании пользователя
        val userWithSchedule = if (user.daySession.isEmpty()) {
            user.copy(daySession = ScheduleGenerator.generateSchedule(user.schedulePattern))
        } else {
            user
        }

        users += userWithSchedule
        saveUsers(users)
    }

    fun updateUser(user: LoggedInUser): Boolean {
        val users = loadUsers().toMutableList()
        val userIndex = users.indexOfFirst { it.userId == user.userId }
        if (userIndex != -1) {
            users[userIndex] = user
            saveUsers(users)
            return true
        }
        return false
    }

    // Старый метод можно удалить или оставить для совместимости
    fun updateUserSchedule(abonement: String, newSchedule: List<Date>): Boolean {
        val users = loadUsers().toMutableList()
        val userIndex = users.indexOfFirst { it.abonement == abonement }
        if (userIndex != -1) {
            val updatedUser = users[userIndex].copy(daySession = newSchedule)
            users[userIndex] = updatedUser
            saveUsers(users)
            return true
        }
        return false
    }

    fun getUser(abonement: String, password: String): LoggedInUser? {
        return loadUsers().firstOrNull {
            it.abonement.replace(" ", "") == abonement.replace(" ", "") && it.pass == password
        }
    }

    fun loadUserImage(context: Context, imagePath: String): Bitmap? {
        return try {
            if (imagePath.startsWith("http")) {
                val url = URL(imagePath)
                val connection = url.openConnection()
                connection.connect()
                val inputStream = connection.inputStream
                BitmapFactory.decodeStream(inputStream)
            } else {
                BitmapFactory.decodeFile(imagePath)
            }
        } catch (e: Exception) {
            Log.e("UserFileDataSource", "Error loading image: ${e.message}")
            null
        }
    }

    private fun createDefaultUsersFile() {
        val admin = LoggedInUser(
            userId = UUID.randomUUID().toString(),
            abonement = "2325532151001201",
            pass = "123",
            displayName = "Admin",
            displaySecondName = "",
            displaySurName = "",
            dateBirthday = "",
            imagePath = R.drawable.teacher1.toString(),
            daySession = emptyList(),
            teacher = Teacher(
                idTeacher = 0,
                imageRes = 0,
                name = "",
                direction = emptyList(),
                achievements = emptyList(),
                master_class = emptyList()
            ),
            role = Role.ADMIN,
            schedulePattern = SchedulePattern(
                daysOfWeek = listOf(Calendar.MONDAY, Calendar.WEDNESDAY, Calendar.FRIDAY),
                timeOfDay = "18:00",
                durationWeeks = 4
            )
        )

        val user = LoggedInUser(
            userId = UUID.randomUUID().toString(),
            abonement = "2325532151001200",
            pass = "123",
            displayName = "Виктория",
            displaySecondName = "Кац",
            displaySurName = "Олеговна",
            dateBirthday = "18.10.2012",
            imagePath = R.drawable.logo_img.toString(),
            teacher = Teacher(
                idTeacher = 1,
                imageRes = R.drawable.teacher1,
                name = "Оля",
                direction = listOf("Современные танцы", "Хип-Хоп"),
                achievements = listOf("Победитель конкурса танцев", "Сертифицированный тренер"),
                master_class = listOf("Мастер-класс по хип-хопу", "Современные танцы для начинающих")
            ),
            role = Role.USER,
            schedulePattern = SchedulePattern(
                daysOfWeek = listOf(Calendar.MONDAY, Calendar.WEDNESDAY, Calendar.FRIDAY),
                timeOfDay = "18:00",
                durationWeeks = 4
            )
        )

        saveUsers(listOf(admin, user))
    }
}