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
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.tbuonomo.dotsindicatorsample.core.platform.compose.theme.AppTheme
import com.tbuonomo.dotsindicatorsample.ui.compose.component.PagePlaceholderItem
import com.tbuonomo.viewpagerdotsindicator.compose.DotsIndicator
import com.tbuonomo.viewpagerdotsindicator.compose.model.DotGraphic
import com.tbuonomo.viewpagerdotsindicator.compose.type.BalloonIndicatorType
import com.tbuonomo.viewpagerdotsindicator.compose.type.ShiftIndicatorType
import com.tbuonomo.viewpagerdotsindicator.compose.type.SpringIndicatorType
import com.tbuonomo.viewpagerdotsindicator.compose.type.WormIndicatorType

class DotsIndicatorSampleComposeFragment : Fragment() {
    companion object {
        private val ButtonColor = Color(0xFF63A0FF)
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
        AppTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    var pageCount by remember { mutableStateOf(5) }
                    Column {
                        val pagerState = rememberPagerState()
                        HorizontalPager(
                            modifier = Modifier.padding(top = 24.dp),
                            pageCount = pageCount,
                            contentPadding = PaddingValues(horizontal = 64.dp),
                            pageSpacing = 24.dp,
                            state = pagerState
                        ) {
                            PagePlaceholderItem()
                        }
                        ShiftDotIndicators(pageCount, pagerState, Modifier.padding(top = 32.dp))
                        SpringDotIndicators(pageCount, pagerState, Modifier.padding(top = 16.dp))
                        WormDotIndicators(pageCount, pagerState, Modifier.padding(top = 16.dp))
                        BalloonDotIndicators(pageCount, pagerState, Modifier.padding(top = 16.dp))
                    }
                    PageManagementButtons(
                        Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp),
                        onIncrementPage = { pageCount++ }
                    ) { pageCount-- }
                }
            }
        }
    }

    @Composable
    private fun ShiftDotIndicators(pageCount: Int, pagerState: PagerState, modifier: Modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
            Text(
                text = "ShiftIndicatorType",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            DotsIndicator(
                dotCount = pageCount,
                type = ShiftIndicatorType(dotsGraphic = DotGraphic(color = MaterialTheme.colorScheme.primary)),
                pagerState = pagerState
            )
        }
    }

    @Composable
    private fun SpringDotIndicators(pageCount: Int, pagerState: PagerState, modifier: Modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
            Text(
                text = "SpringIndicatorType",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            DotsIndicator(
                dotCount = pageCount,
                type = SpringIndicatorType(
                    dotsGraphic = DotGraphic(
                        16.dp,
                        borderWidth = 2.dp,
                        borderColor = MaterialTheme.colorScheme.primary,
                        color = Color.Transparent
                    ),
                    selectorDotGraphic = DotGraphic(
                        14.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                ),
                pagerState = pagerState
            )
        }
    }

    @Composable
    private fun WormDotIndicators(pageCount: Int, pagerState: PagerState, modifier: Modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
            Text(
                text = "WormIndicatorType",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            DotsIndicator(
                dotCount = pageCount,
                type = WormIndicatorType(
                    dotsGraphic = DotGraphic(
                        16.dp,
                        borderWidth = 2.dp,
                        borderColor = MaterialTheme.colorScheme.primary,
                        color = Color.Transparent,
                    ),
                    wormDotGraphic = DotGraphic(
                        16.dp,
                        color = MaterialTheme.colorScheme.primary,
                    )
                ),
                pagerState = pagerState
            )
        }
    }

    @Composable
    private fun BalloonDotIndicators(pageCount: Int, pagerState: PagerState, modifier: Modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
            Text(
                text = "BalloonIndicatorType",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            DotsIndicator(
                dotCount = pageCount,
                type = BalloonIndicatorType(
                    dotsGraphic = DotGraphic(
                        color = MaterialTheme.colorScheme.primary,
                        size = 8.dp
                    ),
                    balloonSizeFactor = 2f
                ),
                dotSpacing = 20.dp,
                pagerState = pagerState
            )
        }
    }

    @Composable
    private fun PageManagementButtons(
        modifier: Modifier,
        onIncrementPage: () -> Unit,
        onDecrementPage: () -> Unit
    ) {
        Row(modifier = modifier) {
            Button(
                onClick = onIncrementPage,
            ) {
                Text(text = "Add page")
            }
            Spacer(modifier = Modifier.size(16.dp))
            Button(
                onClick = onDecrementPage,
            ) {
                Text(text = "Remove page")
            }
        }
    }
}