package com.tbuonomo.dotsindicatorsample.viewpager2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.tbuonomo.dotsindicatorsample.core.platform.binding.viewBinding
import com.tbuonomo.dotsindicatorsample.core.platform.viewpager.transformer.ZoomOutPageTransformer
import com.tbuonomo.dotsindicatorsample.databinding.ActivityViewPager2Binding

class ViewPager2Activity : AppCompatActivity() {

    companion object {
        private const val debugMode = false
    }

    private val binding by viewBinding(ActivityViewPager2Binding::inflate)
    private val dotsIndicatorPager2Adapter = DotIndicatorPager2Adapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding.viewPager2) {
            adapter = dotsIndicatorPager2Adapter

            val zoomOutPageTransformer = ZoomOutPageTransformer()
            setPageTransformer { page, position ->
                zoomOutPageTransformer.transformPage(page, position)
            }

            binding.dotsIndicator.attachTo(this)
            binding.springDotsIndicator.attachTo(this)
            binding.wormDotsIndicator.attachTo(this)
        }

        binding.addButton.isVisible = debugMode
        binding.removeButton.isVisible = debugMode
        binding.usingSmartNotifyCheckBox.isVisible = debugMode

        if (debugMode) {
            binding.addButton.setOnClickListener {
                dotsIndicatorPager2Adapter.addItem(binding.usingSmartNotifyCheckBox.isChecked)
            }
            binding.removeButton.setOnClickListener {
                dotsIndicatorPager2Adapter.removeItem(binding.usingSmartNotifyCheckBox.isChecked)
            }
        }
    }
}
