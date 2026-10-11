package com.tbuonomo.dotsindicatorsample.ui.viewpager

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tbuonomo.dotsindicatorsample.R
import com.tbuonomo.dotsindicatorsample.core.platform.binding.viewBinding
import com.tbuonomo.dotsindicatorsample.core.platform.viewpager.transformer.ZoomOutPageTransformer
import com.tbuonomo.dotsindicatorsample.databinding.FragmentViewPagerBinding

class DotsIndicatorSampleViewPagerFragment : Fragment() {

    private val binding by viewBinding(FragmentViewPagerBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding.viewPager) {
            adapter = DotIndicatorPagerAdapter()
            setPageTransformer(true, ZoomOutPageTransformer())

            binding.dotsIndicator.attachTo(this)
            binding.springDotsIndicator.attachTo(this)
            binding.wormDotsIndicator.attachTo(this)
        }

        // Per-dot color demo: each dot lights up in its own accent color when selected.
        // Dots without an entry in the array fall back to the global selectedDotColor / dotsColor.
        binding.dotsIndicator.selectedDotColors = intArrayOf(
            Color.parseColor("#FF5252"), // page 0 — red
            Color.parseColor("#FF6D00"), // page 1 — orange
            Color.parseColor("#FFD600"), // page 2 — yellow
            Color.parseColor("#69F0AE"), // page 3 — green
            Color.parseColor("#40C4FF"), // page 4 — blue
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(
            R.layout.fragment_view_pager, container, false
        )
}
