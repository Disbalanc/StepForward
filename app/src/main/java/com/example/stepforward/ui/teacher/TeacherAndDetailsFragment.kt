package com.example.stepforward.ui.teacher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.stepforward.R
import com.example.stepforward.data.model.Teacher
import com.example.stepforward.databinding.FragmentTeacherAndDetailsBinding

class TeacherAndDetailsFragment : Fragment() {

    private var _binding: FragmentTeacherAndDetailsBinding? = null
    private val binding get() = _binding!!

    private var teacher: Teacher? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeacherAndDetailsBinding.inflate(inflater, container, false)

        // Получаем данные о выбранном учителе из аргументов
        arguments?.let {
            teacher = it.getParcelable("teacher")
        }

        binding.recordBtn.setOnClickListener {
            // код обработки нажатия на кнопку записи на бесплатное прием
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Проверяем, что teacher не равен null, и обновляем UI
        teacher?.let { teacher ->
            binding.teacherImage.setImageResource(teacher.imageRes)
            binding.teacherName.text = teacher.name
            binding.teacherDirection.text = teacher.direction.joinToString("\n")
            binding.teacherMasterClass.text = teacher.master_class.joinToString("\n")
            binding.teacherAchievements.text = teacher.achievements.joinToString("\n")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}