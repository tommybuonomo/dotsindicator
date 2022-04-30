package com.tbuonomo.viewpagerdotsindicator

import android.content.Context
import android.graphics.Color
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.StyleableRes
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.tbuonomo.viewpagerdotsindicator.attacher.ViewPager2Attacher
import com.tbuonomo.viewpagerdotsindicator.attacher.ViewPagerAttacher

abstract class BaseDotsIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        const val DEFAULT_POINT_COLOR = Color.CYAN
    }

    enum class Type(
        val defaultSize: Float,
        val defaultSpacing: Float,
        @StyleableRes val styleableId: IntArray,
        @StyleableRes val dotsColorId: Int,
        @StyleableRes val dotsSizeId: Int,
        @StyleableRes val dotsSpacingId: Int,
        @StyleableRes val dotsCornerRadiusId: Int,
        @StyleableRes val dotsClickableId: Int
    ) {
        DEFAULT(
            16f,
            8f,
            R.styleable.SpringDotsIndicator,
            R.styleable.SpringDotsIndicator_dotsColor,
            R.styleable.SpringDotsIndicator_dotsSize,
            R.styleable.SpringDotsIndicator_dotsSpacing,
            R.styleable.SpringDotsIndicator_dotsCornerRadius,
            R.styleable.SpringDotsIndicator_dotsClickable
        ),
        SPRING(
            16f,
            4f,
            R.styleable.DotsIndicator,
            R.styleable.DotsIndicator_dotsColor,
            R.styleable.DotsIndicator_dotsSize,
            R.styleable.DotsIndicator_dotsSpacing,
            R.styleable.DotsIndicator_dotsCornerRadius,
            R.styleable.SpringDotsIndicator_dotsClickable
        ),
        WORM(
            16f,
            4f,
            R.styleable.WormDotsIndicator,
            R.styleable.WormDotsIndicator_dotsColor,
            R.styleable.WormDotsIndicator_dotsSize,
            R.styleable.WormDotsIndicator_dotsSpacing,
            R.styleable.WormDotsIndicator_dotsCornerRadius,
            R.styleable.SpringDotsIndicator_dotsClickable
        )
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
            dotsClickable = a.getBoolean(type.dotsClickableId, true)

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
        post { refreshDots() }
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
            removeDot()
        }
    }

    fun refreshDots() {
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
        dots.forEach { it.setWidth(dotsSize.toInt()) }
    }

    // ABSTRACT METHODS AND FIELDS

    abstract fun refreshDotColor(index: Int)
    abstract fun addDot(index: Int)
    abstract fun removeDot()
    abstract fun buildOnPageChangedListener(): OnPageChangeListenerHelper
    abstract val type: Type

    // PUBLIC METHODS

    @Deprecated(
        "Use setDotsColors(color) instead", ReplaceWith("setDotsColors(color)")
    )
    fun setPointsColor(color: Int) {
        dotsColor = color
        refreshDotsColors()
    }

    @Deprecated(
        "Use attachTo(viewPager) instead", ReplaceWith("attachTo(viewPager)")
    )
    fun setViewPager(viewPager: ViewPager) {
        ViewPagerAttacher().setup(this, viewPager)
    }

    @Deprecated(
        "Use attachTo(viewPager) instead", ReplaceWith("attachTo(viewPager)")
    )
    fun setViewPager2(viewPager2: ViewPager2) {
        ViewPager2Attacher().setup(this, viewPager2)
    }

    fun attachTo(viewPager: ViewPager) {
        ViewPagerAttacher().setup(this, viewPager)
    }

    fun attachTo(viewPager2: ViewPager2) {
        ViewPager2Attacher().setup(this, viewPager2)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1 && layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            layoutDirection = View.LAYOUT_DIRECTION_LTR
            rotation = 180f
            requestLayout()
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
        post { refreshDots() }
    }
}