package com.tbuonomo.dotsindicatorsample.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tbuonomo.dotsindicatorsample.core.platform.binding.viewBinding
import com.tbuonomo.dotsindicatorsample.databinding.ActivityDotsIndicatorSampleBinding
import com.tbuonomo.dotsindicatorsample.ui.DotsIndicatorSampleActivity.DotsIndicatorType.*
import com.tbuonomo.dotsindicatorsample.ui.compose.DotsIndicatorSampleComposeFragment
import com.tbuonomo.dotsindicatorsample.ui.viewpager.DotsIndicatorSampleViewPagerFragment
import com.tbuonomo.dotsindicatorsample.ui.viewpager2.DotsIndicatorSampleViewPager2Fragment

class DotsIndicatorSampleActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityDotsIndicatorSampleBinding::inflate)

    sealed interface DotsIndicatorType {
        object ViewPager : DotsIndicatorType
        object ViewPager2 : DotsIndicatorType
        object Compose : DotsIndicatorType
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val type: DotsIndicatorType = Compose

        supportFragmentManager.beginTransaction().add(
            binding.root.id, when (type) {
                ViewPager -> DotsIndicatorSampleViewPagerFragment()
                ViewPager2 -> DotsIndicatorSampleViewPager2Fragment()
                Compose -> DotsIndicatorSampleComposeFragment()
            }
        ).commit()
    }
}