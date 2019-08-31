package com.tbuonomo.dotsindicatorsample.viewpager2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.tbuonomo.dotsindicatorsample.R

class DotIndicatorPager2Adapter : RecyclerView.Adapter<ViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return object : ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.material_page, parent, false)) {}
  }

  override fun getItemCount() = 10

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    // Empty
  }
}
