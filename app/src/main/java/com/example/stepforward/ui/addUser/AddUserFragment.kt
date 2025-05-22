package com.example.stepforward.ui.addUser

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.example.stepforward.R
import com.example.stepforward.data.LoginDataSource
import com.example.stepforward.data.model.LoggedInUser
import com.example.stepforward.data.model.Role
import com.example.stepforward.data.model.Teacher
import java.util.UUID

class AddUserFragment : Fragment() {

    private lateinit var abonementEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var displayNameEditText: EditText
    private lateinit var profileImageView: ImageView
    private lateinit var addButton: Button
    private lateinit var loginDataSource: LoginDataSource
    private var selectedImageUri: Uri? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedImageUri = uri
        profileImageView.setImageURI(uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginDataSource = LoginDataSource(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_user, container, false)

        abonementEditText = view.findViewById(R.id.abonementEditText)
        passwordEditText = view.findViewById(R.id.passwordEditText)
        displayNameEditText = view.findViewById(R.id.displayNameEditText)
        profileImageView = view.findViewById(R.id.profileImageView)
        addButton = view.findViewById(R.id.addButton)

        profileImageView.setOnClickListener {
            pickImage.launch("image/*")
        }

        addButton.setOnClickListener {
            addUser ()
        }

        return view
    }

    private fun addUser () {
        val abonement = abonementEditText.text.toString()
        val password = passwordEditText.text.toString()
        val displayName = displayNameEditText.text.toString()

        if (abonement.isEmpty() || password.isEmpty() || displayName.isEmpty()) {
            Toast.makeText(requireContext(), "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
            return
        }


        val bitmap = profileImageView.drawable?.toBitmap()
        var imagePath = ""
        if (bitmap != null) {
            imagePath = loginDataSource.saveImageToFile(requireContext(), bitmap)
        } else {
            // Если изображение не выбрано, используйте ресурс по умолчанию
            val defaultBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_profile)
            imagePath = loginDataSource.saveImageToFile(requireContext(), defaultBitmap)
        }

        val newUser  = LoggedInUser(
            userId = UUID.randomUUID().toString(),
            abonement = abonement,
            pass = password,
            displayName = displayName,
            displaySecondName = "",
            displaySurName = "",
            dateBirthday = "",
            imagePath = imagePath,
            daySession = emptyList(),
            teacher = Teacher(
                idTeacher = 0,
                imageRes = 0,
                name = "",
                direction = emptyList(),
                achievements = emptyList(),
                master_class = emptyList()
            ),
            role = Role.USER
        )

        loginDataSource.registerUser (newUser )
        Toast.makeText(requireContext(), "Пользователь добавлен", Toast.LENGTH_SHORT).show()

        abonementEditText.text.clear()
        passwordEditText.text.clear()
        displayNameEditText.text.clear()
        profileImageView.setImageResource(R.drawable.ic_profile)
    }
}