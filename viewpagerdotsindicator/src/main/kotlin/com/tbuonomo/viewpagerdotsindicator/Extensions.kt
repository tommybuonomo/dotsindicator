package com.tbuonomo.viewpagerdotsindicator

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2

internal fun View.setPaddingHorizontal(padding: Int) {
    setPadding(padding, paddingTop, padding, paddingBottom)
}

internal fun View.setPaddingVertical(padding: Int) {
    setPadding(paddingLeft, padding, paddingRight, padding)
}

internal fun View.setWidth(width: Int) {
    layoutParams.apply {
        this.width = width
        requestLayout()
    }
}

internal fun <T> ArrayList<T>.isInBounds(index: Int) = index in 0 until size

internal fun Context.getThemePrimaryColor(): Int {
    val value = TypedValue()
    this.theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, value, true)
    return value.data
}

internal val ViewPager.isNotEmpty: Boolean get() = (adapter?.count ?: 0) > 0
internal val ViewPager2.isNotEmpty: Boolean get() = (adapter?.itemCount ?: 0) > 0
internal val ViewPager?.isEmpty: Boolean get() = this?.adapter?.count == 0
internal val ViewPager2?.isEmpty: Boolean get() = this?.adapter?.itemCount == 0

fun View.setBackgroundCompat(background: Drawable?) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        this.background = background
    } else {
        setBackgroundDrawable(background)
    }
}

@Composable
fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }

@Composable
fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }

@Composable
fun Float.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }