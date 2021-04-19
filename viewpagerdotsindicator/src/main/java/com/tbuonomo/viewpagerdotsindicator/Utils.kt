package com.tbuonomo.viewpagerdotsindicator

import android.content.Context
import android.os.Build
import android.provider.Settings

fun getAnimationScale(context: Context): Float {
  return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
    Settings.Global.getFloat(
      context.contentResolver,
      Settings.Global.ANIMATOR_DURATION_SCALE, 1.0f
    )
  } else 1.0f
}