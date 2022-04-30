package com.tbuonomo.viewpagerdotsindicator

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.LinearLayout.HORIZONTAL
import android.widget.RelativeLayout
import androidx.dynamicanimation.animation.FloatPropertyCompat
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.tbuonomo.viewpagerdotsindicator.BaseDotsIndicator.Type.WORM

class WormDotsIndicator @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseDotsIndicator(context, attrs, defStyleAttr) {
    private var dotIndicatorView: ImageView? = null
    private var dotIndicatorLayout: View? = null

    // Attributes
    private var dotsStrokeWidth: Float = 0f
    private var dotIndicatorColor: Int = 0
    private var dotsStrokeColor: Int = 0

    private var dotIndicatorXSpring: SpringAnimation? = null
    private var dotIndicatorWidthSpring: SpringAnimation? = null
    private val strokeDotsLinearLayout: LinearLayout = LinearLayout(context)

    init {
        val linearParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val horizontalPadding = dpToPx(24)
        setPadding(horizontalPadding, 0, horizontalPadding, 0)
        clipToPadding = false
        strokeDotsLinearLayout.layoutParams = linearParams
        strokeDotsLinearLayout.orientation = HORIZONTAL
        addView(strokeDotsLinearLayout)

        dotsStrokeWidth = dpToPxF(2f) // 2dp
        dotIndicatorColor = context.getThemePrimaryColor()
        dotsStrokeColor = dotIndicatorColor

        if (attrs != null) {
            val a = getContext().obtainStyledAttributes(attrs, R.styleable.WormDotsIndicator)

            // Dots attributes
            dotIndicatorColor =
                a.getColor(R.styleable.WormDotsIndicator_dotsColor, dotIndicatorColor)
            dotsStrokeColor =
                a.getColor(R.styleable.WormDotsIndicator_dotsStrokeColor, dotIndicatorColor)

            // Spring dots attributes
            dotsStrokeWidth = a.getDimension(
                R.styleable.WormDotsIndicator_dotsStrokeWidth,
                dotsStrokeWidth
            )

            a.recycle()
        }

        if (isInEditMode) {
            addDots(5)
            addView(buildDot(false))
        }

        setUpDotIndicator()
    }

    private fun setUpDotIndicator() {
        if (pager?.isEmpty == true) {
            return
        }

        if (dotIndicatorView != null && indexOfChild(dotIndicatorView) != -1) {
            removeView(dotIndicatorView)
        }

        dotIndicatorLayout = buildDot(false)
        dotIndicatorView = dotIndicatorLayout!!.findViewById(R.id.worm_dot)
        addView(dotIndicatorLayout)
        dotIndicatorXSpring = SpringAnimation(dotIndicatorLayout, SpringAnimation.TRANSLATION_X)
        val springForceX = SpringForce(0f)
        springForceX.dampingRatio = 1f
        springForceX.stiffness = 300f
        dotIndicatorXSpring!!.spring = springForceX

        val floatPropertyCompat = object : FloatPropertyCompat<View>("DotsWidth") {
            override fun getValue(`object`: View): Float {
                return dotIndicatorView!!.layoutParams.width.toFloat()
            }

            override fun setValue(`object`: View, value: Float) {
                val params = dotIndicatorView!!.layoutParams
                params.width = value.toInt()
                dotIndicatorView!!.requestLayout()
            }
        }
        dotIndicatorWidthSpring = SpringAnimation(dotIndicatorLayout, floatPropertyCompat)
        val springForceWidth = SpringForce(0f)
        springForceWidth.dampingRatio = 1f
        springForceWidth.stiffness = 300f
        dotIndicatorWidthSpring!!.spring = springForceWidth
    }

    override fun addDot(index: Int) {
        val dot = buildDot(true)
        dot.setOnClickListener {
            if (dotsClickable && index < pager?.count ?: 0) {
                pager!!.setCurrentItem(index, true)
            }
        }

        dots.add(dot.findViewById<View>(R.id.worm_dot) as ImageView)
        strokeDotsLinearLayout.addView(dot)
    }

