@file:OptIn(ExperimentalFoundationApi::class)

package com.tbuonomo.dotsindicatorsample.ui.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.tbuonomo.dotsindicatorsample.ui.compose.component.PagePlaceholderItem
import com.tbuonomo.viewpagerdotsindicator.compose.DotsIndicator
import com.tbuonomo.viewpagerdotsindicator.compose.DotsIndicatorType
import kotlinx.coroutines.launch

class DotsIndicatorSampleComposeFragment : Fragment() {
    companion object {
        private val BackgroundLight = Color(0xFF9CECFB)
        private val BackgroundDark = Color(0xFF0052D4)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent { DotsIndicatorSampleScreen() }
        }
    }

    @Preview
    @Composable
    fun DotsIndicatorSampleScreen() {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                BackgroundLight,
                                BackgroundDark
                            )
                        )
                    )
            ) {
                Column {
                    val pagerState = rememberPagerState()
                    val pageCount = 10
                    HorizontalPager(
                        modifier = Modifier.padding(top = 32.dp),
                        pageCount = pageCount,
                        contentPadding = PaddingValues(horizontal = 32.dp),
                        pageSpacing = 16.dp,
                        state = pagerState
                    ) {
                        PagePlaceholderItem()
                    }
                    val coroutineScope = rememberCoroutineScope()
                    DotsIndicator(
                        dotCount = pageCount,
                        modifier = Modifier.padding(vertical = 64.dp),
                        type = DotsIndicatorType.Shift(),
                        currentPage = pagerState.currentPage,
                        currentPageOffsetFraction = pagerState.currentPageOffsetFraction,
                        onDotClicked = {
                            coroutineScope.launch { pagerState.animateScrollToPage(it) }
                        }
                    )
                }
            }
        }
    }
}