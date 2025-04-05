package com.example.stepforward.ui.home

import TeacherAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.HandlerCompat.postDelayed
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.example.stepforward.R
import com.example.stepforward.R.drawable.teacher1
import com.example.stepforward.data.Adapters.ClubCartAdapter
import com.example.stepforward.data.Adapters.ClubCartItemAdapter
import com.example.stepforward.data.Adapters.StyleDirectionAdapter
import com.example.stepforward.data.Managers.CarouselManager
import com.example.stepforward.data.model.ClubCart
import com.example.stepforward.data.model.StyleDirection
import com.example.stepforward.data.model.Teacher
import com.example.stepforward.databinding.FragmentHomeBinding
import com.example.stepforward.ui.teacher.TeacherAndDetailsFragment
import io.github.jan.supabase.auth.Auth.Companion.setup

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var teacherManager: CarouselManager
    private lateinit var styleDirectionManager: CarouselManager
    private lateinit var clubCartRecycler: RecyclerView
    private lateinit var clubCartItemsRecycler: RecyclerView
    private lateinit var tvClubCartTitle: TextView
    private lateinit var tvClubCartPrice: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupAllCarousels()
        setupClubCarts()
        setupNavigation()
        updateClubCartDetails()
        updateClubCartDetails()

        // Инициализация данных первой карточки
        binding.root.postDelayed(::updateClubCartDetails, 100)

        binding.recordBtn.setOnClickListener {
            // код обработки нажатия на кнопку записи на пробный прием
        }

        return binding.root
    }

    private fun setupAllCarousels() {
        // Настройка карусели преподавателей
        teacherManager = CarouselManager(
            recyclerView = binding.TeacherRc,
            btnBack = binding.BackTeacherBtn,
            btnNext = binding.NextTeacherBtn
        ).apply {
            setup(TeacherAdapter(getTeachers()) { teacher ->
                val actionId = R.id.action_nav_home_to_teacherAndDetailsFragment
                val bundle = Bundle().apply {
                    putParcelable("teacher", teacher) // Передаем объект Teacher через Bundle
                }
                findNavController().navigate(actionId, bundle)
            })
        }

        // Настройка карусели стилей
        styleDirectionManager = CarouselManager(
            recyclerView = binding.StyleAndDirectionRc,
            btnBack = binding.BackStyleAndDirectionsBtn,
            btnNext = binding.NextStyleAndDirectionsBtn
        ).apply {
            setup(StyleDirectionAdapter(getStyles()))
        }
    }

    private fun setupClubCarts() {
        // Настройка основного RecyclerView
        clubCartRecycler = binding.clubCartRecycler
        clubCartRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = ClubCartAdapter(getClubCarts)
        }

        // Настройка RecyclerView с пунктами
        clubCartItemsRecycler = binding.clubCartItemsRecycler
        clubCartItemsRecycler.layoutManager = LinearLayoutManager(requireContext())

        // Привязка остальных элементов
        tvClubCartTitle = binding.tvClubCartTitle
        tvClubCartPrice = binding.tvClubCartPrice

        // Обновление данных при прокрутке
        clubCartRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                updateClubCartDetails()
            }
        })

        // Инициализация первого элемента
        updateClubCartDetails()
    }

    private fun setupNavigation() {
        binding.BackClubCartBtn.setOnClickListener {
            smoothScroll(-1)
        }

        binding.NextClubCartBtn.setOnClickListener {
            smoothScroll(1)
        }
    }

    private fun smoothScroll(direction: Int) {
        val layoutManager = clubCartRecycler.layoutManager as LinearLayoutManager
        val currentPosition = layoutManager.findFirstVisibleItemPosition()
        val targetPosition = currentPosition + direction

        if (targetPosition in 0 until getClubCarts.size) {
            clubCartRecycler.smoothScrollToPosition(targetPosition)
        }
    }

    private fun updateClubCartDetails() {
        val layoutManager = clubCartRecycler.layoutManager as LinearLayoutManager
        var position = layoutManager.findFirstVisibleItemPosition()

        if (position == RecyclerView.NO_POSITION) {
            position = 0
        }

        if (position in getClubCarts.indices) {
            val currentCart = getClubCarts[position]

            binding.tvClubCartTitle.text = currentCart.title
            binding.tvClubCartPrice.text = "%.2f ₽".format(currentCart.price)

            binding.clubCartItemsRecycler.adapter = ClubCartItemAdapter(currentCart.enter)
        }
    }

    // В вашем фрагменте/активности
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
    private fun getStyles() = listOf(
        StyleDirection(1,R.drawable.style1, "Современные"),
        StyleDirection(2,R.drawable.style2, "Хип-Хоп"),
        StyleDirection(3,R.drawable.style3, "Бальные")
    )

    private val getClubCarts = listOf(
        ClubCart(
            id = 1,
            imageRes = R.drawable.club1,
            price = 999.99f,
            title = "VIP Карта",
            enter = listOf("Персональный тренер", "СПА-зона", "Персональный шкафчик")
        ),
        ClubCart(
            id = 2,
            imageRes = R.drawable.club2,
            price = 499.99f,
            title = "Стандарт",
            enter = listOf("Групповые занятия", "Фитнес-бар", "Сауна")
        )
    )

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}