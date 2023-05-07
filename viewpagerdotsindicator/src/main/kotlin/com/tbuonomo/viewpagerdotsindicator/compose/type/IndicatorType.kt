package com.tbuonomo.viewpagerdotsindicator.compose.type

import androidx.compose.ui.unit.Dp
import com.tbuonomo.viewpagerdotsindicator.compose.DotGraphic

abstract class IndicatorType {
    abstract val backgroundDots: DotGraphic
    abstract val foregroundDot: DotGraphic?
    open fun computeBackgroundDoWidth(currentDotIndex: Int, globalOffset: Float): Dp? = null
}