package com.tbuonomo.viewpagerdotsindicator

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.support.animation.FloatPropertyCompat
import android.support.animation.SpringAnimation
import android.support.animation.SpringForce
import android.support.v4.view.ViewPager
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.LinearLayout.HORIZONTAL
import android.widget.RelativeLayout

class WormDotsIndicator @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : BaseDotsIndicator(context, attrs, defStyleAttr) {
  private var dotIndicatorView: ImageView? = null
  private var dotIndicatorLayout: View? = null

  // Attributes
  private var dotsStrokeWidth: Int = 0
  private var dotIndicatorColor: Int = 0
  private var dotsStrokeColor: Int = 0

  private val horizontalMargin: Int
  private var dotIndicatorXSpring: SpringAnimation? = null
  private var dotIndicatorWidthSpring: SpringAnimation? = null
  private val strokeDotsLinearLayout: LinearLayout = LinearLayout(context)

  init {
    val linearParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
    horizontalMargin = dpToPx(24)
    linearParams.setMargins(horizontalMargin, 0, horizontalMargin, 0)
    strokeDotsLinearLayout.layoutParams = linearParams
    strokeDotsLinearLayout.orientation = HORIZONTAL
    addView(strokeDotsLinearLayout)

    dotsStrokeWidth = dpToPx(2) // 2dp
    dotIndicatorColor = context.getThemePrimaryColor()
    dotsStrokeColor = dotIndicatorColor

    if (attrs != null) {
      val a = getContext().obtainStyledAttributes(attrs, R.styleable.WormDotsIndicator)

      // Dots attributes
      dotIndicatorColor = a.getColor(R.styleable.WormDotsIndicator_dotsColor, dotIndicatorColor)
      dotsStrokeColor = a.getColor(R.styleable.WormDotsIndicator_dotsStrokeColor, dotIndicatorColor)

      // Spring dots attributes
      dotsStrokeWidth = a.getDimension(R.styleable.WormDotsIndicator_dotsStrokeWidth,
              dotsStrokeWidth.toFloat()).toInt()

      a.recycle()
    }

    if (isInEditMode) {
      addDots(5)
      addView(buildDot(false))
    }

    setUpDotIndicator()
  }

  private fun setUpDotIndicator() {
    if (viewPager != null && viewPager!!.adapter != null && viewPager!!.adapter!!.count == 0) {
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
      if (dotsClickable && viewPager != null && viewPager!!.adapter != null && index < viewPager!!.adapter!!.count) {
        viewPager!!.setCurrentItem(index, true)
      }
    }

    dots.add(dot.findViewById<View>(R.id.worm_dot) as ImageView)
    strokeDotsLinearLayout.addView(dot)
  }

  private fun buildDot(stroke: Boolean): ViewGroup {
    val dot = LayoutInflater.from(context).inflate(R.layout.worm_dot_layout, this,
            false) as ViewGroup
    val dotImageView = dot.findViewById<View>(R.id.worm_dot)
    dotImageView.setBackgroundResource(
            if (stroke) R.drawable.worm_dot_stroke_background else R.drawable.worm_dot_background)
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
      dotBackground.setStroke(dotsStrokeWidth, dotsStrokeColor)
    } else {
      dotBackground.setColor(dotIndicatorColor)
    }
    dotBackground.cornerRadius = dotsCornerRadius.toFloat()
  }

  override fun refreshDotColor(index: Int) {
    setUpDotBackground(true, dots[index])
  }

  override fun removeDot(index: Int) {
    strokeDotsLinearLayout.removeViewAt(strokeDotsLinearLayout.childCount - 1)
    dots.removeAt(dots.size - 1)
  }

  override fun buildOnPageChangedListener(): OnPageChangeListener {
    return object : ViewPager.OnPageChangeListener {
      override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        val stepX = dotsSize + dotsSpacing * 2
        val xFinalPosition: Float
        val widthFinalPosition: Float

        if (positionOffset >= 0 && positionOffset < 0.1f) {
          xFinalPosition = (horizontalMargin + position * stepX).toFloat()
          widthFinalPosition = dotsSize.toFloat()
        } else if (positionOffset >= 0.1f && positionOffset <= 0.9f) {
          xFinalPosition = (horizontalMargin + position * stepX).toFloat()
          widthFinalPosition = (dotsSize + stepX).toFloat()
        } else {
          xFinalPosition = (horizontalMargin + (position + 1) * stepX).toFloat()
          widthFinalPosition = dotsSize.toFloat()
        }

        if (dotIndicatorXSpring!!.spring.finalPosition != xFinalPosition) {
          dotIndicatorXSpring!!.spring.finalPosition = xFinalPosition
        }

        if (dotIndicatorWidthSpring!!.spring.finalPosition != widthFinalPosition) {
          dotIndicatorWidthSpring!!.spring.finalPosition = widthFinalPosition
        }

        if (!dotIndicatorXSpring!!.isRunning) {
          dotIndicatorXSpring!!.start()
        }

        if (!dotIndicatorWidthSpring!!.isRunning) {
          dotIndicatorWidthSpring!!.start()
        }
      }

      override fun onPageSelected(position: Int) {}

      override fun onPageScrollStateChanged(state: Int) {}
    }
  }

  override fun getType() = Type.WORM

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
}
