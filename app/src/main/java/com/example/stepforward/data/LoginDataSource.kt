package com.example.stepforward.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.example.stepforward.R
import com.example.stepforward.data.model.LoggedInUser
import com.example.stepforward.data.model.Role
import com.example.stepforward.data.model.Teacher
import com.example.stepforward.ui.login.UserViewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Calendar
import java.util.Date
import java.util.UUID

class LoginDataSource(private val context: Context) {
    private val userFileDataSource = UserFileDataSource(context)

    fun login(username: String, password: String): Result<LoggedInUser> {
        return try {
            //userFileDataSource.resetData()
            val user = userFileDataSource.getUser(username, password)
            if (user == null) {
                return Result.Error(IOException("Invalid credentials"))
            }

            // Проверяем, есть ли сохраненное расписание
            val sessions = if (user.daySession.isEmpty()) {
                // Если нет - генерируем новое и сохраняем
                val newSessions = ScheduleGenerator.generateSchedule(user.schedulePattern)
                userFileDataSource.updateUserSchedule(user.abonement, newSessions)
                newSessions
            } else {
                // Используем сохраненное расписание
                user.daySession
            }

            // Загрузка изображения из файла
            val bitmap = userFileDataSource.loadUserImage(context, user.imagePath)
            val fakeUser = user.copy(
                userId = user.userId,
                abonement = user.abonement.chunked(4).joinToString(" "),
                pass = user.pass,
                displayName = user.displayName,
                displaySecondName = user.displaySecondName,
                displaySurName = user.displaySurName,
                dateBirthday = user.dateBirthday,
                imagePath = user.imagePath,
                teacher = user.teacher,
                daySession = sessions,
                role = user.role,
                schedulePattern = user.schedulePattern
            )

            // Сохраните изображение в кеш, если нужно
            if (bitmap != null) {
                val cachedPath = saveImageToFile(context, bitmap)
                fakeUser.imagePath = cachedPath
            }

            Result.Success(fakeUser)
        } catch (e: Throwable) {
            Result.Error(IOException("Error logging in", e))
        }
    }

    fun saveImageToFile(context: Context, bitmap: Bitmap): String {
        val file = File(context.cacheDir, "user_image_${UUID.randomUUID()}.png")
        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        }
        return file.absolutePath
    }

    fun registerUser(user: LoggedInUser) {
        userFileDataSource.createUser(user)
    }

    private fun saveImageToFile(bitmap: Bitmap): String {
        val file = File(context.cacheDir, "user_image.png")
        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        }
        return file.absolutePath
    }
}

fun Bitmap.toBase64(): String {
    return ByteArrayOutputStream().use { outputStream ->
        this.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }
}