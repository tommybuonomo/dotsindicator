package com.tbuonomo.dotsindicatorsample.viewpager2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tbuonomo.dotsindicatorsample.core.platform.binding.viewBinding
import com.tbuonomo.dotsindicatorsample.core.platform.viewpager.transformer.ZoomOutPageTransformer
import com.tbuonomo.dotsindicatorsample.databinding.ActivityViewPager2Binding

class ViewPager2Activity : AppCompatActivity() {

    private val binding by viewBinding(ActivityViewPager2Binding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding.viewPager2) {
            adapter = DotIndicatorPager2Adapter()

            val zoomOutPageTransformer = ZoomOutPageTransformer()
            setPageTransformer { page, position ->
                zoomOutPageTransformer.transformPage(page, position)
            }

            binding.dotsIndicator.attachTo(this)
            binding.springDotsIndicator.attachTo(this)
            binding.wormDotsIndicator.attachTo(this)
        }
    }
}
