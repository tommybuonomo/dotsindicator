package com.tbuonomo.dotsindicatorsample.ui.compose.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tbuonomo.dotsindicatorsample.ui.compose.component.TextPlaceholderStyle.Description
import com.tbuonomo.dotsindicatorsample.ui.compose.component.TextPlaceholderStyle.Title

sealed interface TextPlaceholderStyle {
    object Title : TextPlaceholderStyle
    object Description : TextPlaceholderStyle
}

@Composable
fun TextPlaceholder(style: TextPlaceholderStyle, endMargin: Dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 4.dp, end = endMargin)
            .height(
                when (style) {
                    Title -> 16.dp
                    Description -> 8.dp
                }
            )
            .background(
                color = when (style) {
                    Title -> Color(0xFF64B5F6)
                    Description -> Color(0xFFA9D1F7)
                },
                shape = RoundedCornerShape(100),
            ),
    )
}