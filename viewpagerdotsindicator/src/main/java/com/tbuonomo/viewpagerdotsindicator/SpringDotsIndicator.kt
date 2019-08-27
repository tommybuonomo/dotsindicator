package com.tbuonomo.viewpagerdotsindicator

import android.content.Context
import android.graphics.drawable.GradientDrawable
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.LinearLayout.HORIZONTAL
import android.widget.RelativeLayout
import com.tbuonomo.viewpagerdotsindicator.BaseDotsIndicator.Type.SPRING

class SpringDotsIndicator @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : BaseDotsIndicator(context, attrs, defStyleAttr) {

  companion object {
    private const val DEFAULT_DAMPING_RATIO = 0.5f
    private const val DEFAULT_STIFFNESS = 300
  }

  private var dotIndicatorView: View? = null

  // Attributes
  private var dotsStrokeWidth: Int = 0
  private var dotsStrokeColor: Int = 0
  private var dotIndicatorColor: Int = 0
  private var stiffness: Float = 0f
  private var dampingRatio: Float = 0f

  private val dotIndicatorSize: Int
  private val dotIndicatorAdditionalSize: Int
  private val horizontalMargin: Int
  private var dotIndicatorSpring: SpringAnimation? = null
  private val strokeDotsLinearLayout: LinearLayout = LinearLayout(context)

  init {

    val linearParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
    horizontalMargin = dpToPx(24)
    linearParams.setMargins(horizontalMargin, 0, horizontalMargin, 0)
    strokeDotsLinearLayout.layoutParams = linearParams
    strokeDotsLinearLayout.orientation = HORIZONTAL
    addView(strokeDotsLinearLayout)

    dotsStrokeWidth = dpToPx(2) // 2dp
    dotIndicatorAdditionalSize = dpToPx(1) // 1dp additional to fill the stroke dots
    dotIndicatorColor = context.getThemePrimaryColor()
    dotsStrokeColor = dotIndicatorColor
    stiffness = DEFAULT_STIFFNESS.toFloat()
    dampingRatio = DEFAULT_DAMPING_RATIO
    dotsClickable = true

    if (attrs != null) {
      val a = getContext().obtainStyledAttributes(attrs, R.styleable.SpringDotsIndicator)

      // Dots attributes
      dotIndicatorColor = a.getColor(R.styleable.SpringDotsIndicator_dotsColor, dotIndicatorColor)
      dotsStrokeColor = a.getColor(R.styleable.SpringDotsIndicator_dotsStrokeColor,
              dotIndicatorColor)
      stiffness = a.getFloat(R.styleable.SpringDotsIndicator_stiffness, stiffness)
      dampingRatio = a.getFloat(R.styleable.SpringDotsIndicator_dampingRatio, dampingRatio)

      // Spring dots attributes
      dotsStrokeWidth = a.getDimension(R.styleable.SpringDotsIndicator_dotsStrokeWidth,
              dotsStrokeWidth.toFloat()).toInt()

      a.recycle()
    }

    dotIndicatorSize = (dotsSize - dotsStrokeWidth * 2 + dotIndicatorAdditionalSize).toInt()

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

    dotIndicatorView = buildDot(false)
    addView(dotIndicatorView)
    dotIndicatorSpring = SpringAnimation(dotIndicatorView, SpringAnimation.TRANSLATION_X)
    val springForce = SpringForce(0f)
    springForce.dampingRatio = dampingRatio
    springForce.stiffness = stiffness
    dotIndicatorSpring!!.spring = springForce
  }

  override fun addDot(index: Int) {
    val dot = buildDot(true)
    dot.setOnClickListener {
      if (dotsClickable && viewPager != null && viewPager!!.adapter != null && index < viewPager!!.adapter!!.count) {
        viewPager!!.setCurrentItem(index, true)
      }
    }

    dots.add(dot.findViewById<View>(R.id.spring_dot) as ImageView)
    strokeDotsLinearLayout.addView(dot)
  }

  private fun buildDot(stroke: Boolean): ViewGroup {
    val dot = LayoutInflater.from(context).inflate(R.layout.spring_dot_layout, this,
            false) as ViewGroup
    val dotView = dot.findViewById<ImageView>(R.id.spring_dot)
    dotView.setBackgroundResource(
            if (stroke) R.drawable.spring_dot_stroke_background else R.drawable.spring_dot_background)
    val params = dotView.layoutParams as RelativeLayout.LayoutParams
    params.height = if (stroke) dotsSize.toInt() else dotIndicatorSize
    params.width = params.height
    params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE)

    params.setMargins(dotsSpacing.toInt(), 0, dotsSpacing.toInt(), 0)

    setUpDotBackground(stroke, dotView)
    return dot
  }

  private fun setUpDotBackground(stroke: Boolean, dotView: View) {
    val dotBackground = dotView.findViewById<View>(R.id.spring_dot).background as GradientDrawable
    if (stroke) {
      dotBackground.setStroke(dotsStrokeWidth, dotsStrokeColor)
    } else {
      dotBackground.setColor(dotIndicatorColor)
    }
    dotBackground.cornerRadius = dotsCornerRadius
  }

  override fun removeDot(index: Int) {
    strokeDotsLinearLayout.removeViewAt(strokeDotsLinearLayout.childCount - 1)
    dots.removeAt(dots.size - 1)
  }

  override fun refreshDotColor(index: Int) {
    setUpDotBackground(true, dots[index])
  }

  override fun buildOnPageChangedListener(): OnPageChangeListener {
    return object : OnPageChangeListener {
      override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        val globalPositionOffsetPixels = position * (dotsSize + dotsSpacing * 2) + (dotsSize + dotsSpacing * 2) *
                positionOffset
        val indicatorTranslationX = globalPositionOffsetPixels + horizontalMargin.toFloat() + dotsStrokeWidth.toFloat() - dotIndicatorAdditionalSize / 2f
        dotIndicatorSpring?.spring?.finalPosition = indicatorTranslationX
        dotIndicatorSpring?.animateToFinalPosition(indicatorTranslationX)
      }

      override fun onPageSelected(position: Int) {}

      override fun onPageScrollStateChanged(state: Int) {}
    };
  }

  override fun getType() = SPRING

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
