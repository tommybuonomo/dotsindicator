package com.tbuonomo.viewpagerdotsindicator

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.test.core.app.ApplicationProvider
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Tier 2 + Tier 3 tests for the View-based DotsIndicator per-dot color API.
 *
 * Tier 2 — color assertions: verifies that dotColors and selectedDotColors are actually
 * applied to the GradientDrawable backing each dot, including fallback to global colors.
 *
 * Tier 3 — golden screenshots: visually pins the rendered indicator for each color scenario
 * so a future regression in color application can be caught without manual inspection.
 *
 * Record/update goldens: ./gradlew :viewpagerdotsindicator:recordRoborazziDebug
 * Verify (fail on diff):  ./gradlew :viewpagerdotsindicator:verifyRoborazziDebug
 * Diffs land in:          build/outputs/roborazzi/ as *_compare.png images
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class DotsIndicatorViewRenderTest {

    // Contrasting palette — chosen so selected/unselected dots are unambiguous in goldens.
    private val UNSELECTED = Color.parseColor("#BDBDBD") // mid-gray
    private val SELECTED = Color.parseColor("#1565C0")   // deep blue

    // Per-dot arrays sized for 5 dots, each color visually distinct.
    private val PER_DOT_UNSELECTED = intArrayOf(
        Color.parseColor("#F44336"), // red   — dot 0
        Color.parseColor("#4CAF50"), // green — dot 1
        Color.parseColor("#2196F3"), // blue  — dot 2
        Color.parseColor("#FF9800"), // orange— dot 3
        Color.parseColor("#9C27B0"), // purple— dot 4
    )
    private val PER_DOT_SELECTED = intArrayOf(
        Color.parseColor("#B71C1C"), // dark red    — dot 0
        Color.parseColor("#1B5E20"), // dark green  — dot 1
        Color.parseColor("#0D47A1"), // dark blue   — dot 2
        Color.parseColor("#E65100"), // dark orange — dot 3
        Color.parseColor("#4A148C"), // dark purple — dot 4
    )

    // -------------------------------------------------------------------------
    // Color assertion tests — verify drawable colors, no pixel comparison.
    // -------------------------------------------------------------------------

    @Test
    fun globalColors_selectedDotGetsGlobalSelectedColor() {
        val indicator = buildIndicator(count = 5, currentItem = 2)
        assertEquals(SELECTED, dotColor(indicator, 2))
    }

    @Test
    fun globalColors_unselectedDotsGetGlobalDotsColor() {
        val indicator = buildIndicator(count = 5, currentItem = 2)
        for (i in listOf(0, 1, 3, 4)) {
            assertEquals("dot $i should be UNSELECTED", UNSELECTED, dotColor(indicator, i))
        }
    }

    @Test
    fun perDotUnselectedColors_eachUnselectedDotGetsItsOwnColor() {
        val indicator = buildIndicator(
            count = 5, currentItem = 0,
            dotColors = PER_DOT_UNSELECTED,
        )
        // Dot 0 is selected → not covered by dotColors path; check dots 1-4
        for (i in 1..4) {
            assertEquals(
                "dot $i should use PER_DOT_UNSELECTED[$i]",
                PER_DOT_UNSELECTED[i],
                dotColor(indicator, i),
            )
        }
    }

    @Test
    fun perDotUnselectedColors_partialArray_uncoveredDotsUseGlobalColor() {
        val partial = intArrayOf(
            Color.parseColor("#F44336"), // dot 0
            Color.parseColor("#4CAF50"), // dot 1
        )
        val indicator = buildIndicator(count = 5, currentItem = 0, dotColors = partial)
        // Dots 2-4 are beyond the array; they must fall back to global UNSELECTED
        for (i in 2..4) {
            assertEquals("dot $i beyond partial array should use UNSELECTED",
                UNSELECTED, dotColor(indicator, i))
        }
    }

    @Test
    fun perDotSelectedColors_selectedDotGetsItsOwnColor() {
        val indicator = buildIndicator(
            count = 5, currentItem = 3,
            selectedDotColors = PER_DOT_SELECTED,
        )
        assertEquals(PER_DOT_SELECTED[3], dotColor(indicator, 3))
    }

    @Test
    fun perDotSelectedColors_unselectedDotsStillUseGlobalDotsColor() {
        val indicator = buildIndicator(
            count = 5, currentItem = 3,
            selectedDotColors = PER_DOT_SELECTED,
        )
        for (i in listOf(0, 1, 2, 4)) {
            assertEquals("dot $i (unselected) should still use global UNSELECTED",
                UNSELECTED, dotColor(indicator, i))
        }
    }

    @Test
    fun bothArrays_selectedDotUsesSelectedArray_unselectedUsesUnselectedArray() {
        val indicator = buildIndicator(
            count = 5, currentItem = 2,
            dotColors = PER_DOT_UNSELECTED,
            selectedDotColors = PER_DOT_SELECTED,
        )
        assertEquals("dot 2 (selected) should use PER_DOT_SELECTED[2]",
            PER_DOT_SELECTED[2], dotColor(indicator, 2))
        assertEquals("dot 0 (unselected) should use PER_DOT_UNSELECTED[0]",
            PER_DOT_UNSELECTED[0], dotColor(indicator, 0))
        assertEquals("dot 4 (unselected) should use PER_DOT_UNSELECTED[4]",
            PER_DOT_UNSELECTED[4], dotColor(indicator, 4))
    }

    @Test
    fun refreshDotsColors_afterSettingDotColors_updatesExistingDots() {
        val indicator = buildIndicator(count = 3, currentItem = 0)
        // Before: all unselected dots use global UNSELECTED
        assertEquals(UNSELECTED, dotColor(indicator, 1))
        // Set per-dot colors after dots already exist
        indicator.dotColors = intArrayOf(
            Color.parseColor("#F44336"),
            Color.parseColor("#4CAF50"),
            Color.parseColor("#2196F3"),
        )
        // refreshDotsColors() must have re-painted dot 1 (unselected)
        assertEquals(Color.parseColor("#4CAF50"), dotColor(indicator, 1))
    }

    @Test
    fun refreshDotsColors_afterSettingSelectedDotColors_updatesSelectedDot() {
        val indicator = buildIndicator(count = 3, currentItem = 1)
        assertEquals(SELECTED, dotColor(indicator, 1))
        indicator.selectedDotColors = intArrayOf(
            Color.parseColor("#B71C1C"),
            Color.parseColor("#1B5E20"),
            Color.parseColor("#0D47A1"),
        )
        assertEquals(Color.parseColor("#1B5E20"), dotColor(indicator, 1))
    }

    // -------------------------------------------------------------------------
    // Golden screenshot tests — visual pin for each color scenario.
    // -------------------------------------------------------------------------

    @Test
    fun screenshot_globalColors_selectedAtMiddle() {
        captureViewGolden(
            buildIndicator(count = 5, currentItem = 2),
            "dots_view_global_mid"
        )
    }

    @Test
    fun screenshot_perDotUnselectedColors_selectedAtFirst() {
        captureViewGolden(
            buildIndicator(count = 5, currentItem = 0, dotColors = PER_DOT_UNSELECTED),
            "dots_view_per_unselected"
        )
    }

    @Test
    fun screenshot_perDotSelectedColors_selectedAtMiddle() {
        captureViewGolden(
            buildIndicator(count = 5, currentItem = 2, selectedDotColors = PER_DOT_SELECTED),
            "dots_view_per_selected"
        )
    }

    @Test
    fun screenshot_bothPerDotArrays_selectedAtMiddle() {
        captureViewGolden(
            buildIndicator(
                count = 5, currentItem = 2,
                dotColors = PER_DOT_UNSELECTED,
                selectedDotColors = PER_DOT_SELECTED,
            ),
            "dots_view_both_arrays"
        )
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private fun buildIndicator(
        count: Int,
        currentItem: Int,
        dotColors: IntArray? = null,
        selectedDotColors: IntArray? = null,
    ): DotsIndicator {
        val context = ApplicationProvider.getApplicationContext<Context>()
        return DotsIndicator(context).apply {
            dotsColor = UNSELECTED
            selectedDotColor = SELECTED
            this.dotColors = dotColors
            this.selectedDotColors = selectedDotColors
            pager = object : BaseDotsIndicator.Pager {
                override val isNotEmpty = true
                override val currentItem = currentItem
                override val isEmpty = false
                override val count = count
                override fun setCurrentItem(item: Int, smoothScroll: Boolean) = Unit
                override fun removeOnPageChangeListener() = Unit
                override fun addOnPageChangeListener(helper: OnPageChangeListenerHelper) = Unit
            }
            repeat(count) { i -> addDot(i) }
            measure(
                View.MeasureSpec.makeMeasureSpec(480, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            )
            layout(0, 0, measuredWidth, measuredHeight)
        }
    }

    /**
     * Traverses: DotsIndicator → LinearLayout (child 0) → dot FrameLayout (child N) → ImageView.
     * Returns the ARGB color currently set on the dot's GradientDrawable background.
     */
    private fun dotColor(indicator: DotsIndicator, dotIndex: Int): Int {
        val linearLayout = indicator.getChildAt(0) as LinearLayout
        val dotFrame = linearLayout.getChildAt(dotIndex) as FrameLayout
        val imageView = dotFrame.getChildAt(0) as ImageView
        val drawable = imageView.background as GradientDrawable
        return checkNotNull(drawable.color) {
            "dot $dotIndex has no color; was setColor() called?"
        }.defaultColor
    }

    private fun captureViewGolden(view: View, goldenName: String) {
        view.captureRoboImage("src/test/screenshots/$goldenName.png")
    }
}
