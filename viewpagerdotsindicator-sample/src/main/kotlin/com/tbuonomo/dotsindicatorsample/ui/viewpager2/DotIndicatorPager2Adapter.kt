package com.tbuonomo.dotsindicatorsample.ui.viewpager2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.tbuonomo.dotsindicatorsample.R

class DotIndicatorPager2Adapter : RecyclerView.Adapter<ViewHolder>() {
    object Card

    private val items = MutableList(10) { Card }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return object : ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.material_page, parent, false)
        ) {}
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Empty
    }

    fun addItem(isUsingSmartNotify: Boolean) {
        items.add(Card)
        if (isUsingSmartNotify) {
            notifyItemInserted(items.size)
        } else {
            notifyDataSetChanged()
        }
    }

    fun removeItem(isUsingSmartNotify: Boolean) {
        val itemToRemove = items.random()
        val itemToRemoveIndex = items.indexOf(itemToRemove)
        items.remove(itemToRemove)
        if (isUsingSmartNotify) {
            notifyItemRemoved(itemToRemoveIndex)
        } else {
            notifyDataSetChanged()
        }
    }
}
