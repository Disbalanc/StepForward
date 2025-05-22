package com.example.stepforward.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.stepforward.R
import com.example.stepforward.data.model.LoggedInUser
import com.example.stepforward.data.model.Role
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
        users += user
        saveUsers(users)
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
            pass = "admin123",
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
            role = Role.ADMIN
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
            daySession = listOf(
                // Прошлый понедельник
                Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_WEEK, -7)
                    set(Calendar.HOUR_OF_DAY, 18)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                }.time,

                // Прошедшая среда
                Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_WEEK, -5)
                    set(Calendar.HOUR_OF_DAY, 18)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                }.time,

                // Прошедшая пятница
                Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_WEEK, -3)
                    set(Calendar.HOUR_OF_DAY, 18)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                }.time,

                // Текущий понедельник
                Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 18)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    if (get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                        add(Calendar.DAY_OF_WEEK, - (get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY))
                    }
                }.time,

                // Текущая среда
                Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 18)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    if (get(Calendar.DAY_OF_WEEK) != Calendar.WEDNESDAY) {
                        add(Calendar.DAY_OF_WEEK, - (get(Calendar.DAY_OF_WEEK) - Calendar.WEDNESDAY))
                    }
                }.time,

                // Текущая пятница
                Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 18)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    if (get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY) {
                        add(Calendar.DAY_OF_WEEK, - (get(Calendar.DAY_OF_WEEK) - Calendar.FRIDAY))
                    }
                }.time,

                // Следующая среда
                Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 18)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    if (get(Calendar.DAY_OF_WEEK) != Calendar.WEDNESDAY) {
                        add(Calendar.DAY_OF_WEEK, 7 - (get(Calendar.DAY_OF_WEEK) - Calendar.WEDNESDAY))
                    }
                }.time,

                // Следующая пятница
                Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 18)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    if (get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY) {
                        add(Calendar.DAY_OF_WEEK, 7 - (get(Calendar.DAY_OF_WEEK) - Calendar.FRIDAY))
                    }
                }.time
            ).sortedDescending(),
            teacher = Teacher(
                idTeacher = 1,
                imageRes = R.drawable.teacher1,
                name = "Оля",
                direction = listOf("Современные танцы", "Хип-Хоп"),
                achievements = listOf("Победитель конкурса танцев", "Сертифицированный тренер"),
                master_class = listOf("Мастер-класс по хип-хопу", "Современные танцы для начинающих")
            ),
            role = Role.USER
        )

        saveUsers(listOf(admin, user))
    }
}