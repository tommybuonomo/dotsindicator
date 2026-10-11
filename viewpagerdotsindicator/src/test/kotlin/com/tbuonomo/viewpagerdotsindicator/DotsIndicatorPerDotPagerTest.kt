package com.tbuonomo.viewpagerdotsindicator

import android.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(ParameterizedRobolectricTestRunner::class)
@Config(sdk = [34])
class DotsIndicatorPerDotPagerTest(
    private val pager2: Boolean,
    private val progress: Boolean,
    private val rtl: Boolean,
) {
    private val selected = intArrayOf(Color.RED, Color.GREEN, Color.BLUE, Color.MAGENTA, Color.CYAN)
    private val unselected = intArrayOf(Color.DKGRAY, Color.LTGRAY, Color.YELLOW, Color.GRAY, Color.WHITE)

    private fun fixture() = IndicatorPagerFixture(pager2, progress, rtl) {
        selectedDotColors = selected
        dotColors = unselected
    }

    private fun assertPalette(f: IndicatorPagerFixture, page: Int, selectedColors: IntArray?, colors: IntArray?) {
        for (index in 0 until f.dotCount) {
            val isSelected = index == page || progress && index < page
            val expected = if (isSelected) selectedColors?.getOrNull(index) ?: f.indicator.selectedDotColor
                else colors?.getOrNull(index) ?: f.indicator.dotsColor
            if (page == f.dotCount - 1 && index == page && f.dotCount > 1) {
                assertLastPageColor(expected, f.color(index), tolerance = 4.0)
            } else {
                assertEquals("page $page, dot $index", expected, f.color(index))
            }
        }
    }

    @Test fun pageJumps_keepEachDotsOwnSelectedAndUnselectedColors() {
        fixture().use { f ->
            for (page in listOf(3, 1, 4, 0)) {
                f.goTo(page)
                assertPalette(f, page, selected, unselected)
            }
        }
    }

    @Test fun runtimeArrays_partialEmptyAndNullRefreshExistingDots() {
        fixture().use { f ->
            f.goTo(2)
            val partialSelected = intArrayOf(Color.CYAN)
            val partialUnselected = intArrayOf(Color.BLACK, Color.WHITE)
            f.indicator.selectedDotColors = partialSelected
            f.indicator.dotColors = partialUnselected
            assertPalette(f, 2, partialSelected, partialUnselected)
            f.indicator.selectedDotColor = Color.MAGENTA
            f.indicator.dotsColor = Color.YELLOW
            assertPalette(f, 2, partialSelected, partialUnselected)
            f.indicator.selectedDotColors = intArrayOf()
            f.indicator.dotColors = intArrayOf()
            assertPalette(f, 2, null, null)
            f.indicator.selectedDotColors = selected
            f.indicator.dotColors = unselected
            assertPalette(f, 2, selected, unselected)
            f.indicator.selectedDotColors = null
            f.indicator.dotColors = null
            assertPalette(f, 2, null, null)
        }
    }

    @Test fun changingPageCount_preservesPerIndexColorsAndFallbacks() {
        fixture().use { f ->
            f.resize(7)
            f.goTo(5)
            assertEquals(7, f.dotCount)
            assertPalette(f, 5, selected, unselected)
            f.goTo(0)
            for (count in listOf(3, 1, 0, 4)) {
                f.resize(count)
                assertEquals(count, f.dotCount)
                assertPalette(f, 0, selected, unselected)
            }
        }
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "ViewPager2={0}, progress={1}, RTL={2}")
        fun parameters(): List<Array<Any>> = DotsIndicatorPagerTest.parameters()
    }
}
