package com.example.stepforward

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.stepforward.databinding.ActivityMainBinding
import com.example.stepforward.ui.login.LoginActivity
import com.example.stepforward.data.model.LoggedInUser
import com.example.stepforward.data.model.Role
import com.example.stepforward.data.model.Teacher
import com.example.stepforward.ui.addUser.AddUserFragment
import com.example.stepforward.ui.calendar.CalendarFragment
import com.example.stepforward.ui.login.UserViewModel
import com.example.stepforward.ui.notifications.NotificationsFragment
import com.example.stepforward.ui.profile.ProfileFragment
import com.example.stepforward.ui.teacher.TeacherFragment
import com.google.android.material.navigation.NavigationView
import java.io.File
import android.net.Uri
import android.widget.TextView


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var profileImageView: ImageView
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val headerView = navView.getHeaderView(0)
        profileImageView = headerView.findViewById(R.id.profileLogo_imageView)

        val navController = findNavController(R.id.nav_host_fragment_content_main)

        // Извлекаем данные пользователя из Intent
        val user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("USER_DATA", LoggedInUser::class.java)
        } else {
            intent.getParcelableExtra("USER_DATA")
        }
        user?.let {
            userViewModel.updateUser(it)
        }

        // Наблюдаем за изменениями в UserViewModel
        userViewModel.user.observe(this) { user ->
            if (user != null) {
                Log.d("MainActivity", "Current user: $user")
            } else {
                Log.e("MainActivity", "No user data available")
            }
        }

        // Наблюдаем за изменениями в UserViewModel
        userViewModel.user.observe(this) { user ->
            if (user != null) {
                Log.d("MainActivity", "Current user: $user")
                // Обновляем меню в зависимости от роли
                updateNavigationMenu(user.role)
                updateProfileImage(user.imagePath)
            } else {
                Log.e("MainActivity", "No user data available")
            }
        }



        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_profile, R.id.nav_teacher, R.id.nav_notification, R.id.nav_setting, R.id.nav_calendar, R.id.nav_feedback, R.id.nav_add_user, R.id.group_bottom, R.id.nav_logout
            ), drawerLayout
        )

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_teacher -> {
                    val teacherFragment = TeacherFragment() // Убедитесь, что это правильный ID действия
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment_content_main, teacherFragment)
                        .addToBackStack(null)
                        .commit()
                    true
                }
                R.id.nav_setting -> {
                    // Переход на NotificationsFragment без передачи данных через Bundle
                    val notificationFragment = NotificationsFragment()

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment_content_main, notificationFragment)
                        .addToBackStack(null)
                        .commit()
                    true
                }
                R.id.nav_calendar -> {
                    /// Переход на CalendarFragment без передачи данных через Bundle
                    val calendarFragment = CalendarFragment()

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment_content_main, calendarFragment)
                        .addToBackStack(null)
                        .commit()
                    true
                }
                R.id.nav_logout -> {
                    // Показать диалог подтверждения
                    showLogoutConfirmationDialog()
                    true
                }
                R.id.nav_profile -> {
                    // Переход на ProfileFragment без передачи данных через Bundle
                    val profileFragment = ProfileFragment()

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment_content_main, profileFragment)
                        .addToBackStack(null)
                        .commit()
                    true
                }
                R.id.nav_add_user -> {
                    if (userViewModel.user.value?.role == Role.ADMIN) {
                        val addUserFragment = AddUserFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.nav_host_fragment_content_main, addUserFragment)
                            .addToBackStack(null)
                            .commit()
                        true
                    } else {
                        false
                    }
                }
                else -> {
                    // Стандартная обработка через NavController
                    menuItem.isChecked = true
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    navController.navigate(menuItem.itemId)
                    true
                }
            }
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun updateNavigationMenu(role: Role) {
        val navView = binding.navView
        val menu = navView.menu
        // Показываем или скрываем пункт меню для добавления пользователей
        menu.findItem(R.id.nav_add_user).isVisible = role == Role.ADMIN
    }

    private fun updateProfileImage(imagePath: String) {
        if (imagePath.isNotEmpty()) {
            val file = File(imagePath)
            if (file.exists()) {
                Glide.with(this)
                    .load(imagePath)
                    .transform(CircleCrop())
                    .into(profileImageView)
            } else {
                Log.e("MainActivity", "Image file not found: $imagePath")
                profileImageView.setImageResource(R.drawable.ic_profile)
            }
        } else {
            profileImageView.setImageResource(R.drawable.ic_profile)
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Выход")
            .setMessage("Вы уверены, что хотите выйти?")
            .setPositiveButton("Да") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun performLogout() {
        // Очистка данных
        userViewModel.clearUserData()

        // Очистка SharedPreferences
        getSharedPreferences("AppSettings", Context.MODE_PRIVATE).edit().clear().apply()

        // Переход на экран входа
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}