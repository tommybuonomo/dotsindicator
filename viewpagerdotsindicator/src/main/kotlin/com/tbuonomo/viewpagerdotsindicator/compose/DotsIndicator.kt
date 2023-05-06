package com.tbuonomo.viewpagerdotsindicator.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.Orientation.Horizontal
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.tbuonomo.viewpagerdotsindicator.OnPageChangeListenerHelper
import com.tbuonomo.viewpagerdotsindicator.compose.DotsIndicatorType.Shift
import kotlin.math.absoluteValue

@Composable
fun DotsIndicator(
    dotCount: Int,
    modifier: Modifier = Modifier,
    dotSpacing: Dp = 12.dp,
    orientation: Orientation = Horizontal,
    type: DotsIndicatorType,
    currentPage: Int,
    currentPageOffsetFraction: Float,
    onDotClicked: ((index: Int) -> Unit)? = null
) {
    var globalOffset by remember { mutableStateOf(0f) }
    val listenerHelper = object : OnPageChangeListenerHelper() {
        override val pageCount = dotCount
        override fun onPageScrolled(selected: Int, next: Int, offset: Float) {
            globalOffset = selected + offset
            println("selectedPosition: $globalOffset")
        }

        override fun resetPosition(position: Int) {
            println("resetPosition: $position")
        }
    }
    listenerHelper.onPageScrolled(currentPage, currentPageOffsetFraction)
    Box(modifier = modifier) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(), content = {
                items(dotCount) { dotIndex ->
                    Dot(type.backgroundDots, getBackgroundDotModifier(type, dotIndex, globalOffset).clickable {
                        onDotClicked?.invoke(dotIndex)
                    })
                }
            }, horizontalArrangement = Arrangement.spacedBy(
                dotSpacing, alignment = Alignment.CenterHorizontally
            ),
            contentPadding = PaddingValues(start = dotSpacing, end = dotSpacing)
        )
    }
}

private fun getBackgroundDotModifier(type: DotsIndicatorType, currentDotIndex: Int, globalOffset: Float): Modifier {
    return when (type) {
        is Shift -> {
            val diffFactor = 1f - (currentDotIndex - globalOffset).absoluteValue.coerceAtMost(1f)
            val widthToAdd = ((type.shiftSizeFactor - 1f).coerceAtLeast(0f) * type.backgroundDots.size * diffFactor)
            val dotWidth = type.backgroundDots.size + widthToAdd
            Modifier.width(dotWidth)
        }
        else -> Modifier
    }
}

@Composable
private fun Dot(
    graphic: DotGraphic,
    modifier: Modifier,
) {
    Box(
        modifier = modifier
            .background(
                color = graphic.color,
                shape = graphic.shape,
            )
            .size(graphic.size)
            .apply {
                graphic.borderWidth?.let { borderWidth ->
                    border(
                        width = borderWidth,
                        color = graphic.borderColor,
                        shape = graphic.shape
                    )
                }
            }
    )
}

sealed interface DotsIndicatorType {
    val backgroundDots: DotGraphic
    val foregroundDot: DotGraphic?

    class Shift(
        override val backgroundDots: DotGraphic = DotGraphic(),
        val shiftSizeFactor: Float = 3f,
    ) : DotsIndicatorType {
        override val foregroundDot: DotGraphic? = null
    }

    class Spring(
        override val backgroundDots: DotGraphic = DotGraphic(),
        override val foregroundDot: DotGraphic = DotGraphic(color = Color.White),
    ) : DotsIndicatorType

    class Worm(
        override val backgroundDots: DotGraphic = DotGraphic(),
        override val foregroundDot: DotGraphic = DotGraphic(color = Color.White),
    ) : DotsIndicatorType
}

data class DotGraphic(
    val size: Dp = 12.dp,
    val color: Color = Color.White,
    val shape: Shape = CircleShape,
    val borderWidth: Dp? = null,
    val borderColor: Color = Color.White,
)

@Preview
@Composable
fun DotsIndicatorPreview() {
    DotsIndicator(
        dotCount = 10,
        dotSpacing = 8.dp,
        type = Shift(),
        currentPage = 0,
        currentPageOffsetFraction = 0f
    )
}