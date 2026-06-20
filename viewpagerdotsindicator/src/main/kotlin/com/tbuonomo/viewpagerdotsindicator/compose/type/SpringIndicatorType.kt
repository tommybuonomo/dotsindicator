package com.tbuonomo.viewpagerdotsindicator.compose.type

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.annotation.VisibleForTesting
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

class SpringIndicatorType(
    private val dotsGraphic: DotGraphic = DotGraphic(),
    private val selectorDotGraphic: DotGraphic = DotGraphic(color = Color.Black),
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
        var firstDotPositionX: Float by remember(dotCount, dotsGraphic) { mutableStateOf(-1f) }
        var lastDotPositionX: Float by remember(dotCount, dotsGraphic) { mutableStateOf(-1f) }
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
                            (dotsGraphic.size - selectorDotGraphic.size) / 2
                        }
                    }
                    val density = LocalDensity.current.density
                    val foregroundDotPositionDp by remember(globalOffsetProvider) {
                        derivedStateOf {
                            computeSelectorDotPositionDp(
                                firstDotPositionX,
                                lastDotPositionX,
                                dotCount,
                                globalOffsetProvider(),
                                density,
                                centeredOffset
                            )
                        }
                    }
                    Dot(
                        selectorDotGraphic, Modifier.absoluteOffset(
                            x = foregroundDotPositionDp,
                            y = centeredOffset
                        )
                    )
                }
            }
        }
    }

    @VisibleForTesting
    internal fun computeSelectorDotPositionDp(
        firstDotPositionX: Float,
        lastDotPositionX: Float,
        dotCount: Int,
        globalOffset: Float,
        density: Float,
        centeredOffset: Dp
    ): Dp {
        val foregroundDotPositionX =
            firstDotPositionX + (lastDotPositionX - firstDotPositionX) / (dotCount - 1) * globalOffset
        val foregroundDotPositionDp = (foregroundDotPositionX / density).dp
        return foregroundDotPositionDp + centeredOffset
    }
}
