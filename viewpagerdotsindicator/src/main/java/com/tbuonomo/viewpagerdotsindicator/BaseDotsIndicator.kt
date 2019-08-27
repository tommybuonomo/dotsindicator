package com.tbuonomo.viewpagerdotsindicator

import android.content.Context
import android.database.DataSetObserver
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener

abstract class BaseDotsIndicator @JvmOverloads constructor(context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) :
        FrameLayout(context, attrs, defStyleAttr) {

  companion object {
    const val DEFAULT_POINT_COLOR = Color.CYAN
  }

  enum class Type(val defaultSize: Float,
          val defaultSpacing: Float,
          val styleableId: IntArray,
          val dotsColorId: Int,
          val dotsSizeId: Int,
          val dotsSpacingId: Int,
          val dotsCornerRadiusId: Int) {
    DEFAULT(16f,
            8f,
            R.styleable.SpringDotsIndicator,
            R.styleable.SpringDotsIndicator_dotsColor,
            R.styleable.SpringDotsIndicator_dotsSize,
            R.styleable.SpringDotsIndicator_dotsSpacing,
            R.styleable.SpringDotsIndicator_dotsCornerRadius),
    SPRING(16f,
            4f,
            R.styleable.DotsIndicator,
            R.styleable.DotsIndicator_dotsColor,
            R.styleable.DotsIndicator_dotsSize,
            R.styleable.DotsIndicator_dotsSpacing,
            R.styleable.DotsIndicator_dotsCornerRadius),
    WORM(16f,
            4f,
            R.styleable.WormDotsIndicator,
            R.styleable.WormDotsIndicator_dotsColor,
            R.styleable.WormDotsIndicator_dotsSize,
            R.styleable.WormDotsIndicator_dotsSpacing,
            R.styleable.WormDotsIndicator_dotsCornerRadius)
  }

  @JvmField
  protected val dots = ArrayList<ImageView>()

  private var onPageChangeListener: OnPageChangeListener? = null

  var dotsClickable: Boolean = true
  var dotsColor: Int = DEFAULT_POINT_COLOR
    set(value) {
      field = value
      refreshDotsColors()
    }

  protected var dotsSize = dpToPxF(type.defaultSize)
  protected var dotsCornerRadius = dotsSize / 2f
  protected var dotsSpacing = dpToPxF(type.defaultSpacing)

  init {
    if (attrs != null) {
      val a = context.obtainStyledAttributes(attrs, type.styleableId)

      dotsColor = a.getColor(type.dotsColorId, DEFAULT_POINT_COLOR)
      dotsSize = a.getDimension(type.dotsSizeId, dotsSize)
      dotsCornerRadius = a.getDimension(type.dotsCornerRadiusId, dotsCornerRadius)
      dotsSpacing = a.getDimension(type.dotsSpacingId, dotsSpacing)

      a.recycle()
    }
  }

  var viewPager: ViewPager? = null
    set(value) {
      if (value!!.adapter == null) {
        throw IllegalStateException("You have to set an adapter to the view pager before " +
                "initializing the dots indicator !")
      }
      field = value

      value.adapter!!.registerDataSetObserver(object : DataSetObserver() {
        override fun onChanged() {
          super.onChanged()
          refreshDots()
        }
      })

      refreshDots()
    }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    refreshDots()
  }

  private fun refreshDotsCount() {
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

  protected fun dpToPxF(dp: Float): Float {
    return context.resources.displayMetrics.density * dp
  }

  protected fun addDots(count: Int) {
    for (i in 0 until count) {
      addDot(i)
    }
  }

  private fun removeDots(count: Int) {
    for (i in 0 until count) {
      removeDot(i)
    }
  }

  protected fun refreshDots() {
    post {
      // Check if we need to refresh the dots count
      refreshDotsCount()
      refreshDotsColors()
      refreshDotsSize()
      refreshOnPageChangedListener()
    }
  }

  private fun refreshOnPageChangedListener() {
    if (viewPager!!.isNotEmpty) {
      onPageChangeListener?.let { viewPager!!.removeOnPageChangeListener(it) }
      onPageChangeListener = buildOnPageChangedListener()
      viewPager!!.addOnPageChangeListener(onPageChangeListener!!)
      onPageChangeListener!!.onPageScrolled(viewPager!!.currentItem, 0f, 0)
    }
  }

  private fun refreshDotsSize() {
    for (i in 0 until viewPager!!.currentItem) {
      dots[i].setWidth(dotsSize.toInt())
    }
  }

  // ABSTRACT METHODS AND FIELDS

  abstract fun refreshDotColor(index: Int)
  abstract fun addDot(index: Int)
  abstract fun removeDot(index: Int)
  abstract fun buildOnPageChangedListener(): OnPageChangeListener
  abstract val type: Type

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

  fun Context.getThemePrimaryColor(): Int {
    val value = TypedValue()
    this.theme.resolveAttribute(R.attr.colorPrimary, value, true)
    return value.data
  }

  protected val ViewPager.isNotEmpty: Boolean get() = adapter!!.count > 0
  protected val ViewPager?.isEmpty: Boolean
    get() = this != null && this.adapter != null &&
            adapter!!.count == 0
}