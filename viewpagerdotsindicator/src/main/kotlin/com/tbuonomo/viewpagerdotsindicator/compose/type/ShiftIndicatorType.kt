package com.tbuonomo.viewpagerdotsindicator.compose.type

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.times
import com.tbuonomo.viewpagerdotsindicator.compose.DotGraphic
import kotlin.math.absoluteValue

class ShiftIndicatorType(
    override val backgroundDots: DotGraphic = DotGraphic(),
    private val shiftSizeFactor: Float = 3f,
) : IndicatorType() {
    override val foregroundDot: DotGraphic? = null

    override fun computeBackgroundDoWidth(currentDotIndex: Int, globalOffset: Float): Dp {
        val diffFactor = 1f - (currentDotIndex - globalOffset).absoluteValue.coerceAtMost(1f)
        val widthToAdd = ((shiftSizeFactor - 1f).coerceAtLeast(0f) * backgroundDots.size * diffFactor)
        return backgroundDots.size + widthToAdd
    }
}