    private fun buildDot(stroke: Boolean): ViewGroup {
        val dot = LayoutInflater.from(context).inflate(
            R.layout.worm_dot_layout, this,
            false
        ) as ViewGroup
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) {
            dot.layoutDirection = View.LAYOUT_DIRECTION_LTR
        }
        val dotImageView = dot.findViewById<View>(R.id.worm_dot)
        dotImageView.setBackgroundResource(
            if (stroke) R.drawable.worm_dot_stroke_background else R.drawable.worm_dot_background
        )
        val params = dotImageView.layoutParams as RelativeLayout.LayoutParams
        params.height = dotsSize.toInt()
        params.width = params.height
        params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE)

        params.setMargins(dotsSpacing.toInt(), 0, dotsSpacing.toInt(), 0)

        setUpDotBackground(stroke, dotImageView)
        return dot
    }

    private fun setUpDotBackground(stroke: Boolean, dotImageView: View) {
        val dotBackground = dotImageView.background as GradientDrawable
        if (stroke) {
            dotBackground.setStroke(dotsStrokeWidth.toInt(), dotsStrokeColor)
        } else {
            dotBackground.setColor(dotIndicatorColor)
        }
        dotBackground.cornerRadius = dotsCornerRadius
    }

    override fun refreshDotColor(index: Int) {
        setUpDotBackground(true, dots[index])
    }

    override fun removeDot() {
        strokeDotsLinearLayout.removeViewAt(strokeDotsLinearLayout.childCount - 1)
        dots.removeAt(dots.size - 1)
    }

    override fun buildOnPageChangedListener(): OnPageChangeListenerHelper {
        return object : OnPageChangeListenerHelper() {

            override val pageCount: Int
                get() = dots.size

            override fun onPageScrolled(
                selectedPosition: Int,
                nextPosition: Int,
                positionOffset: Float
            ) {
                val x = (dots[selectedPosition].parent as ViewGroup).left.toFloat()
                val nextX =
                    (dots[if (nextPosition == -1) selectedPosition else nextPosition].parent as ViewGroup).left
                        .toFloat()
                val xFinalPosition: Float
                val widthFinalPosition: Float

                when (positionOffset) {
                    in 0.0f..0.1f -> {
                        xFinalPosition = x
                        widthFinalPosition = dotsSize
                    }
                    in 0.1f..0.9f -> {
                        xFinalPosition = x
                        widthFinalPosition = nextX - x + dotsSize
                    }
                    else -> {
                        xFinalPosition = nextX
                        widthFinalPosition = dotsSize
                    }
                }

                dotIndicatorXSpring?.animateToFinalPosition(xFinalPosition)
                dotIndicatorWidthSpring?.animateToFinalPosition(widthFinalPosition)
            }

            override fun resetPosition(position: Int) {
                // Empty
            }
        }
    }

    override val type get() = WORM

    //*********************************************************
    // Users Methods
    //*********************************************************

    /**
     * Set the indicator dot color.
     *
     * @param color the color fo the indicator dot.
     */
    fun setDotIndicatorColor(color: Int) {
        if (dotIndicatorView != null) {
            dotIndicatorColor = color
            setUpDotBackground(false, dotIndicatorView!!)
        }
    }

    /**
     * Set the stroke indicator dots color.
     *
     * @param color the color fo the stroke indicator dots.
     */
    fun setStrokeDotsIndicatorColor(color: Int) {
        dotsStrokeColor = color
        for (v in dots) {
            setUpDotBackground(true, v)
        }
    }

    /**
     * Set the dots stroke width.
     *
     * @param width the stroke width color for the indicator dots.
     */
    fun setDotsStrokeWidth(width: Float) {
        dotsStrokeWidth = width
        for (v in dots) {
            setUpDotBackground(true, v)
        }
    }
}
