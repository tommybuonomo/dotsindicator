package com.tbuonomo.viewpagerdotsindicator.compose

import com.tbuonomo.viewpagerdotsindicator.compose.type.BalloonIndicatorType
import com.tbuonomo.viewpagerdotsindicator.compose.type.ShiftIndicatorType
import com.tbuonomo.viewpagerdotsindicator.compose.type.SpringIndicatorType
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tier 1 — pure positioning/sizing math. No Compose runtime needed; runs in microseconds.
 * These pin the formulas that drive every indicator's visual so a refactor can't silently
 * shift a dot.
 */
class ComputationsTest {

    private val delta = 0.0001f

    // region computeGlobalScrollOffset (DotsIndicator.kt)

    @Test
    fun globalOffset_atFirstPage_isZero() {
        assertEquals(0f, computeGlobalScrollOffset(position = 0, positionOffset = 0f, totalCount = 5), delta)
    }

    @Test
    fun globalOffset_atMiddlePage_equalsPageIndex() {
        assertEquals(2f, computeGlobalScrollOffset(position = 2, positionOffset = 0f, totalCount = 5), delta)
    }

    @Test
    fun globalOffset_midTransition_addsFraction() {
        assertEquals(2.5f, computeGlobalScrollOffset(position = 2, positionOffset = 0.5f, totalCount = 5), delta)
        assertEquals(3.99f, computeGlobalScrollOffset(position = 3, positionOffset = 0.99f, totalCount = 5), delta)
    }

    @Test
    fun globalOffset_atExactLastPage_isClampedJustBelowLast() {
        // Guard: settling exactly on the last page would put the selector past the last dot.
        val result = computeGlobalScrollOffset(position = 4, positionOffset = 0f, totalCount = 5)
        assertTrue("expected just below 4 but was $result", result < 4f && result > 3.99f)
    }

    @Test
    fun globalOffset_outOfRange_returnsZero() {
        // rightPosition beyond the last index → treated as not-a-real-transition.
        assertEquals(0f, computeGlobalScrollOffset(position = 4, positionOffset = 0.5f, totalCount = 5), delta)
        // negative left position.
        assertEquals(0f, computeGlobalScrollOffset(position = -1, positionOffset = 0f, totalCount = 5), delta)
    }

    @Test
    fun globalOffset_twoPages_transitions() {
        assertEquals(0f, computeGlobalScrollOffset(position = 0, positionOffset = 0f, totalCount = 2), delta)
        assertEquals(0.5f, computeGlobalScrollOffset(position = 0, positionOffset = 0.5f, totalCount = 2), delta)
    }

    // endregion

    // region ShiftIndicatorType.computeDotWidth — default size 16.dp, shiftSizeFactor 3f

    private val shift = ShiftIndicatorType()

    @Test
    fun shift_selectedDot_isWidest() {
        // diffFactor = 1 → 16 + (3-1)*16*1 = 48.dp
        assertEquals(48f, shift.computeDotWidth(currentDotIndex = 2, globalOffset = 2f).value, delta)
    }

    @Test
    fun shift_farDot_isBaseSize() {
        // |index - offset| >= 1 → diffFactor 0 → base 16.dp
        assertEquals(16f, shift.computeDotWidth(currentDotIndex = 0, globalOffset = 2f).value, delta)
    }

    @Test
    fun shift_neighbour_interpolates() {
        // half-way transition → 16 + (3-1)*16*0.5 = 32.dp
        assertEquals(32f, shift.computeDotWidth(currentDotIndex = 2, globalOffset = 2.5f).value, delta)
        assertEquals(32f, shift.computeDotWidth(currentDotIndex = 3, globalOffset = 2.5f).value, delta)
    }

    @Test
    fun shift_isSymmetricAroundSelected() {
        val left = shift.computeDotWidth(currentDotIndex = 1, globalOffset = 2f).value
        val right = shift.computeDotWidth(currentDotIndex = 3, globalOffset = 2f).value
        assertEquals(left, right, delta)
    }

    // endregion

    // region BalloonIndicatorType.computeDotWidth — default size 12.dp, balloonSizeFactor 1.5f

    private val balloon = BalloonIndicatorType()

    @Test
    fun balloon_selectedDot_isMaxScale() {
        // (12 + 0.5*12*1) / 12 = 1.5
        assertEquals(1.5f, balloon.computeDotWidth(currentDotIndex = 2, globalOffset = 2f), delta)
    }

    @Test
    fun balloon_farDot_isBaseScale() {
        assertEquals(1.0f, balloon.computeDotWidth(currentDotIndex = 0, globalOffset = 2f), delta)
    }

    @Test
    fun balloon_neighbour_interpolates() {
        // (12 + 0.5*12*0.5) / 12 = 1.25
        assertEquals(1.25f, balloon.computeDotWidth(currentDotIndex = 2, globalOffset = 2.5f), delta)
    }

    // endregion

    // region SpringIndicatorType.computeSelectorDotPositionDp

    private val spring = SpringIndicatorType()

    @Test
    fun spring_endpoints_mapToFirstAndLastDot() {
        val first = spring.computeSelectorDotPositionDp(
            firstDotPositionX = 0f, lastDotPositionX = 400f, dotCount = 5,
            globalOffset = 0f, density = 1f, centeredOffset = 0.dp
        ).value
        val last = spring.computeSelectorDotPositionDp(
            firstDotPositionX = 0f, lastDotPositionX = 400f, dotCount = 5,
            globalOffset = 4f, density = 1f, centeredOffset = 0.dp
        ).value
        assertEquals(0f, first, delta)
        assertEquals(400f, last, delta)
    }

    @Test
    fun spring_isMonotonicInOffset() {
        var previous = Float.NEGATIVE_INFINITY
        for (step in 0..40) {
            val offset = step / 10f
            val pos = spring.computeSelectorDotPositionDp(
                firstDotPositionX = 0f, lastDotPositionX = 400f, dotCount = 5,
                globalOffset = offset, density = 1f, centeredOffset = 0.dp
            ).value
            assertTrue("position must increase with offset", pos >= previous)
            previous = pos
        }
    }

    @Test
    fun spring_appliesCenteredOffsetAndDensity() {
        // density 2 halves the pixel→dp conversion; centeredOffset is added on top.
        val pos = spring.computeSelectorDotPositionDp(
            firstDotPositionX = 0f, lastDotPositionX = 400f, dotCount = 5,
            globalOffset = 2f, density = 2f, centeredOffset = 4.dp
        ).value
        // foregroundX = 0 + 100*2 = 200 ; /density(2) = 100 ; + 4 = 104
        assertEquals(104f, pos, delta)
    }

    // endregion
}
