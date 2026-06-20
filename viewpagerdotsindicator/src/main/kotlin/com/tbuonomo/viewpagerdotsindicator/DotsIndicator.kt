package com.tbuonomo.viewpagerdotsindicator

import android.animation.ArgbEvaluator
import android.content.Context
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import com.tbuonomo.viewpagerdotsindicator.BaseDotsIndicator.Type.DEFAULT

class DotsIndicator @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseDotsIndicator(context, attrs, defStyleAttr) {

    companion object {
        const val DEFAULT_WIDTH_FACTOR = 2.5f

        /**
         * Returns the color at [index] in [colors], or [fallback] when [colors] is null or
         * [index] is out of range. Pure — no Android context required; safe to call from tests.
         */
        @JvmStatic
        fun resolveColor(colors: IntArray?, index: Int, fallback: Int): Int =
            colors?.getOrElse(index) { fallback } ?: fallback
    }

    private lateinit var linearLayout: LinearLayout
    private var dotsWidthFactor: Float = 0f
    private var progressMode: Boolean = false
    private var dotsElevation: Float = 0f

    var selectedDotColor: Int = 0
        set(value) {
            field = value
            refreshDotsColors()
        }

    /**
     * Per-dot selected colors. Dot at position *i* uses `selectedDotColors[i]`; positions
     * beyond the array length fall back to [selectedDotColor]. `null` (default) uses
     * [selectedDotColor] for every dot — fully backward-compatible.
     *
     * Java: `indicator.setSelectedDotColors(new int[]{Color.RED, Color.GREEN, Color.BLUE});`
     */
    var selectedDotColors: IntArray? = null
        set(value) {
            field = value
            refreshDotsColors()
        }

    /**
     * Per-dot unselected colors. Dot at position *i* uses `dotColors[i]`; positions beyond
     * the array length fall back to [dotsColor]. `null` (default) uses [dotsColor] for every
     * dot — fully backward-compatible.
     *
     * Java: `indicator.setDotColors(new int[]{Color.GRAY, Color.LTGRAY, Color.DKGRAY});`
     */
    var dotColors: IntArray? = null
        set(value) {
            field = value
            refreshDotsColors()
        }

    private val argbEvaluator = ArgbEvaluator()

    private fun getSelectedColorForIndex(index: Int) =
        resolveColor(selectedDotColors, index, selectedDotColor)

    private fun getUnselectedColorForIndex(index: Int) =
        resolveColor(dotColors, index, dotsColor)

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        linearLayout = LinearLayout(context)
        linearLayout.orientation = LinearLayout.HORIZONTAL
        addView(linearLayout, WRAP_CONTENT, WRAP_CONTENT)

