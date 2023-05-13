package com.tbuonomo.dotsindicatorsample.ui.viewpager2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.tbuonomo.dotsindicatorsample.R
import com.tbuonomo.dotsindicatorsample.core.platform.binding.viewBinding
import com.tbuonomo.dotsindicatorsample.core.platform.viewpager.transformer.ZoomOutPageTransformer
import com.tbuonomo.dotsindicatorsample.databinding.FragmentViewPager2Binding

class DotsIndicatorSampleViewPager2Fragment : Fragment() {

    companion object {
        private const val debugMode = false
    }

    private val binding by viewBinding(FragmentViewPager2Binding::bind)
    private val dotsIndicatorPager2Adapter = DotIndicatorPager2Adapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(
            R.layout.fragment_view_pager2, container, false
        )
}
