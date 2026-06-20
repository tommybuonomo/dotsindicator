package com.tbuonomo.viewpagerdotsindicator

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Pure-JVM tests for [DotsIndicator.resolveColor] — the color-resolution logic that backs
 * the per-dot selected/unselected color API. No Android context or Robolectric required.
 */
class DotsIndicatorColorTest {

    private val red = 0xFFFF0000.toInt()
    private val green = 0xFF00FF00.toInt()
    private val blue = 0xFF0000FF.toInt()
    private val fallback = 0xFF888888.toInt()

    // region null array

    @Test
    fun nullArray_anyIndex_returnsFallback() {
        assertEquals(fallback, DotsIndicator.resolveColor(null, 0, fallback))
        assertEquals(fallback, DotsIndicator.resolveColor(null, 99, fallback))
    }

    // endregion

    // region empty array

    @Test
    fun emptyArray_anyIndex_returnsFallback() {
        assertEquals(fallback, DotsIndicator.resolveColor(intArrayOf(), 0, fallback))
        assertEquals(fallback, DotsIndicator.resolveColor(intArrayOf(), 5, fallback))
    }

    // endregion

    // region in-bounds indices

    @Test
    fun inBounds_firstIndex_returnsFirstElement() {
        val colors = intArrayOf(red, green, blue)
        assertEquals(red, DotsIndicator.resolveColor(colors, 0, fallback))
    }

    @Test
    fun inBounds_middleIndex_returnsCorrectElement() {
        val colors = intArrayOf(red, green, blue)
        assertEquals(green, DotsIndicator.resolveColor(colors, 1, fallback))
    }

    @Test
    fun inBounds_lastIndex_returnsLastElement() {
        val colors = intArrayOf(red, green, blue)
        assertEquals(blue, DotsIndicator.resolveColor(colors, 2, fallback))
    }

    @Test
    fun inBounds_singleElementArray_returnsElement() {
        assertEquals(red, DotsIndicator.resolveColor(intArrayOf(red), 0, fallback))
    }

    // endregion

    // region out-of-bounds indices

    @Test
    fun outOfBounds_indexEqualsSize_returnsFallback() {
        val colors = intArrayOf(red, green)
        assertEquals(fallback, DotsIndicator.resolveColor(colors, 2, fallback))
    }

    @Test
    fun outOfBounds_largeIndex_returnsFallback() {
        val colors = intArrayOf(red, green, blue)
        assertEquals(fallback, DotsIndicator.resolveColor(colors, 100, fallback))
    }

    // endregion

    // endregion

    // region fallback value is preserved exactly

    @Test
    fun fallback_valueIsReturnedUnchanged() {
        val specificFallback = 0xABCDEF01.toInt()
        assertEquals(specificFallback, DotsIndicator.resolveColor(null, 0, specificFallback))
        assertEquals(specificFallback, DotsIndicator.resolveColor(intArrayOf(), 0, specificFallback))
        assertEquals(specificFallback, DotsIndicator.resolveColor(intArrayOf(red), 5, specificFallback))
    }

    // endregion

    // region per-index selected color behavior paths
    // These tests simulate getSelectedColorForIndex(i) = resolveColor(selectedDotColors, i, selectedDotColor)

    @Test
    fun selectedDotColors_null_allIndicesUseGlobalSelectedColor() {
        val globalSelected = red
        for (i in 0..4) {
            assertEquals(
                "index $i should use global selected color when selectedDotColors is null",
                globalSelected,
                DotsIndicator.resolveColor(null, i, globalSelected)
            )
        }
    }

    @Test
    fun selectedDotColors_partialArray_coveredIndicesUsePerDotColor() {
        val globalSelected = fallback
        val perDot = intArrayOf(red, green) // covers indices 0 and 1 only

        assertEquals(red, DotsIndicator.resolveColor(perDot, 0, globalSelected))
        assertEquals(green, DotsIndicator.resolveColor(perDot, 1, globalSelected))
    }

    @Test
    fun selectedDotColors_partialArray_uncoveredIndicesFallBackToGlobal() {
        val globalSelected = fallback
        val perDot = intArrayOf(red, green) // covers indices 0 and 1; index 2 must fall back

        assertEquals(globalSelected, DotsIndicator.resolveColor(perDot, 2, globalSelected))
        assertEquals(globalSelected, DotsIndicator.resolveColor(perDot, 5, globalSelected))
    }

    @Test
    fun selectedDotColors_fullArray_everyIndexUsesItsOwnColor() {
        val globalSelected = fallback
        val perDot = intArrayOf(red, green, blue)

        assertEquals(red, DotsIndicator.resolveColor(perDot, 0, globalSelected))
        assertEquals(green, DotsIndicator.resolveColor(perDot, 1, globalSelected))
        assertEquals(blue, DotsIndicator.resolveColor(perDot, 2, globalSelected))
    }

    // endregion

    // region per-index unselected color behavior paths
    // These tests simulate getUnselectedColorForIndex(i) = resolveColor(dotColors, i, dotsColor)

    @Test
    fun dotColors_null_allIndicesUseGlobalDotsColor() {
        val globalUnselected = blue
        for (i in 0..4) {
            assertEquals(
                "index $i should use global dots color when dotColors is null",
                globalUnselected,
                DotsIndicator.resolveColor(null, i, globalUnselected)
            )
        }
    }

    @Test
    fun dotColors_partialArray_coveredIndicesUsePerDotColor() {
        val globalUnselected = fallback
        val perDot = intArrayOf(red, green)

        assertEquals(red, DotsIndicator.resolveColor(perDot, 0, globalUnselected))
        assertEquals(green, DotsIndicator.resolveColor(perDot, 1, globalUnselected))
    }

    @Test
    fun dotColors_partialArray_uncoveredIndicesFallBackToGlobal() {
        val globalUnselected = fallback
        val perDot = intArrayOf(red)

        assertEquals(fallback, DotsIndicator.resolveColor(perDot, 1, globalUnselected))
        assertEquals(fallback, DotsIndicator.resolveColor(perDot, 3, globalUnselected))
    }

    @Test
    fun dotColors_singleEntryArray_onlyFirstDotGetsPerDotColor() {
        val globalUnselected = fallback
        val perDot = intArrayOf(blue)

        assertEquals(blue, DotsIndicator.resolveColor(perDot, 0, globalUnselected))
        assertEquals(fallback, DotsIndicator.resolveColor(perDot, 1, globalUnselected))
    }

    // endregion

    // region selected vs unselected distinction for same index

    @Test
    fun sameIndex_selectedAndUnselectedColorsAreIndependent() {
        val selectedColors = intArrayOf(red, green, blue)
        val unselectedColors = intArrayOf(blue, red, green)
        val globalSelected = fallback
        val globalUnselected = 0xFF111111.toInt()

        for (i in 0..2) {
            val resolvedSelected = DotsIndicator.resolveColor(selectedColors, i, globalSelected)
            val resolvedUnselected = DotsIndicator.resolveColor(unselectedColors, i, globalUnselected)
            // They must differ — the arrays are set up so no entry matches at the same index
            assert(resolvedSelected != resolvedUnselected) {
                "index $i: selected ($resolvedSelected) should differ from unselected ($resolvedUnselected)"
            }
        }
    }

    // endregion
}
