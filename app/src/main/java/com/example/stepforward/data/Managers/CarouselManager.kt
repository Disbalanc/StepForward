package com.example.stepforward.data.Managers

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.example.stepforward.R

class CarouselManager(
    private val recyclerView: RecyclerView,
    private val btnBack: ImageView,
    private val btnNext: ImageView,
) {
    private lateinit var adapter: RecyclerView.Adapter<*>
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var snapHelper: SnapHelper

    fun setup(adapter: RecyclerView.Adapter<*>) {
        this.adapter = adapter
        layoutManager = LinearLayoutManager(recyclerView.context, RecyclerView.HORIZONTAL, false)
        snapHelper = PagerSnapHelper()

        recyclerView.apply {
            layoutManager = this@CarouselManager.layoutManager
            this.adapter = this@CarouselManager.adapter
            setHasFixedSize(true)
        }

        snapHelper.attachToRecyclerView(recyclerView)
        setupNavigation()
    }

    private fun setupNavigation() {
        btnNext.setOnClickListener {
            smoothScrollToPosition(getCurrentPosition() + 1)
        }

        btnBack.setOnClickListener {
            smoothScrollToPosition(getCurrentPosition() - 1)
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                updateButtonVisibility()

            }
        })
    }

    private fun getCurrentPosition(): Int {
        return layoutManager.findFirstVisibleItemPosition()
    }

    private fun smoothScrollToPosition(position: Int) {
        when {
            position < 0 -> return
            position >= adapter.itemCount -> return
            else -> recyclerView.smoothScrollToPosition(position)
        }
    }

    private fun updateButtonVisibility() {
        val canScrollBack = recyclerView.canScrollHorizontally(-1)
        val canScrollForward = recyclerView.canScrollHorizontally(1)

        btnBack.visibility = if (canScrollBack) View.VISIBLE else View.INVISIBLE
        btnNext.visibility = if (canScrollForward) View.VISIBLE else View.INVISIBLE
    }

    private fun Int.dpToPx(): Int = (this * recyclerView.context.resources.displayMetrics.density).toInt()
}