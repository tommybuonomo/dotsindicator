package com.tbuonomo.viewpagerdotsindicator.compose.type

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.tbuonomo.viewpagerdotsindicator.compose.Dot
import com.tbuonomo.viewpagerdotsindicator.compose.model.DotGraphic
import kotlin.math.absoluteValue

class BalloonIndicatorType(
    private val dotsGraphic: DotGraphic = DotGraphic(size = 12.dp),
    private val balloonSizeFactor: Float = 1.5f,
) : IndicatorType() {
    @Composable
    override fun IndicatorTypeComposable(
        globalOffsetProvider: () -> Float,
        modifier: Modifier,
        dotCount: Int,
        dotSpacing: Dp,
        onDotClicked: ((Int) -> Unit)?,
    ) {
        Box(modifier = modifier) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dotsGraphic.size * balloonSizeFactor),
                content = {
                    items(dotCount) { dotIndex ->
                        val dotSize by remember(globalOffsetProvider()) {
                            derivedStateOf { computeDotWidth(dotIndex, globalOffsetProvider()) }
                        }
                        val dotModifier by remember(dotSize) {
                            mutableStateOf(
                                Modifier
                                    .scale(dotSize)
                                    .clickable {
                                        onDotClicked?.invoke(dotIndex)
                                    })
                        }
                        Dot(dotsGraphic, dotModifier)
                    }
                },
                horizontalArrangement = Arrangement.spacedBy(
                    dotSpacing, alignment = Alignment.CenterHorizontally
                ),
                contentPadding = PaddingValues(start = dotSpacing, end = dotSpacing),
                verticalAlignment = Alignment.CenterVertically
            )
        }
    }

    private fun computeDotWidth(currentDotIndex: Int, globalOffset: Float): Float {
        val diffFactor = 1f - (currentDotIndex - globalOffset).absoluteValue.coerceAtMost(1f)
        val sizeToAdd = ((balloonSizeFactor - 1f).coerceAtLeast(0f) * dotsGraphic.size * diffFactor)
        return (dotsGraphic.size + sizeToAdd) / dotsGraphic.size
    }
}