package com.tbuonomo.viewpagerdotsindicator

import android.content.Context
import android.database.DataSetObserver
import android.graphics.Color
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView

abstract class BaseDotsIndicator @JvmOverloads constructor(context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) :
        FrameLayout(context, attrs, defStyleAttr) {

  companion object {
    const val DEFAULT_POINT_COLOR = Color.CYAN
    const val DEFAULT_WIDTH_FACTOR = 2.5f
  }

  @JvmField
  protected val dots = ArrayList<ImageView>()

  private var onPageChangeListenerHelper: OnPageChangeListenerHelper? = null

  var dotsClickable: Boolean = true
  var dotsColor: Int = DEFAULT_POINT_COLOR
    set(value) {
      field = value
      refreshDotsColors()
    }

  protected var dotsSize: Float = dpToPx(16).toFloat() // 16dp
  protected var dotsCornerRadius: Float = dpToPx(4).toFloat() // 4dp
  protected var dotsSpacing: Float = dotsSize / 2

  var viewPager: ViewPager? = null
    set(value) {
      field = value
      setUpViewPager()
      refreshDots()
    }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    refreshDots()
  }

  private fun setUpViewPager() {
    if (viewPager!!.adapter != null) {
      viewPager!!.adapter!!.registerDataSetObserver(object : DataSetObserver() {
        override fun onChanged() {
          super.onChanged()
          refreshDots()
        }
      })
    }
  }

  protected fun refreshDotsCount() {
    if (dots.size < viewPager!!.adapter!!.count) {
      addDots(viewPager!!.adapter!!.count - dots.size)
    } else if (dots.size > viewPager!!.adapter!!.count) {
      removeDots(dots.size - viewPager!!.adapter!!.count)
    }
  }

  protected fun refreshDotsColors() {
    for (i in dots.indices) {
      refreshDotColor(i)
    }
  }

  protected fun dpToPx(dp: Int): Int {
    return (context.resources.displayMetrics.density * dp).toInt()
  }

  protected fun addDots(count: Int) {
    for (i in 0 until count) {
      addDot(i)
    }
  }

  protected fun removeDots(count: Int) {
    for (i in 0 until count) {
      removeDot(i)
    }
  }

  protected fun refreshDots() {
    if (viewPager != null && viewPager!!.adapter != null) {
      post {
        // Check if we need to refresh the dots count
        refreshDotsCount()
        refreshDotsColors()
        refreshDotsSize()
        refreshOnPageChangedListener()
      }
    } else {
      Log.e(DotsIndicator::class.java.simpleName,
              "You have to set an adapter to the view pager before !")
    }
  }

  protected fun refreshOnPageChangedListener() {
    if (viewPager != null && viewPager!!.adapter != null && viewPager!!.adapter!!.count > 0) {
      onPageChangeListenerHelper?.let { viewPager!!.removeOnPageChangeListener(it) }
      onPageChangeListenerHelper = buildOnPageChangedListener()
      viewPager!!.addOnPageChangeListener(onPageChangeListenerHelper!!)
      onPageChangeListenerHelper!!.onPageScrolled(viewPager!!.currentItem, -1, 0f)
    }
  }

  protected fun refreshDotsSize() {
    for (i in 0 until viewPager!!.currentItem) {
      dots[i].setWidth(dotsSize.toInt())
    }
  }

  // ABSTRACT METHODS

  abstract fun refreshDotColor(index: Int)
  abstract fun addDot(index: Int)
  abstract fun removeDot(index: Int)
  abstract fun buildOnPageChangedListener(): OnPageChangeListenerHelper

  // PUBLIC METHODS

  @Deprecated("Use setDotsColors() instead")
  fun setPointsColor(color: Int) {
    dotsColor = color
    refreshDotsColors()
  }

  // EXTENSIONS

  fun View.setWidth(width: Int) {
    layoutParams.apply {
      this.width = width
      requestLayout()
    }
  }

  fun <T> ArrayList<T>.isInBounds(index: Int) = index in 0 until size
}