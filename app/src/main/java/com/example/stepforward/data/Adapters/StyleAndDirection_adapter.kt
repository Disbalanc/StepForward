package com.example.stepforward.data.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stepforward.R
import com.example.stepforward.data.model.StyleDirection

class StyleDirectionAdapter(private val items: List<StyleDirection>) : RecyclerView.Adapter<StyleDirectionAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.style_image)
        val title: TextView = view.findViewById(R.id.style_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.styleanddirection_item, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.image.setImageResource(item.ImageRes)
        holder.title.text = item.Title
    }

    override fun getItemCount() = items.size
}
