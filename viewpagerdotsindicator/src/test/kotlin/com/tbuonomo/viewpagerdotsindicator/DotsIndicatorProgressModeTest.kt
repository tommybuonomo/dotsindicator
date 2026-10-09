package com.tbuonomo.viewpagerdotsindicator

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.ViewGroup
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DotsIndicatorProgressModeTest {
    private val selected = Color.BLUE
    private val unselected = Color.GRAY

    private class TestPager(override var currentItem: Int) : BaseDotsIndicator.Pager {
        override val count = 5
        override val isNotEmpty = true
        override val isEmpty = false
        override fun setCurrentItem(item: Int, smoothScroll: Boolean) { currentItem = item }
        override fun removeOnPageChangeListener() = Unit
        override fun addOnPageChangeListener(onPageChangeListenerHelper: OnPageChangeListenerHelper) = Unit
    }

    private fun indicator(progress: Boolean, currentItem: Int = 0): DotsIndicator {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val attrs = Robolectric.buildAttributeSet()
            .addAttribute(R.attr.progressMode, progress.toString()).build()
        return DotsIndicator(context, attrs).apply {
            dotsColor = unselected
            selectedDotColor = selected
            pager = TestPager(currentItem)
            repeat(5) { addDot(it); refreshDotColor(it) }
        }
    }

    private fun assertColor(indicator: DotsIndicator, index: Int, expected: Int) {
        val row = indicator.getChildAt(0) as ViewGroup
        val dot = (row.getChildAt(index) as ViewGroup).getChildAt(0)
        val actual = (dot.background as GradientDrawable).color!!.defaultColor
        if (index == 4 && expected == selected) {
            assertLastPageColor(expected, actual)
        } else {
            assertEquals("dot $index", expected, actual)
        }
    }

    @Test fun forwardScroll_keepsPassedDotsSelected() {
        val view = indicator(progress = true)
        val listener = view.buildOnPageChangedListener()
        listener.onPageScrolled(0, 0f)
        view.pager!!.setCurrentItem(3, true)
        listener.onPageScrolled(1, .5f)
        listener.onPageScrolled(2, .5f)
        assertColor(view, 0, selected)
        assertColor(view, 1, selected)
    }

    @Test fun forwardJump_fillsEverySkippedDot() {
        val view = indicator(progress = true)
        val listener = view.buildOnPageChangedListener()
        listener.onPageScrolled(0, 0f)
        view.pager!!.setCurrentItem(4, false)
        listener.onPageScrolled(4, 0f)
        for (index in 0..4) assertColor(view, index, selected)
    }

    @Test fun backwardJump_clearsDotsBeyondTheScrollPosition() {
        val view = indicator(progress = true, currentItem = 4)
        val listener = view.buildOnPageChangedListener()
        listener.onPageScrolled(4, 0f)
        view.pager!!.setCurrentItem(1, false)
        listener.onPageScrolled(1, 0f)
        assertColor(view, 0, selected)
        assertColor(view, 1, selected)
        for (index in 2..4) assertColor(view, index, unselected)
    }

    @Test fun forwardScroll_usesScrollPositionWhenPagerDestinationIsBehind() {
        val view = indicator(progress = true)
        val listener = view.buildOnPageChangedListener()
        listener.onPageScrolled(0, .5f)
        // A new destination can be announced before the animation has caught up.
        listener.onPageScrolled(2, .5f)
        assertColor(view, 0, selected)
        assertColor(view, 1, selected)
    }

    @Test fun normalMode_doesNotRehighlightTheAnnouncedDestinationDuringReset() {
        val view = indicator(progress = false, currentItem = 4)
        val listener = view.buildOnPageChangedListener()
        listener.onPageScrolled(4, 0f)
        // Regression guard for #211: currentItem still points to dot 4 during reset.
        listener.onPageScrolled(1, .5f)
        assertColor(view, 4, unselected)
        assertColor(view, 3, unselected)
    }

    @Test fun normalMode_forwardJumpLeavesPassedDotsUnselected() {
        val view = indicator(progress = false)
        val listener = view.buildOnPageChangedListener()
        listener.onPageScrolled(0, 0f)
        view.pager!!.setCurrentItem(4, false)
        listener.onPageScrolled(4, 0f)
        for (index in 0..3) assertColor(view, index, unselected)
        assertColor(view, 4, selected)
    }
}
