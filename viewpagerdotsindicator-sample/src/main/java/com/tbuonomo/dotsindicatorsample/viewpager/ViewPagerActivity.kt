package com.tbuonomo.dotsindicatorsample.viewpager

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.tbuonomo.dotsindicatorsample.R
import kotlinx.android.synthetic.main.activity_view_pager.dots_indicator
import kotlinx.android.synthetic.main.activity_view_pager.spring_dots_indicator
import kotlinx.android.synthetic.main.activity_view_pager.view_pager
import kotlinx.android.synthetic.main.activity_view_pager.worm_dots_indicator

class ViewPagerActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requestWindowFeature(Window.FEATURE_NO_TITLE)
    window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
    setContentView(R.layout.activity_view_pager)

    with(view_pager) {
      adapter = DotIndicatorPagerAdapter()
      setPageTransformer(true, ZoomOutPageTransformer())

      dots_indicator.setViewPager(this)
      spring_dots_indicator.setViewPager(this)
      worm_dots_indicator.setViewPager(this)
    }
  }
}
