package com.example.stepforward.data.Adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.stepforward.R
import com.example.stepforward.data.model.Teacher
import com.example.stepforward.data.model.TrialLesson
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class CalendarAdapter(private var items: List<Date>,
                      private var trialLessons: List<TrialLesson> = emptyList(),
                      private var teachers: List<Teacher> = emptyList() // Добавляем список учителей
) :
    RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameDay: TextView = view.findViewById(R.id.nameDay_tv)
        val time: TextView = view.findViewById(R.id.time_tv)
        val date: TextView = view.findViewById(R.id.date_tv) // New TextView for date
        val back: LinearLayout = view.findViewById(R.id.back)
        val teacher: TextView = view.findViewById(R.id.teacher_tv) // Новое поле для учителя

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val date = items[position]
                    val trialLesson = trialLessons.firstOrNull { isSameDay(date, it.date) }?.let {
//                        // Показать диалог с информацией о пробном занятии
//                        showTrialLessonInfo(it)
                    }
                }
            }
        }

        private fun isSameDay(date1: Date, date2: Date): Boolean {
            val cal1 = Calendar.getInstance().apply { time = date1 }
            val cal2 = Calendar.getInstance().apply { time = date2 }
            return cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                    cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_calndar, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val date = items[position]
        val dayFormat = SimpleDateFormat("EEEE", Locale("ru"))
        val timeFormat = SimpleDateFormat("HH:mm", Locale("ru"))
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale("ru"))

        holder.nameDay.text = dayFormat.format(date)
        holder.time.text = timeFormat.format(date)
        holder.date.text = dateFormat.format(date)

        // Проверяем, является ли это пробным занятием
        val trialLesson = trialLessons.firstOrNull { isSameDay(date, it.date) }

        when {
            trialLesson != null -> {
                val teacher = teachers.firstOrNull { it.idTeacher == trialLesson.teacherId }
                holder.teacher.text = teacher?.name ?: "Учитель"
                holder.teacher.visibility = View.VISIBLE

                // Форматируем дату из trialLesson
                holder.nameDay.text = dayFormat.format(trialLesson.date)
                holder.time.text = timeFormat.format(trialLesson.date)
                holder.date.text = dateFormat.format(trialLesson.date)

                // Стиль для пробного занятия
                holder.back.backgroundTintList = holder.itemView.context.resources.getColorStateList(R.color.trial_lesson)
                holder.nameDay.setTextColor(Color.WHITE)
                holder.time.setTextColor(Color.WHITE)
                holder.date.setTextColor(Color.WHITE)
            }
            isPastDate(date) -> {
                // Прошедшие занятия
                holder.back.backgroundTintList = holder.itemView.context.resources.getColorStateList(R.color.lt_gray)
                holder.nameDay.setTextColor(Color.parseColor("#AAAAAA"))
                holder.time.setTextColor(Color.parseColor("#AAAAAA"))
                holder.date.setTextColor(Color.parseColor("#AAAAAA"))
            }
            isToday(date) -> {
                // Сегодняшнее занятие
                holder.back.backgroundTintList = holder.itemView.context.resources.getColorStateList(R.color.accent)
                holder.nameDay.setTextColor(Color.WHITE)
                holder.time.setTextColor(Color.WHITE)
                holder.date.setTextColor(Color.WHITE)
            }
            else -> {
                // Будущие занятия
                holder.back.backgroundTintList = null
                holder.nameDay.setTextColor(Color.parseColor("#2B2B2B"))
                holder.time.setTextColor(Color.parseColor("#2B2B2B"))
                holder.date.setTextColor(Color.parseColor("#2B2B2B"))
            }
        }
    }

    private fun isPastDate(date: Date): Boolean {
        val currentDate = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
        return date.before(currentDate)
    }

    private fun isToday(date: Date): Boolean {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val checkDate = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        return today == checkDate
    }

    fun updateList(
        newList: List<Date>,
        newTrialLessons: List<TrialLesson> = emptyList(),
        newTeachers: List<Teacher> = emptyList()
    ) {
        items = newList.sortedBy { it.time }
        trialLessons = newTrialLessons
        teachers = newTeachers
        notifyDataSetChanged()
    }

    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    override fun getItemCount() = items.size
}