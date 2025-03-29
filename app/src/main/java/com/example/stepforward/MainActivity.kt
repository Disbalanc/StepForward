package com.example.stepforward

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.stepforward.databinding.ActivityMainBinding
import com.example.stepforward.ui.login.LoginActivity
import com.example.stepforward.data.model.LoggedInUser
import com.example.stepforward.ui.login.UserViewModel
import com.example.stepforward.ui.profile.ProfileFragment
import com.google.android.material.navigation.NavigationView

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        // Обновленная инициализация
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        intent.getParcelableExtra<LoggedInUser>("loggedInUser")?.let { user ->
            userViewModel.updateUser(user)
        }

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_profile, R.id.nav_teacher, R.id.nav_notification, R.id.nav_setting, R.id.nav_calendar, R.id.nav_feedback
            ), drawerLayout
        )

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_logout -> {
                    // Обработка выхода
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}