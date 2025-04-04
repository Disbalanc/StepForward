package com.example.stepforward.ui.profile

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.stepforward.R
import com.example.stepforward.data.model.LoggedInUser
import com.example.stepforward.databinding.FragmentProfileBinding
import com.example.stepforward.ui.login.UserViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private lateinit var userViewModel: UserViewModel

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                updateUI(it)
            } ?: run {
                Log.e("ProfileFragment", "User  data not available")
                requireActivity().supportFragmentManager.popBackStack()
            }
        }

        // Обработчик нажатия на кнопку "Редактировать"
        binding.editButton.setOnClickListener {
            // Переход на ProfileEditFragment
            findNavController().navigate(R.id.profileEditFragment)
        }
    }

    private fun updateUI(user: LoggedInUser ) {
        // Заполнение полей данными пользователя
        binding.fullName.text = "${user.displayName} ${user.displaySecondName} ${user.displaySurName}"

        // Загрузка изображения
        val bitmap = BitmapFactory.decodeFile(user.imagePath)
        if (bitmap != null) {
            binding.profileImage.setImageBitmap(bitmap)
        } else {
            Log.e("ProfileFragment", "Failed to load image from path: ${user.imagePath}")
        }

        // Calculate age
        val age = calculateAge(user.dateBirthday)
        binding.age.text = "${age} Лет"

        binding.fullNameText.text = user.displayName
        binding.secondNameText.text = user.displaySecondName
        binding.surnameText.text = user.displaySurName
        binding.birthDate.setText(user.dateBirthday)
    }

    private fun calculateAge(dateString: String): Int {
        val sdf = SimpleDateFormat("dd.MM.yyyy")
        val birthDate: Date? = sdf.parse(dateString)
        val calendar = Calendar.getInstance()

        if (birthDate != null) {
            calendar.time = birthDate
            val birthYear = calendar.get(Calendar.YEAR)
            val birthMonth = calendar.get(Calendar.MONTH)
            val birthDay = calendar.get(Calendar.DAY_OF_MONTH)

            val now = Calendar.getInstance()
            var age = now.get(Calendar.YEAR) - birthYear

            // Adjust age if the birthday hasn't occurred yet this year
            if (now.get(Calendar.MONTH) < birthMonth ||
                (now.get(Calendar.MONTH) == birthMonth && now.get(Calendar.DAY_OF_MONTH) < birthDay)) {
                age--
            }
            return age
        }
        return 0 // In case of an error, return 0 or handle appropriately
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}