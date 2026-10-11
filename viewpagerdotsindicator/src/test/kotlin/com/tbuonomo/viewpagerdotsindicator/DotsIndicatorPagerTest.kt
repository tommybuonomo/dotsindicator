package com.tbuonomo.viewpagerdotsindicator

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.Robolectric
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import java.time.Duration

@RunWith(ParameterizedRobolectricTestRunner::class)
@Config(sdk = [34])
class DotsIndicatorPagerTest(
    private val pager2: Boolean,
    private val progress: Boolean,
    private val rtl: Boolean,
) {
    @Test fun pageJumps_updatePassedAndFutureDotColors() {
        IndicatorPagerFixture(pager2, progress, rtl).use { fixture ->
            for (page in listOf(3, 1, 4, 0)) {
                fixture.goTo(page)
                for (index in 0..4) {
                    val selected = index == page || progress && index < page
                    val expected = if (selected) Color.BLUE else Color.GRAY
                    if (page == 4 && index == 4) {
                        assertLastPageColor(expected, fixture.color(index))
                    } else {
                        assertEquals("page $page, dot $index", expected, fixture.color(index))
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "ViewPager2={0}, progress={1}, RTL={2}")
        fun parameters(): List<Array<Any>> = listOf(false, true).flatMap { pager2 ->
            listOf(false, true).flatMap { progress ->
                listOf(false, true).map { rtl -> arrayOf(pager2, progress, rtl) }
            }
        }
    }
}

/** Real pager adapters and callbacks; no direct calls to the indicator's listener. */
internal class IndicatorPagerFixture(
    private val pager2: Boolean,
    progress: Boolean,
    rtl: Boolean,
) : AutoCloseable {
    private val controller = Robolectric.buildActivity(Activity::class.java).setup()
    private val activity = controller.get()
    val indicator = DotsIndicator(activity, Robolectric.buildAttributeSet()
        .addAttribute(R.attr.progressMode, progress.toString()).build()).apply {
        dotsColor = Color.GRAY
        selectedDotColor = Color.BLUE
    }
    private var pageCount = 5
    private val root = LinearLayout(activity).apply {
        orientation = LinearLayout.VERTICAL
        layoutDirection = if (rtl) View.LAYOUT_DIRECTION_RTL else View.LAYOUT_DIRECTION_LTR
    }
    private val classicAdapter = object : PagerAdapter() {
        override fun getCount() = pageCount
        override fun isViewFromObject(view: View, item: Any) = view === item
        override fun instantiateItem(container: ViewGroup, position: Int): Any =
            FrameLayout(activity).also { container.addView(it) }
        override fun destroyItem(container: ViewGroup, position: Int, item: Any) {
            container.removeView(item as View)
        }
        override fun getItemPosition(item: Any) = POSITION_NONE
    }
    private val recyclerAdapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun getItemCount() = pageCount
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            object : RecyclerView.ViewHolder(FrameLayout(activity).apply {
                layoutParams = ViewGroup.LayoutParams(-1, -1)
            }) {}
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = Unit
    }
    private val pager: ViewGroup = if (pager2) ViewPager2(activity).apply { adapter = recyclerAdapter }
        else ViewPager(activity).apply { adapter = classicAdapter }

    init {
        root.addView(indicator, LinearLayout.LayoutParams(-1, -2))
        root.addView(pager, LinearLayout.LayoutParams(-1, 0, 1f))
        activity.setContentView(root)
        if (pager2) indicator.attachTo(pager as ViewPager2) else indicator.attachTo(pager as ViewPager)
        controller.visible()
        settle()
    }

    fun settle() {
        repeat(4) {
            root.measure(View.MeasureSpec.makeMeasureSpec(600, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(800, View.MeasureSpec.EXACTLY))
            root.layout(0, 0, 600, 800)
            shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(16))
        }
    }

    fun goTo(page: Int) {
        if (pager2) (pager as ViewPager2).setCurrentItem(page, false)
        else (pager as ViewPager).setCurrentItem(page, false)
        settle()
    }

    fun resize(count: Int) {
        pageCount = count
        if (pager2) recyclerAdapter.notifyDataSetChanged() else classicAdapter.notifyDataSetChanged()
        settle()
    }

    val dotCount: Int get() = (indicator.getChildAt(0) as ViewGroup).childCount
    fun color(index: Int): Int {
        val dot = ((indicator.getChildAt(0) as ViewGroup).getChildAt(index) as ViewGroup).getChildAt(0)
        return (dot.background as GradientDrawable).color!!.defaultColor
    }

    override fun close() { controller.pause().stop().destroy() }
}

// The existing listener uses lastPage - .0001f to keep its interpolation pair in bounds.
// ArgbEvaluator's gamma conversion can leave a two-level difference at that endpoint.
internal fun assertLastPageColor(expected: Int, actual: Int) {
    for (shift in listOf(24, 16, 8, 0)) {
        assertEquals("last-page channel $shift", ((expected ushr shift) and 255).toDouble(),
            ((actual ushr shift) and 255).toDouble(), 2.0)
    }
}
