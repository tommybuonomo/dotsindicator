package com.tbuonomo.viewpagerdotsindicator.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tbuonomo.viewpagerdotsindicator.compose.type.IndicatorType
import com.tbuonomo.viewpagerdotsindicator.compose.type.ShiftIndicatorType
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DotsIndicator(
    dotCount: Int,
    modifier: Modifier = Modifier,
    dotSpacing: Dp = 12.dp,
    type: IndicatorType,
    pagerState: PagerState,
) {
    val coroutineScope = rememberCoroutineScope()
    DotsIndicator(
        dotCount = dotCount,
        modifier = modifier,
        dotSpacing = dotSpacing,
        type = type,
        currentPage = pagerState.currentPage,
        currentPageOffsetFraction = pagerState.currentPageOffsetFraction,
    ) { dotIndex ->
        coroutineScope.launch { pagerState.animateScrollToPage(dotIndex) }
    }
}

@Composable
fun DotsIndicator(
    dotCount: Int,
    modifier: Modifier = Modifier,
    dotSpacing: Dp = 12.dp,
    type: IndicatorType,
    currentPage: Int,
    currentPageOffsetFraction: Float,
    onDotClicked: ((index: Int) -> Unit)? = null
) {
    var globalOffset by remember {
        mutableStateOf(0f)
    }
    globalOffset = computeGlobalScrollOffset(currentPage, currentPageOffsetFraction, dotCount)

    type.IndicatorTypeComposable(globalOffset, modifier, dotCount, dotSpacing, onDotClicked)
}

private fun computeGlobalScrollOffset(position: Int, positionOffset: Float, totalCount: Int): Float {
    var offset = (position + positionOffset)
    val lastPageIndex = (totalCount - 1).toFloat()
    if (offset == lastPageIndex) {
        offset = lastPageIndex - .0001f
    }
    val leftPosition = offset.toInt()
    val rightPosition = leftPosition + 1
    if (rightPosition > lastPageIndex || leftPosition < 0) {
        return 0f
    }

    return leftPosition + offset % 1
}

@Composable
internal fun Dot(
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
            .border(
                width = graphic.borderWidth ?: 0.dp,
                color = graphic.borderColor,
                shape = graphic.shape
            )
    )
}

data class DotGraphic(
    val size: Dp = 16.dp,
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
        type = ShiftIndicatorType(),
        currentPage = 0,
        currentPageOffsetFraction = 0f
    )
}