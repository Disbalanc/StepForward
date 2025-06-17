package com.example.stepforward.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.stepforward.R
import com.example.stepforward.data.UserFileDataSource
import com.example.stepforward.data.model.LoggedInUser
import com.example.stepforward.data.model.Teacher
import com.example.stepforward.data.model.TrialLesson
import com.example.stepforward.databinding.FragmentFreeLessonBinding
import com.example.stepforward.ui.login.UserViewModel
import java.util.Calendar
import java.util.Date

class FreeLessonFragment : Fragment() {
    private var _binding: FragmentFreeLessonBinding? = null
    private val binding get() = _binding!!
    private lateinit var userViewModel: UserViewModel
    private lateinit var userFileDataSource: UserFileDataSource
    private lateinit var teacherSpinner: Spinner
    private lateinit var teachers: List<Teacher>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFreeLessonBinding.inflate(inflater, container, false)
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        userFileDataSource = UserFileDataSource(requireContext())
        teachers = arguments?.getParcelableArrayList("teachers") ?: emptyList()

        setupSpinners()
        setupSubmitButton()

        return binding.root
    }

    private fun setupSpinners() {
        // Направления танцев
        val danceDirections = resources.getStringArray(R.array.dance_directions)
        val danceDirectionAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            danceDirections
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.danceDirectionSpinner.adapter = danceDirectionAdapter

        // Студии
        val studios = arrayOf(
            getString(R.string.sinara),
            getString(R.string.south)
        )
        val studioAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            studios
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.studioSpinner.adapter = studioAdapter

        // Спиннер для выбора учителя
        teacherSpinner = binding.teacherSpinner
        val teacherAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            teachers.map { it.name }
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        teacherSpinner.adapter = teacherAdapter
    }

    private fun setupSubmitButton() {
        binding.submitBtn.setOnClickListener {
            val selectedDirection = binding.danceDirectionSpinner.selectedItem.toString()
            val selectedStudio = binding.studioSpinner.selectedItem.toString()
            val phone = binding.phoneEditText.text.toString()
            val age = binding.ageEditText.text.toString()

            if (validateInput(phone, age)) {
                val trialDate = generateTrialDate()
                val selectedTeacherPosition = teacherSpinner.selectedItemPosition
                val selectedTeacher = teachers[selectedTeacherPosition]

                userViewModel.user.value?.let { currentUser ->
// FreeLessonFragment.kt
                    val updatedUser = currentUser.copy(
                        daySession = currentUser.daySession, // Не добавляем пробное занятие в daySession
                        trialLesson = TrialLesson(
                            direction = selectedDirection,
                            studio = selectedStudio,
                            date = trialDate,
                            teacherId = selectedTeacher.idTeacher
                        )
                    )

                    userViewModel.updateUser(updatedUser)
                    if (userFileDataSource.updateUser(updatedUser)) {
                        showSuccessMessage(selectedDirection, selectedStudio, trialDate, selectedTeacher)
                        clearForm()
                    } else {
                        Toast.makeText(requireContext(), "Ошибка сохранения", Toast.LENGTH_SHORT).show()
                    }
                } ?: run {
                    Toast.makeText(requireContext(), "Ошибка: пользователь не найден", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun generateTrialDate(): Date {
        val calendar = Calendar.getInstance().apply {
            // Устанавливаем время пробного занятия (например, 18:00)
            set(Calendar.HOUR_OF_DAY, 18)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // Добавляем 1 день к текущей дате (чтобы не было сегодня, если время уже прошло)
            add(Calendar.DAY_OF_YEAR, 1)

            // Проверяем, что это рабочий день (пн, ср, пт)
            while (!isWorkingDay(get(Calendar.DAY_OF_WEEK))) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        return calendar.time
    }

    private fun isWorkingDay(dayOfWeek: Int): Boolean {
        return dayOfWeek == Calendar.MONDAY ||
                dayOfWeek == Calendar.WEDNESDAY ||
                dayOfWeek == Calendar.FRIDAY
    }

    private fun validateInput(phone: String, age: String): Boolean {
        if (phone.isBlank() || phone.length < 10) {
            Toast.makeText(requireContext(), "Введите корректный телефон", Toast.LENGTH_SHORT).show()
            return false
        }
        if (age.isBlank() || age.toIntOrNull() == null) {
            Toast.makeText(requireContext(), "Введите возраст", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun showSuccessMessage(
        direction: String,
        studio: String,
        date: Date,
        teacher: Teacher
    ) {
        val dateFormat = android.text.format.DateFormat.getDateFormat(requireContext())
        val timeFormat = android.text.format.DateFormat.getTimeFormat(requireContext())

        val message = """
        Вы записаны на пробное занятие!
        Направление: $direction
        Студия: $studio
        Учитель: ${teacher.name}
        Дата: ${dateFormat.format(date)} в ${timeFormat.format(date)}
    """.trimIndent()

        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun clearForm() {
        binding.phoneEditText.text?.clear()
        binding.ageEditText.text?.clear()
        binding.danceDirectionSpinner.setSelection(0)
        binding.studioSpinner.setSelection(0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}