package com.tbuonomo.viewpagerdotsindicator.compose.type

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.times
import com.tbuonomo.viewpagerdotsindicator.compose.Dot
import com.tbuonomo.viewpagerdotsindicator.compose.model.DotGraphic
import kotlin.math.absoluteValue

class ShiftIndicatorType(
    private val dotsGraphic: DotGraphic = DotGraphic(),
    private val shiftSizeFactor: Float = 3f,
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
                modifier = Modifier.fillMaxWidth(), content = {
                    items(dotCount) { dotIndex ->
                        val dotWidth by remember(globalOffsetProvider()) {
                            derivedStateOf { computeDotWidth(dotIndex, globalOffsetProvider()) }
                        }
                        val dotModifier by remember(dotWidth) {
                            mutableStateOf(
                                Modifier
                                    .width(dotWidth)
                                    .clickable {
                                        onDotClicked?.invoke(dotIndex)
                                    })
                        }
                        Dot(dotsGraphic, dotModifier)
                    }
                }, horizontalArrangement = Arrangement.spacedBy(
                    dotSpacing, alignment = Alignment.CenterHorizontally
                ),
                contentPadding = PaddingValues(start = dotSpacing, end = dotSpacing)
            )
        }
    }

    private fun computeDotWidth(currentDotIndex: Int, globalOffset: Float): Dp {
        val diffFactor = 1f - (currentDotIndex - globalOffset).absoluteValue.coerceAtMost(1f)
        val widthToAdd = ((shiftSizeFactor - 1f).coerceAtLeast(0f) * dotsGraphic.size * diffFactor)
        return dotsGraphic.size + widthToAdd
    }
}