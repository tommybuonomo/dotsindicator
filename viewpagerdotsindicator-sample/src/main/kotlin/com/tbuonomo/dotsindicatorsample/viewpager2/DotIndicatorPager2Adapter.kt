package com.tbuonomo.dotsindicatorsample.viewpager2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.tbuonomo.dotsindicatorsample.R

class DotIndicatorPager2Adapter : RecyclerView.Adapter<ViewHolder>() {

    data class Card(val id: Int)

    val items = mutableListOf<Card>().apply {
        repeat(10) { add(Card(it)) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return object : ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.material_page, parent, false)
        ) {}
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Empty
    }
}
