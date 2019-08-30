package com.tbuonomo.viewpagerdotsindicator

import androidx.viewpager.widget.ViewPager

abstract class OnPageChangeListenerHelper : ViewPager.OnPageChangeListener {
  private var lastLeftPosition: Int = -1
  private var lastRightPosition: Int = -1

  internal abstract val pageCount: Int

  override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    var offset = (position + positionOffset)
    val lastPageIndex = (pageCount - 1).toFloat()
    if (offset == lastPageIndex) {
      offset = lastPageIndex - .0001f
    }
    val leftPosition = offset.toInt()
    val rightPosition = leftPosition + 1
    onPageScrolled(leftPosition, rightPosition, offset % 1)

    if (lastLeftPosition != -1) {
      if (leftPosition > lastLeftPosition) {
        resetPosition(lastLeftPosition)
      }

      if (rightPosition < lastRightPosition) {
        resetPosition(lastRightPosition)
      }
    }

    lastLeftPosition = leftPosition
    lastRightPosition = rightPosition
  }

  override fun onPageSelected(position: Int) {
    // Empty
  }

  override fun onPageScrollStateChanged(i: Int) {}

  internal abstract fun onPageScrolled(selectedPosition: Int, nextPosition: Int,
          positionOffset: Float)

  internal abstract fun resetPosition(position: Int)
}
