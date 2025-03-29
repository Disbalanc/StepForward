package com.example.stepforward.ui.profile

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.stepforward.data.model.LoggedInUser
import com.example.stepforward.databinding.FragmentProfileEditBinding
import com.example.stepforward.ui.login.UserViewModel
import java.io.File

class ProfileEditFragment : Fragment() {

    private var _binding: FragmentProfileEditBinding? = null
    private lateinit var userViewModel: UserViewModel
    private var tempImagePath: String? = null

    private val binding get() = _binding!!

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { uri ->
                // Сохраняем временный путь к изображению
                tempImagePath = getRealPathFromURI(uri)
                tempImagePath?.let {
                    binding.profileImage.setImageURI(uri)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        _binding = FragmentProfileEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupObservers()
        setupClickListeners()
    }

    private fun setupUI() {
        binding.login.text = "Сохранить"
    }

    private fun setupObservers() {
        userViewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                loadUserData(it)
            }
        }
    }

    private fun loadUserData(user: LoggedInUser) {
        // Загрузка изображения
        if (user.imagePath.isNotEmpty()) {
            val bitmap = BitmapFactory.decodeFile(user.imagePath)
            bitmap?.let {
                binding.profileImage.setImageBitmap(it)
            }
        }

        // Установка данных в поля
        binding.nameText.setText(user.displayName) // Исправлено на nameText
        binding.abonementText.setText(user.abonement) // Исправлено на abonementText
    }

    private fun setupClickListeners() {
        // Редактирование фото профиля
        binding.editProfileImage.setOnClickListener {
            openImagePicker()
        }

        // Сохранение изменений
        binding.login.setOnClickListener {
            saveChanges()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun saveChanges() {
        val currentUser  = userViewModel.user.value ?: return

        val updatedUser  = currentUser .copy(
            displayName = binding.nameText.text.toString(), // Исправлено на nameText
            abonement = binding.abonementText.text.toString(), // Исправлено на abonementText
            imagePath = tempImagePath ?: currentUser .imagePath
        )

        userViewModel.updateUser (updatedUser )
        requireActivity().onBackPressed()
    }

    private fun getRealPathFromURI(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = requireActivity().contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                return it.getString(columnIndex)
            }
        }
        return null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}