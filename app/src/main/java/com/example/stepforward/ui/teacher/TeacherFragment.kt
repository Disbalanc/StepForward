package com.example.stepforward.ui.teacher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.stepforward.data.model.Teacher
import com.example.stepforward.databinding.FragmentTeacherBinding
import com.example.stepforward.ui.login.UserViewModel

class TeacherFragment : Fragment() {

    private var _binding: FragmentTeacherBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by viewModels(ownerProducer = { requireActivity() })
    private var teacher: Teacher? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeacherBinding.inflate(inflater, container, false)

        return binding.root
    }

    private fun observeViewModel() {
        userViewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                // Получаем учителя из пользователя
                teacher = it.teacher
                updateUI(teacher)
            }
        }
    }

    private fun updateUI(teacher: Teacher?) {
        teacher?.let { teacher ->
            binding.teacherImage.setImageResource(teacher.imageRes)
            binding.teacherName.text = teacher.name
            binding.teacherDirection.text = teacher.direction.joinToString("\n")
            binding.teacherMasterClass.text = teacher.master_class.joinToString("\n")
            binding.teacherAchievements.text = teacher.achievements.joinToString("\n")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Наблюдаем за изменениями в UserViewModel
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}