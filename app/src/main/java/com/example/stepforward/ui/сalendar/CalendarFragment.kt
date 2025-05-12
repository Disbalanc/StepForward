package com.example.stepforward.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stepforward.data.Adapters.CalendarAdapter
import com.example.stepforward.data.model.LoggedInUser
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
        calendarAdapter = CalendarAdapter(emptyList())
        binding.calendarRc.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = calendarAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        userViewModel.user.observe(viewLifecycleOwner) { user ->
            user?.daySession?.let { sessions ->
                calendarAdapter.updateList(sessions.sortedDescending())
                updateAbonementInfo(user)
            }
        }
    }

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