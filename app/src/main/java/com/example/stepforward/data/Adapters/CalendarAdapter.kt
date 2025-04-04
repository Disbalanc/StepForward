package com.example.stepforward.data.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
    }

    fun updateList(newList: List<Date>) {
        items = newList
        notifyDataSetChanged()
    }

    override fun getItemCount() = items.size
}