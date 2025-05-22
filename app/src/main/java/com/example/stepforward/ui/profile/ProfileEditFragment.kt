package com.example.stepforward.ui.profile

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.stepforward.R
import com.example.stepforward.data.model.LoggedInUser
import com.example.stepforward.databinding.FragmentProfileEditBinding
import com.example.stepforward.ui.login.UserViewModel
import java.io.File

class ProfileEditFragment : Fragment() {

    private var _binding: FragmentProfileEditBinding? = null
    private val userViewModel: UserViewModel by viewModels(ownerProducer = { requireActivity() })
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
                binding.profileImage.setImageURI(uri) // Устанавливаем изображение в ImageView
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        userViewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                loadUserData(it)
            }
        }
    }

    private fun loadUserData(user: LoggedInUser) {
        Log.d("ProfileEdit", "Loading user: ${user.displayName}, image: ${user.imagePath}")

        if (user.imagePath.isNotEmpty()) {
            val file = File(user.imagePath)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(user.imagePath)
                if (bitmap != null) {
                    binding.profileImage.setImageBitmap(bitmap)
                } else {
                    binding.profileImage.setImageResource(R.drawable.ic_profile)
                }
            } else {
                binding.profileImage.setImageResource(R.drawable.ic_profile)
            }
        } else {
            binding.profileImage.setImageResource(R.drawable.ic_profile)
        }

        binding.nameText.setText(user.displayName)
        binding.abonementText.setText(user.abonement)
    }

    private fun setupClickListeners() {
        // Редактирование фото профиля
        binding.editProfileImage.setOnClickListener {
            openImagePicker()
        }

        // Сохранение изменений
        binding.saveBtn.setOnClickListener {
            saveChanges()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun saveChanges() {
        val currentUser = userViewModel.user.value ?: run {
            Log.e("ProfileEdit", "User is null")
            return
        }

        val newImagePath = tempImagePath ?: currentUser.imagePath
        Log.d("ProfileEdit", "Saving image path: $newImagePath")

        val updatedUser = currentUser.copy(
            displayName = binding.nameText.text.toString(),
            abonement = binding.abonementText.text.toString(),
            imagePath = newImagePath
        )

        userViewModel.updateUser(updatedUser)
        requireActivity().onBackPressed()
    }

    private fun getRealPathFromURI(uri: Uri): String? {
        return context?.contentResolver?.openInputStream(uri)?.use { inputStream ->
            val file = File.createTempFile("temp_image", ".jpg", context?.cacheDir)
            file.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            file.absolutePath
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}