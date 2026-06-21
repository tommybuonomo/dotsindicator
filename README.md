# Pager Dots Indicator

[![Maven Central](https://img.shields.io/maven-central/v/com.tbuonomo/dotsindicator?label=Maven%20Central)](https://central.sonatype.com/artifact/com.tbuonomo/dotsindicator)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Material%20View%20Pager%20Dots%20Indicator-green.svg?style=flat)](https://android-arsenal.com/details/1/7127)
<a href="https://github.com/JStumpp/awesome-android"><img alt="awesome" src="https://cdn.rawgit.com/sindresorhus/awesome/d7305f38d29fed78fa85652e3a63e154dd8e8829/media/badge.svg" /></a>

Dots Indicator is an Android library for **Jetpack Compose** and classic XML views 🚀

Compose support is built into the same artifact and exposes four indicator types: **Shift**, **Spring**, **Worm**, and **Balloon**.

<img src="https://github.com/tommybuonomo/dotsindicator/assets/15737675/77651550-3819-4fbf-8528-0d28c95d4d07" height="500"/>

Don't forget to star the project if you like it!
![star](https://user-images.githubusercontent.com/15737675/39397370-85f5b294-4afe-11e8-9c02-0dfdf014136a.png)
 == ![heart](https://user-images.githubusercontent.com/15737675/39397367-6e312c2e-4afe-11e8-9fbf-32001b0165a1.png)

Feel free to submit issues and enhancement requests!

## Installation

Add Maven Central and the library dependency:

```kotlin
repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation("com.tbuonomo:dotsindicator:5.1.1")
}
```

## Jetpack Compose

`DotsIndicator` works directly with `androidx.compose.foundation.pager.PagerState`:

```kotlin
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tbuonomo.viewpagerdotsindicator.compose.DotsIndicator
import com.tbuonomo.viewpagerdotsindicator.compose.model.DotGraphic
import com.tbuonomo.viewpagerdotsindicator.compose.type.ShiftIndicatorType

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerWithDots() {
    var pageCount by remember { mutableIntStateOf(5) }
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { pageCount })

    Column {
        HorizontalPager(
            modifier = Modifier.padding(top = 24.dp),
            contentPadding = PaddingValues(horizontal = 64.dp),
            pageSpacing = 24.dp,
            state = pagerState
        ) {
            PagePlaceholderItem()
        }

        DotsIndicator(
            dotCount = pageCount,
            type = ShiftIndicatorType(
                dotsGraphic = DotGraphic(color = MaterialTheme.colorScheme.primary)
            ),
            pagerState = pagerState
        )
    }
}
```

You can also drive the indicator manually with `currentPage`, `currentPageOffsetFraction`, and an optional `onDotClicked` callback.

### ShiftIndicatorType

![ezgif com-crop (1)](https://github.com/tommybuonomo/dotsindicator/assets/15737675/862d7a67-fb78-4767-b5b2-2244dbdbcf31)

```kotlin
DotsIndicator(
    dotCount = pageCount,
    type = ShiftIndicatorType(
        dotsGraphic = DotGraphic(color = MaterialTheme.colorScheme.primary)
    ),
    pagerState = pagerState
)
```

### SpringIndicatorType

![ezgif com-crop (2)](https://github.com/tommybuonomo/dotsindicator/assets/15737675/3c421473-4492-408d-a4cd-a29a6c18ba51)

```kotlin
DotsIndicator(
    dotCount = pageCount,
    type = SpringIndicatorType(
        dotsGraphic = DotGraphic(
            size = 16.dp,
            borderWidth = 2.dp,
            borderColor = MaterialTheme.colorScheme.primary,
            color = Color.Transparent
        ),
        selectorDotGraphic = DotGraphic(
            size = 14.dp,
            color = MaterialTheme.colorScheme.primary
        )
    ),
    pagerState = pagerState
)
```

### WormIndicatorType

![ezgif com-crop (3)](https://github.com/tommybuonomo/dotsindicator/assets/15737675/fdfd8ffc-1581-49f7-8bfd-59f2e118bfac)

```kotlin
DotsIndicator(
    dotCount = pageCount,
    type = WormIndicatorType(
        dotsGraphic = DotGraphic(
            size = 16.dp,
            borderWidth = 2.dp,
            borderColor = MaterialTheme.colorScheme.primary,
            color = Color.Transparent,
        ),
        wormDotGraphic = DotGraphic(
            size = 16.dp,
            color = MaterialTheme.colorScheme.primary,
        )
    ),
    pagerState = pagerState
)
```

### BalloonIndicatorType

![ezgif com-crop (4)](https://github.com/tommybuonomo/dotsindicator/assets/15737675/0687f413-96d4-44ac-8923-0be73d76e2b0)

```kotlin
DotsIndicator(
    dotCount = pageCount,
    type = BalloonIndicatorType(
        dotsGraphic = DotGraphic(
            size = 8.dp,
            color = MaterialTheme.colorScheme.primary
        ),
        balloonSizeFactor = 2f
    ),
    dotSpacing = 20.dp,
    pagerState = pagerState
)
```

## XML views

Classic Android views are still supported for `ViewPager` and `ViewPager2` with `attachTo(...)`.

### DotsIndicator

![ezgif com-crop 1](https://user-images.githubusercontent.com/15737675/38328329-e7008c06-384a-11e8-8449-9f2e396d2bc5.gif) ![ezgif com-crop 3](https://user-images.githubusercontent.com/15737675/38328570-8f1e8230-384b-11e8-9be7-738932a4f85e.gif)

#### XML layout

```xml
<com.tbuonomo.viewpagerdotsindicator.DotsIndicator
    android:id="@+id/dots_indicator"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:dotsColor="@color/material_white"
    app:dotsCornerRadius="8dp"
    app:dotsElevation="4dp"
    app:dotsSize="16dp"
    app:dotsSpacing="4dp"
    app:dotsWidthFactor="2.5"
    app:selectedDotColor="@color/md_blue_200"
    app:progressMode="true"
    app:dotsClickable="true" />
```

#### Custom attributes

| Attribute | Description |
| --- | --- |
| `dotsColor` | Color of the dots |
| `selectedDotColor` | Color of the selected dot (defaults to `dotsColor`) |
| `progressMode` | Colors every previous dot with the selected dot color |
| `dotsSize` | Size of the dots (default: 16dp) |
| `dotsSpacing` | Space between dots (default: 8dp) |
| `dotsWidthFactor` | Width multiplier for the selected dot (default: 2.5) |
| `dotsCornerRadius` | Dot corner radius (defaults to half of `dotsSize`) |
| `dotsElevation` | Elevation applied to the dots |
| `dotsClickable` | Enables changing page when a dot is tapped (default: true) |

#### Kotlin

```kotlin
val dotsIndicator = findViewById<DotsIndicator>(R.id.dots_indicator)
val viewPager = findViewById<ViewPager2>(R.id.view_pager)
viewPager.adapter = ViewPagerAdapter()
dotsIndicator.attachTo(viewPager)
```

### SpringDotsIndicator

![ezgif com-crop 4](https://user-images.githubusercontent.com/15737675/38329136-2c470ef0-384d-11e8-88a8-c8719dc1d0b7.gif) ![ezgif com-crop 5](https://user-images.githubusercontent.com/15737675/38329293-b87f68a4-384d-11e8-8a04-c560c60dac7c.gif)

#### XML layout

```xml
<com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator
    android:id="@+id/spring_dots_indicator"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:dampingRatio="0.5"
    app:dotsColor="@color/material_white"
    app:dotsStrokeColor="@color/material_yellow"
    app:dotsCornerRadius="2dp"
    app:dotsSize="16dp"
    app:dotsSpacing="6dp"
    app:dotsStrokeWidth="2dp"
    app:stiffness="300"
    app:dotsClickable="true" />
```

#### Custom attributes

| Attribute | Description |
| --- | --- |
| `dotsColor` | Color of the moving indicator dot |
| `dotsStrokeColor` | Color of the stroke dots (defaults to the indicator color) |
| `dotsSize` | Size of the dots (default: 16dp) |
| `dotsSpacing` | Space between dots (default: 4dp) |
| `dotsCornerRadius` | Dot corner radius (defaults to half of `dotsSize`) |
| `dotsStrokeWidth` | Stroke width for the background dots (default: 2dp) |
| `dampingRatio` | Spring force damping ratio (default: 0.5) |
| `stiffness` | Spring force stiffness (default: 300) |
| `dotsClickable` | Enables changing page when a dot is tapped (default: true) |

#### Kotlin

```kotlin
val springDotsIndicator = findViewById<SpringDotsIndicator>(R.id.spring_dots_indicator)
val viewPager = findViewById<ViewPager2>(R.id.view_pager)
viewPager.adapter = ViewPagerAdapter()
springDotsIndicator.attachTo(viewPager)
```

### WormDotsIndicator

![ezgif com-crop 6](https://user-images.githubusercontent.com/15737675/38329969-9cf3de2e-384f-11e8-9ada-fa3fbef04d80.gif) ![ezgif com-crop 7](https://user-images.githubusercontent.com/15737675/38330079-f35908fc-384f-11e8-85aa-4daf64c73115.gif)

#### XML layout

```xml
<com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator
    android:id="@+id/worm_dots_indicator"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:dotsColor="@color/material_blueA200"
    app:dotsStrokeColor="@color/material_yellow"
    app:dotsCornerRadius="8dp"
    app:dotsSize="16dp"
    app:dotsSpacing="4dp"
    app:dotsStrokeWidth="2dp"
    app:dotsClickable="true" />
```

#### Custom attributes

| Attribute | Description |
| --- | --- |
| `dotsColor` | Color of the moving indicator dot |
| `dotsStrokeColor` | Color of the stroke dots (defaults to the indicator color) |
| `dotsSize` | Size of the dots (default: 16dp) |
| `dotsSpacing` | Space between dots (default: 4dp) |
| `dotsCornerRadius` | Dot corner radius (defaults to half of `dotsSize`) |
| `dotsStrokeWidth` | Stroke width for the background dots (default: 2dp) |
| `dotsClickable` | Enables changing page when a dot is tapped (default: true) |

#### Kotlin

```kotlin
val wormDotsIndicator = findViewById<WormDotsIndicator>(R.id.worm_dots_indicator)
val viewPager = findViewById<ViewPager2>(R.id.view_pager)
viewPager.adapter = ViewPagerAdapter()
wormDotsIndicator.attachTo(viewPager)
```

## ViewPager and ViewPager2

`attachTo(...)` accepts both `androidx.viewpager.widget.ViewPager` and `androidx.viewpager2.widget.ViewPager2`:

```kotlin
dotsIndicator.attachTo(viewPager)
dotsIndicator.attachTo(viewPager2)
```

## Help Maintenance

If you could help me to continue maintain this repo, buying me a cup of coffee will make my life really happy and get much energy out of it.

<a href="https://www.buymeacoffee.com/tommybuonomo" target="_blank"><img src="https://www.buymeacoffee.com/assets/img/custom_images/purple_img.png" alt="Buy Me A Coffee" style="height: auto !important;width: auto !important;" ></a>

## Changelog
### 5.1.1
- Fix RTL crashes and direction issues across classic and Compose indicators, including Worm and Spring behavior in RTL layouts
- Fix indicator state regressions: RTL rotation now resets correctly when returning to LTR, and fast-scroll dot colors no longer get stuck out of sync
- Add Compose regression coverage for Shift, Balloon, Spring, and Worm indicators with computation, behavior, and screenshot tests in both LTR and RTL
- Fix [#207](https://github.com/tommybuonomo/dotsindicator/issues/207), [#209](https://github.com/tommybuonomo/dotsindicator/issues/209), and [#211](https://github.com/tommybuonomo/dotsindicator/issues/211)
### 5.1.0
- Fix import issues
- Upgrade AGP versions
- Migrate repo to Maven Central Repository
### 5.0
- Add Jetpack Compose support with 4 types: ShiftIndicatorType, SpringIndicatorType, WormIndicatorType, BalloonIndicatorType
### 4.3
- Fix [#144][i144], [#143][i143], [#139][i139], [#135][i135], [#133][i133], [#131][i131], [#126][i126], [#109][i109], [#95][i95], [#93][i93], [#86][i86], [#85][i85], [#80][i80], [#78][i78], [#73][i73], [#68][i68], [#58][i58]
- Methods `setViewPager` and `setViewPager2` are now deprecated and replaced by `attachTo(...)`
### 4.2
Fix [#115](https://github.com/tommybuonomo/dotsindicator/issues/115)
The library is now on MavenCentral.
The library name moves from `com.tbuonomo.andrui:viewpagerdotsindicator` to `com.tbuonomo:dotsindicator`
### 4.1.2
Fix [#55][i55] and [#56][i56]
### 4.1.1
Fix crash
### 4.1
- Support RTL (fix [#32][i32] and [#51][i51])
### 4.0
- Support of ViewPager2 (fix [#40][i40])
- Convert all the project to Kotlin
- Migration to AndroidX
- Fix [#37][i37]: findViewById, causing missing adapter error

### 3.0.3
- Fix [#20][i20]: Dots indicator initialises with the wrong number of dots initially
### 3.0.2
- Add attribute `selectedDotColor` and `progressMode` to `DotsIndicator`
- Fix RTL issues and improve `DotsIndicator` globally
### 2.1.0
- Add attribute `dotsStrokeColor` to `SpringDotsIndicator` and `WormDotsIndicator`

## License
    Copyright 2016 Tommy Buonomo
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
        http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

[i20]: https://github.com/tommybuonomo/dotsindicator/issues/20
[i32]: https://github.com/tommybuonomo/dotsindicator/issues/32
[i37]: https://github.com/tommybuonomo/dotsindicator/issues/37
[i40]: https://github.com/tommybuonomo/dotsindicator/issues/40
[i51]: https://github.com/tommybuonomo/dotsindicator/issues/51
[i55]: https://github.com/tommybuonomo/dotsindicator/issues/55
[i56]: https://github.com/tommybuonomo/dotsindicator/issues/56
[i20]: https://github.com/tommybuonomo/dotsindicator/issues/20
[i32]: https://github.com/tommybuonomo/dotsindicator/issues/32
[i37]: https://github.com/tommybuonomo/dotsindicator/issues/37
[i40]: https://github.com/tommybuonomo/dotsindicator/issues/40
[i51]: https://github.com/tommybuonomo/dotsindicator/issues/51
[i55]: https://github.com/tommybuonomo/dotsindicator/issues/55
[i56]: https://github.com/tommybuonomo/dotsindicator/issues/56
[i144]: https://github.com/tommybuonomo/dotsindicator/issues/144
[i143]: https://github.com/tommybuonomo/dotsindicator/issues/143
[i139]: https://github.com/tommybuonomo/dotsindicator/issues/139
[i135]: https://github.com/tommybuonomo/dotsindicator/issues/135
[i133]: https://github.com/tommybuonomo/dotsindicator/issues/133
[i131]: https://github.com/tommybuonomo/dotsindicator/issues/131
[i126]: https://github.com/tommybuonomo/dotsindicator/issues/126
[i109]: https://github.com/tommybuonomo/dotsindicator/issues/109
[i95]: https://github.com/tommybuonomo/dotsindicator/issues/95
[i93]: https://github.com/tommybuonomo/dotsindicator/issues/93
[i86]: https://github.com/tommybuonomo/dotsindicator/issues/86
[i85]: https://github.com/tommybuonomo/dotsindicator/issues/85
[i80]: https://github.com/tommybuonomo/dotsindicator/issues/80
[i78]: https://github.com/tommybuonomo/dotsindicator/issues/78
[i73]: https://github.com/tommybuonomo/dotsindicator/issues/73
[i68]: https://github.com/tommybuonomo/dotsindicator/issues/68
[i58]: https://github.com/tommybuonomo/dotsindicator/issues/58
