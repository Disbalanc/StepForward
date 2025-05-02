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
                userId = UUID.randomUUID().toString(),
                abonement = user.abonement.chunked(4).joinToString(" "),
                pass = user.pass,
                displayName = user.displayName,
                displaySecondName = user.displaySecondName,
                displaySurName = user.displaySurName,
                dateBirthday = user.dateBirthday,
                imagePath = user.imagePath,
                daySession = listOf(
                    java.util.Date(System.currentTimeMillis() - 2 * 86400000),
                    java.util.Date(System.currentTimeMillis() - 86400000),
                    java.util.Date(),
                    java.util.Date(System.currentTimeMillis() + 86400000)
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

    // Функция для сохранения изображения в файл
    private fun saveImageToFile(bitmap: Bitmap): String {
        val file = File(context.cacheDir, "user_image.png") // Путь к файлу
        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        }
        return file.absolutePath // Возвращаем путь к файлу
    }
}

// Extension функция для преобразования Bitmap в Base64
fun Bitmap.toBase64(): String {
    return ByteArrayOutputStream().use { outputStream ->
        this.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }
}