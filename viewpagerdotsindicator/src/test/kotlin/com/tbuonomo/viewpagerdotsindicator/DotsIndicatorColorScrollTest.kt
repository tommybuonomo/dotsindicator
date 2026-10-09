package com.tbuonomo.viewpagerdotsindicator

import android.animation.ArgbEvaluator
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
class DotsIndicatorColorScrollTest {
    private val selected = intArrayOf(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN, Color.MAGENTA)
    private val unselected = intArrayOf(Color.YELLOW, Color.CYAN, Color.MAGENTA, Color.RED, Color.GREEN)
    private fun indicator(progress: Boolean = false): DotsIndicator {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val attrs = Robolectric.buildAttributeSet().addAttribute(R.attr.progressMode, "$progress").build()
        return DotsIndicator(context, attrs).apply {
            // Equal globals must not disable interpolation when per-dot endpoints differ.
            dotsColor = Color.GRAY
            selectedDotColor = Color.GRAY
            selectedDotColors = selected
            dotColors = unselected
            pager = object : BaseDotsIndicator.Pager {
                override val isNotEmpty = true
                override val currentItem = 4
                override val isEmpty = false
                override val count = 5
                override fun setCurrentItem(item: Int, smoothScroll: Boolean) = Unit
                override fun removeOnPageChangeListener() = Unit
                override fun addOnPageChangeListener(onPageChangeListenerHelper: OnPageChangeListenerHelper) = Unit
            }
            repeat(5) { addDot(it) }
        }
    }

    private fun color(view: DotsIndicator, index: Int): Int {
        val dot = ((view.getChildAt(0) as ViewGroup).getChildAt(index) as ViewGroup).getChildAt(0)
        return (dot.background as GradientDrawable).color!!.defaultColor
    }

    @Test fun fractionalScroll_usesEachDotsOwnEndpointsEvenWithEqualGlobals() {
        val view = indicator()
        val listener = view.buildOnPageChangedListener()
        for (offset in listOf(0f, .25f, .5f, .75f)) {
            listener.onPageScrolled(0, offset)
            assertEquals(ArgbEvaluator().evaluate(offset, selected[0], unselected[0]), color(view, 0))
            assertEquals(ArgbEvaluator().evaluate(offset, unselected[1], selected[1]), color(view, 1))
        }
    }

    @Test fun forwardReset_usesOwnUnselectedColor() {
        val view = indicator()
        val listener = view.buildOnPageChangedListener()
        listener.onPageScrolled(0, .5f)
        listener.onPageScrolled(2, .5f)
        assertEquals(unselected[0], color(view, 0))
        assertEquals(unselected[1], color(view, 1))
    }

    @Test fun backwardReset_ignoresAnnouncedDestinationAndUsesOwnUnselectedColor() {
        val view = indicator()
        val listener = view.buildOnPageChangedListener()
        listener.onPageScrolled(3, .5f)
        listener.onPageScrolled(0, .5f)
        for (index in 2..4) assertEquals(unselected[index], color(view, index))
    }

    @Test fun progressMode_keepsPassedDotsInTheirOwnSelectedColors() {
        val view = indicator(progress = true)
        val listener = view.buildOnPageChangedListener()
        listener.onPageScrolled(4, 0f)
        listener.onPageScrolled(0, .5f)
        listener.onPageScrolled(2, .5f)
        for (index in 0..2) assertEquals(selected[index], color(view, index))
        listener.onPageScrolled(0, .5f)
        for (index in 2..4) assertEquals(unselected[index], color(view, index))
    }
}
