package com.example.stepforward.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stepforward.R
import com.example.stepforward.data.Adapters.CalendarAdapter
import com.example.stepforward.data.model.LoggedInUser
import com.example.stepforward.data.model.Teacher
import com.example.stepforward.databinding.FragmentCalendarBinding
import com.example.stepforward.ui.login.UserViewModel

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by viewModels(ownerProducer = { requireActivity() })
    private lateinit var calendarAdapter: CalendarAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        calendarAdapter = CalendarAdapter(emptyList(), emptyList(), emptyList()) // Три пустых списка
        binding.calendarRc.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = calendarAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        userViewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                updateCalendarData(user)
                updateAbonementInfo(user)
            }
        }
    }

    private fun updateCalendarData(user: LoggedInUser) {
        val sessions = user.daySession ?: emptyList()
        val trialLessons = user.trialLesson?.let { listOf(it) } ?: emptyList()
        val teachers = getTeachers() // Получаем список учителей

        calendarAdapter.updateList(
            sessions.sortedDescending(),
            trialLessons,
            teachers // Передаем список учителей
        )
    }

    private fun getTeachers() = listOf(
        Teacher(
            idTeacher = 1,
            imageRes = R.drawable.teacher1, // Убедитесь, что этот ресурс существует
            name = "Оля",
            direction = listOf("Современные танцы", "Хип-Хоп"),
            achievements = listOf("Победитель конкурса танцев", "Сертифицированный тренер"),
            master_class = listOf("Мастер-класс по хип-хопу", "Современные танцы для начинающих")
        ),
        Teacher(
            idTeacher = 2,
            imageRes = R.drawable.teacher2, // Убедитесь, что этот ресурс существует
            name = "Учитель 1",
            direction = listOf("Бальные танцы", "Современные танцы"),
            achievements = listOf("Чемпионка по бальным танцам", "Участница международных конкурсов"),
            master_class = listOf("Бальные танцы для начинающих", "Современные танцы для детей")
        ),
        Teacher(
            idTeacher = 3,
            imageRes = R.drawable.teacher3, // Убедитесь, что этот ресурс существует
            name = "Учитель 2",
            direction = listOf("Танцы на пилоне", "Стрип-пластика"),
            achievements = listOf("Победительница чемпионата по танцам на пилоне", "Тренер с 10-летним стажем"),
            master_class = listOf("Танцы на пилоне для начинающих", "Стрип-пластика для женщин")
        )
    )

    private fun updateAbonementInfo(user: LoggedInUser) {
        val sessions = user.daySession ?: emptyList()
        val currentDateTime = System.currentTimeMillis()
        val used = sessions.count { date ->
            date.time <= currentDateTime
        }
        val total = sessions.size
        val remaining = total - used
        binding.abonementInfo.text = "Абонементы: $used использовано, $remaining осталось"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}