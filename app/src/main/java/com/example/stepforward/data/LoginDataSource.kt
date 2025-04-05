package com.example.stepforward.data


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.example.stepforward.R
import com.example.stepforward.data.model.LoggedInUser
import com.example.stepforward.data.model.Teacher
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Date
import java.util.UUID

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource(private val context: Context) { // Добавляем контекст в конструктор

    fun login(username: String, password: String): Result<LoggedInUser > {
        return try {
            // Тестовые данные аккаунта
            val testAbonement = "2325532151001200"
            val testPassword = "123"

            // Проверка введенных данных
            if (username.replace(" ", "") != testAbonement || password != testPassword) {
                return Result.Error(IOException("Invalid credentials"))
            }

            // Загрузка изображения...
            val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.logo_img)
                ?: return Result.Error(IOException("Failed to load image"))

            // Сохранение изображения в файл
            val imagePath = saveImageToFile(bitmap)

            // Создание пользователя
            val fakeUser  = LoggedInUser (
                userId = UUID.randomUUID().toString(),
                abonement = testAbonement.chunked(4).joinToString(" "),
                pass = testPassword,
                displayName = "Виктория",
                displaySecondName = "Кац",
                displaySurName = "Олеговна",
                dateBirthday = "18.10.2012",
                imagePath = imagePath,
                daySession = listOf(
                    // Примеры дат с разным временем
                    Date(System.currentTimeMillis() - 2 * 86400000), // 2 дня назад
                    Date(System.currentTimeMillis() - 86400000),     // вчера
                    Date(),                                          // сейчас
                    Date(System.currentTimeMillis() + 86400000)      // завтра
                ).sortedDescending(),
                teacher = Teacher(
                    idTeacher = 1,
                    imageRes = R.drawable.teacher1,
                    name = "Оличка",
                    direction = listOf("Современные танцы", "Хип-Хоп"),
                    achievements = listOf("Победитель конкурса танцев", "Сертифицированный тренер"),
                    master_class = listOf("Мастер-класс по хип-хопу", "Современные танцы для начинающих")
                )
            )

            Result.Success(fakeUser )
        } catch (e: Throwable) {
            Result.Error(IOException("Error logging in", e))
        }
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