        dotsWidthFactor = DEFAULT_WIDTH_FACTOR

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.DotsIndicator)

            selectedDotColor =
                a.getColor(R.styleable.DotsIndicator_selectedDotColor, DEFAULT_POINT_COLOR)

            dotsWidthFactor = a.getFloat(R.styleable.DotsIndicator_dotsWidthFactor, 2.5f)
            if (dotsWidthFactor < 1) {
                Log.w(
                    "DotsIndicator",
                    "The dotsWidthFactor can't be set under 1.0f, please set an higher value"
                )
                dotsWidthFactor = 1f
            }

            progressMode = a.getBoolean(R.styleable.DotsIndicator_progressMode, false)

            dotsElevation = a.getDimension(R.styleable.DotsIndicator_dotsElevation, 0f)

            a.recycle()
        }

        if (isInEditMode) {
            addDots(5)
            refreshDots()
        }

    }

    override fun addDot(index: Int) {
        val dot = LayoutInflater.from(context).inflate(R.layout.dot_layout, this, false)
        val imageView = dot.findViewById<ImageView>(R.id.dot)
        val params = imageView.layoutParams as LayoutParams

        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) {
            dot.layoutDirection = View.LAYOUT_DIRECTION_LTR
        }

        params.height = dotsSize.toInt()
        params.width = params.height
        params.setMargins(dotsSpacing.toInt(), 0, dotsSpacing.toInt(), 0)
        val background = DotsGradientDrawable()
        background.cornerRadius = dotsCornerRadius
        if (isInEditMode) {
            background.setColor(if (0 == index) getSelectedColorForIndex(index) else getUnselectedColorForIndex(index))
        } else {
            background.setColor(if (pager!!.currentItem == index) getSelectedColorForIndex(index) else getUnselectedColorForIndex(index))
        }
        imageView.setBackgroundCompat(background)

        dot.setOnClickListener {
            if (dotsClickable && index < (pager?.count ?: 0)) {
                pager!!.setCurrentItem(index, true)
            }
        }

        dot.setPaddingHorizontal((dotsElevation * 0.8f).toInt())
        dot.setPaddingVertical((dotsElevation * 2).toInt())
        imageView.elevation = dotsElevation

        dots.add(imageView)
        linearLayout.addView(dot)
    }

    override fun removeDot() {
        linearLayout.removeViewAt(linearLayout.childCount - 1)
        dots.removeAt(dots.size - 1)
    }

    override fun buildOnPageChangedListener(): OnPageChangeListenerHelper {
        return object : OnPageChangeListenerHelper() {
            override fun onPageScrolled(
                selectedPosition: Int,
                nextPosition: Int,
                positionOffset: Float
            ) {
                val selectedDot = dots[selectedPosition]
                // Selected dot
                val selectedDotWidth =
                    (dotsSize + dotsSize * (dotsWidthFactor - 1) * (1 - positionOffset)).toInt()
                selectedDot.setWidth(selectedDotWidth)

                if (dots.isInBounds(nextPosition)) {
                    val nextDot = dots[nextPosition]

                    val nextDotWidth =
                        (dotsSize + dotsSize * (dotsWidthFactor - 1) * positionOffset).toInt()
                    nextDot.setWidth(nextDotWidth)

                    val selectedDotBackground = selectedDot.background as DotsGradientDrawable
                    val nextDotBackground = nextDot.background as DotsGradientDrawable

                    val selSelectedColor = getSelectedColorForIndex(selectedPosition)
                    val selUnselectedColor = getUnselectedColorForIndex(selectedPosition)
                    val nxtSelectedColor = getSelectedColorForIndex(nextPosition)
                    val nxtUnselectedColor = getUnselectedColorForIndex(nextPosition)

                    if (selSelectedColor != selUnselectedColor || nxtSelectedColor != nxtUnselectedColor) {
                        val selectedColor = argbEvaluator.evaluate(
                            positionOffset, selSelectedColor, selUnselectedColor
                        ) as Int
                        val nextColor = argbEvaluator.evaluate(
                            positionOffset, nxtUnselectedColor, nxtSelectedColor
                        ) as Int

                        nextDotBackground.setColor(nextColor)

                        if (progressMode && selectedPosition <= pager!!.currentItem) {
                            selectedDotBackground.setColor(selSelectedColor)
                        } else {
                            selectedDotBackground.setColor(selectedColor)
                        }
                    }
                }

                invalidate()
            }

            override fun resetPosition(position: Int) {
                dots[position].setWidth(dotsSize.toInt())
                val elevationItem = dots[position]
                val background = elevationItem.background as? DotsGradientDrawable ?: return
                background.setColor(getUnselectedColorForIndex(position))
                elevationItem.setBackgroundCompat(background)
                elevationItem.invalidate()
            }

            override val pageCount: Int
                get() = dots.size
        }
    }

    override fun refreshDotColor(index: Int) {
        val elevationItem = dots[index]
        val background = elevationItem.background as? DotsGradientDrawable?

        background?.let {
            if (index == pager!!.currentItem || progressMode && index < pager!!.currentItem) {
                background.setColor(getSelectedColorForIndex(index))
            } else {
                background.setColor(getUnselectedColorForIndex(index))
            }
        }

        elevationItem.setBackgroundCompat(background)
        elevationItem.invalidate()
    }

    override val type get() = DEFAULT

    //*********************************************************
    // Users Methods
    //*********************************************************

    @Deprecated("Use setSelectedDotColor() instead", ReplaceWith("setSelectedDotColor()"))
    fun setSelectedPointColor(color: Int) {
        selectedDotColor = color
    }

}
