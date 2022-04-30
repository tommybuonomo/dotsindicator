package com.tbuonomo.dotsindicatorsample.viewpager

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.tbuonomo.dotsindicatorsample.R
import com.tbuonomo.dotsindicatorsample.util.ZoomOutPageTransformer
import kotlinx.android.synthetic.main.activity_view_pager.*

class ViewPagerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_view_pager)

        with(view_pager) {
            adapter = DotIndicatorPagerAdapter()
            setPageTransformer(true, ZoomOutPageTransformer())

            dots_indicator.attachTo(this)
            spring_dots_indicator.attachTo(this)
            worm_dots_indicator.attachTo(this)
        }
    }
}
