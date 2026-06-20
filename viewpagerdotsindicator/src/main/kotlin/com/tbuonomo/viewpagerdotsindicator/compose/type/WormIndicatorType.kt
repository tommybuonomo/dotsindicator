package com.tbuonomo.viewpagerdotsindicator.compose.type

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.tbuonomo.viewpagerdotsindicator.compose.Dot
import com.tbuonomo.viewpagerdotsindicator.compose.dotTestTag
import com.tbuonomo.viewpagerdotsindicator.compose.model.DotGraphic
import kotlin.math.abs
import kotlin.math.floor

class WormIndicatorType(
    private val dotsGraphic: DotGraphic = DotGraphic(),
    private val wormDotGraphic: DotGraphic = DotGraphic(color = Color.White),
) : IndicatorType() {
    @Composable
    override fun IndicatorTypeComposable(
        globalOffsetProvider: () -> Float,
        modifier: Modifier,
        dotCount: Int,
        dotSpacing: Dp,
        onDotClicked: ((Int) -> Unit)?,
    ) {
        val parentLayoutDirection = LocalLayoutDirection.current
        val isRtl = parentLayoutDirection == LayoutDirection.Rtl
        var firstDotPositionX: Float by remember(dotCount) { mutableStateOf(-1f) }
        var lastDotPositionX: Float by remember(dotCount) { mutableStateOf(-1f) }
        // Force LTR on the outer Box so that absoluteOffset uses physical left-to-right coordinates.
        // The LazyRow restores the original direction to keep item order correct in RTL.
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Box(modifier = modifier) {
                CompositionLocalProvider(LocalLayoutDirection provides parentLayoutDirection) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth(), content = {
                            items(dotCount) { dotIndex ->
                                val dotModifier = when (dotIndex) {
                                    0 -> {
                                        Modifier.onGloballyPositioned {
                                            firstDotPositionX = it.positionInParent().x
                                        }
                                    }
                                    dotCount - 1 -> {
                                        Modifier.onGloballyPositioned {
                                            lastDotPositionX = it.positionInParent().x
                                        }
                                    }
                                    else -> Modifier
                                }
                                Dot(dotsGraphic, dotModifier
                                    .testTag(dotTestTag(dotIndex))
                                    .clickable {
                                        onDotClicked?.invoke(dotIndex)
                                    })
                            }
                        }, horizontalArrangement = Arrangement.spacedBy(
                            dotSpacing, alignment = Alignment.CenterHorizontally
                        ),
                        contentPadding = PaddingValues(start = dotSpacing, end = dotSpacing)
                    )
                }
                if (firstDotPositionX != -1f && lastDotPositionX != -1f) {
                    val centeredOffset by remember {
                        derivedStateOf {
                            (dotsGraphic.size - wormDotGraphic.size) / 2
                        }
                    }
                    val density = LocalDensity.current.density
                    val distanceBetween2DotsDp by remember {
                        derivedStateOf {
                            ((abs(lastDotPositionX - firstDotPositionX) / (dotCount - 1)) / density).dp
                        }
                    }
                    val signedDistanceBetween2DotsDp by remember {
                        derivedStateOf {
                            (((lastDotPositionX - firstDotPositionX) / (dotCount - 1)) / density).dp
                        }
                    }
                    val selectorDotWidthDp by remember(lastDotPositionX, firstDotPositionX) {
                        derivedStateOf {
                            distanceBetween2DotsDp + wormDotGraphic.size
                        }
                    }
                    val paddingStartAndEnd: Pair<Dp, Dp> by remember(globalOffsetProvider()) {
                        derivedStateOf {
                            val endPaddingOffset = 1f - ((globalOffsetProvider() % 1.0f) * 2f).coerceIn(0f, 1f)
                            val startPaddingOffset = ((globalOffsetProvider() % 1.0f - 0.5f) * 2f).coerceIn(0f, 1f)
                            val startPadding = distanceBetween2DotsDp * startPaddingOffset
                            val endPadding = distanceBetween2DotsDp * endPaddingOffset
                            startPadding to endPadding
                        }
                    }
                    // In RTL the worm expands toward the left (physical), so start/end padding roles swap.
                    val effectivePaddingStart = if (isRtl) paddingStartAndEnd.second else paddingStartAndEnd.first
                    val effectivePaddingEnd = if (isRtl) paddingStartAndEnd.first else paddingStartAndEnd.second
                    val foregroundDotOffsetDp by remember(globalOffsetProvider) {
                        derivedStateOf {
                            val foregroundDotPositionX =
                                firstDotPositionX + (lastDotPositionX - firstDotPositionX) / (dotCount - 1) * floor(
                                    globalOffsetProvider().toDouble()
                                )
                            // Shift anchor left by one step in RTL so the box spans from dot P+1 to dot P.
                            (foregroundDotPositionX / density).dp + centeredOffset + minOf(0.dp, signedDistanceBetween2DotsDp)
                        }
                    }
                    Dot(
                        wormDotGraphic,
                        Modifier
                            .absoluteOffset(
                                x = foregroundDotOffsetDp,
                                y = centeredOffset
                            )
                            .width(selectorDotWidthDp)
                            .padding(start = effectivePaddingStart, end = effectivePaddingEnd)
                    )
                }
            }
        }
    }
}
