package com.tbuonomo.viewpagerdotsindicator.compose.type

import androidx.compose.ui.graphics.Color
import com.tbuonomo.viewpagerdotsindicator.compose.DotGraphic

class SpringIndicatorType(
    override val backgroundDots: DotGraphic = DotGraphic(),
    override val foregroundDot: DotGraphic = DotGraphic(color = Color.White),
) : IndicatorType()