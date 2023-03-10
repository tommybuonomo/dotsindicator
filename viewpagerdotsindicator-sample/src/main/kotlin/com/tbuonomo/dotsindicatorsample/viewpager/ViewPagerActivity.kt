package com.tbuonomo.dotsindicatorsample.viewpager

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.tbuonomo.dotsindicatorsample.core.platform.binding.viewBinding
import com.tbuonomo.dotsindicatorsample.core.platform.viewpager.transformer.ZoomOutPageTransformer
import com.tbuonomo.dotsindicatorsample.databinding.ActivityViewPagerBinding

class ViewPagerActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityViewPagerBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(binding.root)

        with(binding.viewPager) {
            adapter = DotIndicatorPagerAdapter()
            setPageTransformer(true, ZoomOutPageTransformer())

            binding.dotsIndicator.attachTo(this)
            binding.springDotsIndicator.attachTo(this)
            binding.wormDotsIndicator.attachTo(this)
        }
    }
}
