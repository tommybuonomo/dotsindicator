package com.tbuonomo.dotsindicatorsample.ui.viewpager

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
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(
            R.layout.fragment_view_pager, container, false
        )
}
