package com.tbuonomo.viewpagerdotsindicator

import androidx.viewpager.widget.ViewPager
import kotlin.math.abs

abstract class OnPageChangeListenerHelper : ViewPager.OnPageChangeListener {
  private var currentPage: Int = 0
  private var lastPage: Int = 0

  internal abstract val pageCount: Int

  override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    var selectedPosition = currentPage
    if (position != currentPage && positionOffset == 0f || currentPage < position) {
      resetPosition(selectedPosition)
      currentPage = position
      selectedPosition = currentPage
    }

    if (abs(currentPage - position) > 1) {
      resetPosition(selectedPosition)
      currentPage = lastPage
    }

    var nextPosition = -1
    if (currentPage == position && currentPage + 1 < pageCount) {
      nextPosition = currentPage + 1
    } else if (currentPage > position) {
      nextPosition = selectedPosition
      selectedPosition = currentPage - 1
    }

    onPageScrolled(selectedPosition, nextPosition, positionOffset)

    lastPage = position
  }

  override fun onPageSelected(position: Int) {
    currentPage = position
  }

  override fun onPageScrollStateChanged(i: Int) {}

  internal abstract fun onPageScrolled(selectedPosition: Int, nextPosition: Int,
          positionOffset: Float)

  internal abstract fun resetPosition(position: Int)
}
