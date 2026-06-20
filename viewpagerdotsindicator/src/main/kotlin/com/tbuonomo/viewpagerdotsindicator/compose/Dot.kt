package com.tbuonomo.viewpagerdotsindicator.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tbuonomo.viewpagerdotsindicator.compose.model.DotGraphic

/**
 * Stable test tag applied to each clickable dot, indexed by its position.
 * Used by UI tests to locate and click a specific dot; inert in production.
 */
fun dotTestTag(index: Int): String = "dotsindicator_dot_$index"

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
            .let {
                graphic.borderWidth?.let { borderWidth ->
                    it.border(
                        width = borderWidth,
                        color = graphic.borderColor,
                        shape = graphic.shape
                    )
                } ?: it
            }
    )
}