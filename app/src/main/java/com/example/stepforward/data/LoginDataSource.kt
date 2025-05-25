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
                teacher = user.teacher,
                role = user.role
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