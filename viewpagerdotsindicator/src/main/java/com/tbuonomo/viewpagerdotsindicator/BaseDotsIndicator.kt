package com.tbuonomo.viewpagerdotsindicator

import android.content.Context
import android.database.DataSetObserver
import android.graphics.Color
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback

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

  var pager: Pager? = null

  interface Pager {
    val isNotEmpty: Boolean
    val currentItem: Int
    val isEmpty: Boolean
    val count: Int
    fun setCurrentItem(item: Int, smoothScroll: Boolean)
    fun removeOnPageChangeListener()
    fun addOnPageChangeListener(onPageChangeListenerHelper: OnPageChangeListenerHelper)
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    refreshDots()
  }

  private fun refreshDotsCount() {
    if (dots.size < pager!!.count) {
      addDots(pager!!.count - dots.size)
    } else if (dots.size > pager!!.count) {
      removeDots(dots.size - pager!!.count)
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
    if (pager == null) {
      return
    }
    post {
      // Check if we need to refresh the dots count
      refreshDotsCount()
      refreshDotsColors()
      refreshDotsSize()
      refreshOnPageChangedListener()
    }
  }

  private fun refreshOnPageChangedListener() {
    if (pager!!.isNotEmpty) {
      pager!!.removeOnPageChangeListener()
      val onPageChangeListenerHelper = buildOnPageChangedListener()
      pager!!.addOnPageChangeListener(onPageChangeListenerHelper)
      onPageChangeListenerHelper.onPageScrolled(pager!!.currentItem, 0f)
    }
  }

  private fun refreshDotsSize() {
    for (i in 0 until pager!!.currentItem) {
      dots[i].setWidth(dotsSize.toInt())
    }
  }

  // ABSTRACT METHODS AND FIELDS

  abstract fun refreshDotColor(index: Int)
  abstract fun addDot(index: Int)
  abstract fun removeDot(index: Int)
  abstract fun buildOnPageChangedListener(): OnPageChangeListenerHelper
  abstract val type: Type

  // PUBLIC METHODS

  @Deprecated("Use setDotsColors() instead")
  fun setPointsColor(color: Int) {
    dotsColor = color
    refreshDotsColors()
  }

  fun setViewPager(viewPager: ViewPager) {
    if (viewPager.adapter == null) {
      throw IllegalStateException("You have to set an adapter to the view pager before " +
              "initializing the dots indicator !")
    }

    viewPager.adapter!!.registerDataSetObserver(object : DataSetObserver() {
      override fun onChanged() {
        super.onChanged()
        refreshDots()
      }
    })

    pager = object : Pager {
      var onPageChangeListener: OnPageChangeListener? = null

      override val isNotEmpty: Boolean
        get() = viewPager.isNotEmpty
      override val currentItem: Int
        get() = viewPager.currentItem
      override val isEmpty: Boolean
        get() = viewPager.isEmpty
      override val count: Int
        get() = viewPager.adapter?.count ?: 0

      override fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        viewPager.setCurrentItem(item, smoothScroll)
      }

      override fun removeOnPageChangeListener() {
        onPageChangeListener?.let { viewPager.removeOnPageChangeListener(it) }
      }

      override fun addOnPageChangeListener(onPageChangeListenerHelper:
      OnPageChangeListenerHelper) {
        onPageChangeListener = object : OnPageChangeListener {
          override fun onPageScrolled(position: Int, positionOffset: Float,
                  positionOffsetPixels: Int) {
            onPageChangeListenerHelper.onPageScrolled(position, positionOffset)
          }

          override fun onPageScrollStateChanged(state: Int) {
          }

          override fun onPageSelected(position: Int) {
          }
        }
        viewPager.addOnPageChangeListener(onPageChangeListener!!)
      }
    }

    refreshDots()
  }

  fun setViewPager2(viewPager2: ViewPager2) {
    if (viewPager2.adapter == null) {
      throw IllegalStateException("You have to set an adapter to the view pager before " +
              "initializing the dots indicator !")
    }


    viewPager2.adapter!!.registerAdapterDataObserver(object : AdapterDataObserver() {
      override fun onChanged() {
        super.onChanged()
        refreshDots()
      }
    })

    pager = object : Pager {
      var onPageChangeCallback: OnPageChangeCallback? = null

      override val isNotEmpty: Boolean
        get() = viewPager2.isNotEmpty
      override val currentItem: Int
        get() = viewPager2.currentItem
      override val isEmpty: Boolean
        get() = viewPager2.isEmpty
      override val count: Int
        get() = viewPager2.adapter?.itemCount ?: 0

      override fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        viewPager2.setCurrentItem(item, smoothScroll)
      }

      override fun removeOnPageChangeListener() {
        onPageChangeCallback?.let { viewPager2.unregisterOnPageChangeCallback(it) }
      }

      override fun addOnPageChangeListener(
              onPageChangeListenerHelper: OnPageChangeListenerHelper) {
        onPageChangeCallback = object : OnPageChangeCallback() {
          override fun onPageScrolled(position: Int, positionOffset: Float,
                  positionOffsetPixels: Int) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            onPageChangeListenerHelper.onPageScrolled(position, positionOffset)
          }
        }
        viewPager2.registerOnPageChangeCallback(onPageChangeCallback!!)
      }
    }

    refreshDots()
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
  protected val ViewPager2.isNotEmpty: Boolean get() = adapter!!.itemCount > 0

  protected val ViewPager?.isEmpty: Boolean
    get() = this != null && this.adapter != null &&
            adapter!!.count == 0

  protected val ViewPager2?.isEmpty: Boolean
    get() = this != null && this.adapter != null &&
            adapter!!.itemCount == 0

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    super.onLayout(changed, left, top, right, bottom)
    if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1 && layoutDirection == View.LAYOUT_DIRECTION_RTL) {
      layoutDirection = View.LAYOUT_DIRECTION_LTR
      rotation = 180f
      requestLayout()
    }
  }
}