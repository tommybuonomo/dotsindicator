package com.tbuonomo.viewpagerdotsindicator.compose

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.LayoutDirection
import com.tbuonomo.viewpagerdotsindicator.compose.type.BalloonIndicatorType
import com.tbuonomo.viewpagerdotsindicator.compose.type.IndicatorType
import com.tbuonomo.viewpagerdotsindicator.compose.type.ShiftIndicatorType
import com.tbuonomo.viewpagerdotsindicator.compose.type.SpringIndicatorType
import com.tbuonomo.viewpagerdotsindicator.compose.type.WormIndicatorType
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Tier 2 — drives the real composables headlessly (Robolectric + Compose test rule) and
 * asserts behaviour, not pixels. Runs once per indicator type. Covers the manual checks:
 * tapping a dot navigates to its index, add/remove pages in every edge situation, and the
 * recently-fixed RTL crashes.
 */
@RunWith(ParameterizedRobolectricTestRunner::class)
@Config(sdk = [34])
class DotsIndicatorBehaviorTest(
    @Suppress("unused") private val typeName: String,
    private val typeFactory: () -> IndicatorType,
) {

    @get:Rule
    val composeRule = createComposeRule()

    private fun clickableDotCount(): Int =
        composeRule.onAllNodes(hasClickAction()).fetchSemanticsNodes().size

    @Test
    fun tappingDot_invokesCallbackWithThatIndex() {
        var clicked = -1
        composeRule.setContent {
            DotsIndicator(
                dotCount = 5, type = typeFactory(),
                currentPage = 0, currentPageOffsetFraction = { 0f },
                onDotClicked = { clicked = it }
            )
        }
        for (index in intArrayOf(3, 0, 4)) {
            composeRule.onNodeWithTag(dotTestTag(index)).performClick()
            composeRule.runOnIdle { assertEquals(index, clicked) }
        }
    }

    @Test
    fun rendersAllDots_inLtr() {
        composeRule.setContent {
            DotsIndicator(
                dotCount = 5, type = typeFactory(),
                currentPage = 1, currentPageOffsetFraction = { 0.5f }
            )
        }
        composeRule.onAllNodes(hasClickAction()).assertCountEquals(5)
    }

    /** Regression guard for the RTL crashes/inversions fixed in Spring & Worm. */
    @Test
    fun rendersAllDots_inRtl_withoutCrashing() {
        composeRule.setContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                DotsIndicator(
                    dotCount = 5, type = typeFactory(),
                    currentPage = 0, currentPageOffsetFraction = { 0.5f } // exercises worm padding swap
                )
            }
        }
        composeRule.onAllNodes(hasClickAction()).assertCountEquals(5)
    }

    /**
     * Add/remove pages in the situations the user reproduces by hand:
     * current at index 0, current at the last index, and shrinking down to 1 then 0 dots
     * (the latter exercises the (dotCount - 1) divisor in Spring/Worm).
     */
    @Test
    fun addAndRemovePages_edgeCases_noCrashAndCountTracks() {
        val count = mutableIntStateOf(5)
        val page = mutableIntStateOf(0)
        composeRule.setContent {
            var c by count
            var p by page
            DotsIndicator(
                dotCount = c, type = typeFactory(),
                currentPage = p.coerceIn(0, maxOf(0, c - 1)),
                currentPageOffsetFraction = { 0f }
            )
        }

        fun setState(newCount: Int, newPage: Int) {
            composeRule.runOnIdle {
                count.intValue = newCount
                page.intValue = newPage
            }
            composeRule.waitForIdle()
            assertEquals(newCount, clickableDotCount())
        }

        // current at index 0 → add a page, then remove it
        setState(newCount = 6, newPage = 0)
        setState(newCount = 5, newPage = 0)
        // current at the last index → remove a page (current must clamp, no crash)
        setState(newCount = 5, newPage = 4)
        setState(newCount = 4, newPage = 4)
        // shrink down to a single dot, then to none
        setState(newCount = 1, newPage = 0)
        setState(newCount = 0, newPage = 0)
        // grow back from empty
        setState(newCount = 3, newPage = 0)
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        fun types(): List<Array<Any>> = listOf(
            arrayOf("Shift", { ShiftIndicatorType() }),
            arrayOf("Balloon", { BalloonIndicatorType() }),
            arrayOf("Spring", { SpringIndicatorType() }),
            arrayOf("Worm", { WormIndicatorType() }),
        )
    }
}
