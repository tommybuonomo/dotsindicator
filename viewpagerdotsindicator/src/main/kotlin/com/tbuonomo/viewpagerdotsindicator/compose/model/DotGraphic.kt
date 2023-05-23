package com.tbuonomo.viewpagerdotsindicator.compose.model

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class DotGraphic(
    val size: Dp = 16.dp,
    val color: Color = Color.White,
    val shape: Shape = CircleShape,
    val borderWidth: Dp? = null,
    val borderColor: Color = Color.White,
)