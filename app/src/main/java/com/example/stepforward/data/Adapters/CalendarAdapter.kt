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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CalendarAdapter(private var items: List<Date>) :
    RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameDay: TextView = view.findViewById(R.id.nameDay_tv)
        val time: TextView = view.findViewById(R.id.time_tv)
        val back: LinearLayout = view.findViewById(R.id.back)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_calndar, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val date = items[position]
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        holder.nameDay.text = dayFormat.format(date)
        holder.time.text = timeFormat.format(date)

        // Проверка, является ли дата прошедшей
        if (isPastDate(date)) {
            holder.back.backgroundTintList = holder.itemView.context.resources.getColorStateList(R.color.lt_gray)
        }
    }

    private fun isPastDate(date: Date): Boolean {
        val currentDate = Date()
        return date.before(currentDate)
    }

    fun updateList(newList: List<Date>) {
        items = newList
        notifyDataSetChanged()
    }

    override fun getItemCount() = items.size
}