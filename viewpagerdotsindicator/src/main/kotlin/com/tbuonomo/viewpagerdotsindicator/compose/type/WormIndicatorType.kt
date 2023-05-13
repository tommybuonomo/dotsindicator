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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tbuonomo.viewpagerdotsindicator.compose.Dot
import com.tbuonomo.viewpagerdotsindicator.compose.model.DotGraphic
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
        var firstDotPositionX: Float by remember(dotCount) { mutableStateOf(-1f) }
        var lastDotPositionX: Float by remember(dotCount) { mutableStateOf(-1f) }
        Box(modifier = modifier) {
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
                        Dot(dotsGraphic, dotModifier.clickable {
                            onDotClicked?.invoke(dotIndex)
                        })
                    }
                }, horizontalArrangement = Arrangement.spacedBy(
                    dotSpacing, alignment = Alignment.CenterHorizontally
                ),
                contentPadding = PaddingValues(start = dotSpacing, end = dotSpacing)
            )
            if (firstDotPositionX != -1f && lastDotPositionX != -1f) {
                val centeredOffset by remember {
                    derivedStateOf {
                        (dotsGraphic.size - wormDotGraphic.size) / 2
                    }
                }
                val density = LocalDensity.current.density
                val distanceBetween2DotsDp by remember {
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
                val foregroundDotOffsetDp by remember(globalOffsetProvider) {
                    derivedStateOf {
                        val foregroundDotPositionX =
                            firstDotPositionX + (lastDotPositionX - firstDotPositionX) / (dotCount - 1) * floor(
                                globalOffsetProvider().toDouble()
                            )
                        (foregroundDotPositionX / density).dp + centeredOffset
                    }
                }
                Dot(
                    wormDotGraphic,
                    Modifier
                        .offset(
                            x = foregroundDotOffsetDp,
                            y = centeredOffset
                        )
                        .width(selectorDotWidthDp)
                        .padding(start = paddingStartAndEnd.first, end = paddingStartAndEnd.second)
                )
            }
        }
    }
}