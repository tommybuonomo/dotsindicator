package com.tbuonomo.viewpagerdotsindicator.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.captureRoboImage
import com.tbuonomo.viewpagerdotsindicator.compose.model.DotGraphic
import com.tbuonomo.viewpagerdotsindicator.compose.type.BalloonIndicatorType
import com.tbuonomo.viewpagerdotsindicator.compose.type.IndicatorType
import com.tbuonomo.viewpagerdotsindicator.compose.type.ShiftIndicatorType
import com.tbuonomo.viewpagerdotsindicator.compose.type.SpringIndicatorType
import com.tbuonomo.viewpagerdotsindicator.compose.type.WormIndicatorType
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Tier 3 — golden screenshot tests. Renders each indicator type frozen at a fixed offset,
 * in both LTR and RTL, and compares against committed PNGs in src/test/screenshots/.
 * This is the automated stand-in for the manual "is the selected dot in the right place?".
 *
 *   Record/update goldens: ./gradlew :viewpagerdotsindicator:recordRoborazziDebug
 *   Verify (fail on diff):  ./gradlew :viewpagerdotsindicator:verifyRoborazziDebug
 *   Diffs land in the build/outputs/roborazzi directory as _compare.png images
 */
@RunWith(ParameterizedRobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class DotsIndicatorScreenshotTest(
    private val goldenName: String,
    private val typeFactory: () -> IndicatorType,
    private val layoutDirection: LayoutDirection,
    private val currentPage: Int,
    private val offset: Float,
) {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun capture() {
        composeRule.setContent {
            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                Box(
                    Modifier
                        .background(Color.White)
                        .width(240.dp)
                        .padding(16.dp)
                ) {
                    DotsIndicator(
                        dotCount = DOT_COUNT,
                        type = typeFactory(),
                        currentPage = currentPage,
                        currentPageOffsetFraction = { offset },
                    )
                }
            }
        }
        composeRule.waitForIdle() // let onGloballyPositioned settle the worm/spring overlay
        composeRule.onRoot().captureRoboImage("src/test/screenshots/$goldenName.png")
    }

    companion object {
        private const val DOT_COUNT = 5

        // Defaults are white-on-white; use contrasting colors so the goldens are meaningful.
        private val INACTIVE = Color(0xFFBDBDBD) // gray
        private val ACTIVE = Color(0xFF1565C0)   // blue — easy to spot which dot is selected

        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        fun data(): List<Array<Any>> {
            val types = listOf<Pair<String, () -> IndicatorType>>(
                "shift" to { ShiftIndicatorType(dotsGraphic = DotGraphic(color = ACTIVE)) },
                "balloon" to { BalloonIndicatorType(dotsGraphic = DotGraphic(size = 12.dp, color = ACTIVE)) },
                "spring" to {
                    SpringIndicatorType(
                        dotsGraphic = DotGraphic(color = INACTIVE),
                        selectorDotGraphic = DotGraphic(color = ACTIVE),
                    )
                },
                "worm" to {
                    WormIndicatorType(
                        dotsGraphic = DotGraphic(color = INACTIVE),
                        wormDotGraphic = DotGraphic(color = ACTIVE),
                    )
                },
            )
            val directions = listOf(
                "ltr" to LayoutDirection.Ltr,
                "rtl" to LayoutDirection.Rtl,
            )
            // name, currentPage, offset fraction
            val states = listOf(
                Triple("p0_o0", 0, 0f),     // settled on first page
                Triple("p0_o05", 0, 0.5f),  // mid-transition first→second
                Triple("mid", 2, 0f),       // settled in the middle
                Triple("last", 4, 0f),      // settled on last page
            )
            val params = mutableListOf<Array<Any>>()
            for ((typeName, typeFactory) in types) {
                for ((dirName, direction) in directions) {
                    for ((stateName, page, off) in states) {
                        params += arrayOf(
                            "${typeName}_${dirName}_$stateName",
                            typeFactory,
                            direction,
                            page,
                            off,
                        )
                    }
                }
            }
            return params
        }
    }
}
