# Pager Dots Indicator

![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.tbuonomo/dotsindicator/badge.svg)
[![Android Arsenal]( https://img.shields.io/badge/Android%20Arsenal-Material%20View%20Pager%20Dots%20Indicator-green.svg?style=flat )]( https://android-arsenal.com/details/1/7127 )
<a href="https://github.com/JStumpp/awesome-android"><img alt="awesome" src="https://cdn.rawgit.com/sindresorhus/awesome/d7305f38d29fed78fa85652e3a63e154dd8e8829/media/badge.svg" /></a>

Dots indicator is an Android library available for Jetpack Compose and XML 🚀

<img src="https://github.com/tommybuonomo/dotsindicator/assets/15737675/77651550-3819-4fbf-8528-0d28c95d4d07" height="500"/>


Don't forget to star the project if you like it! 
![star](https://user-images.githubusercontent.com/15737675/39397370-85f5b294-4afe-11e8-9c02-0dfdf014136a.png)
 == ![heart](https://user-images.githubusercontent.com/15737675/39397367-6e312c2e-4afe-11e8-9fbf-32001b0165a1.png)
 
Feel free to submit issues and enhancement requests !

## How to
#### Gradle
```Gradle
repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation("com.tbuonomo:dotsindicator:5.0")
}

```
# For Jetpack Compose
```Kotlin
Column {
    var pageCount by remember { mutableStateOf(5) }
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
    DotsIndicator(
        dotCount = pageCount,
        type = ShiftIndicatorType(dotsGraphic = DotGraphic(color = MaterialTheme.colorScheme.primary)),
        pagerState = pagerState
    )
}
```

4 types are available in JetpackCompose
## ShiftIndicatorType
![ezgif com-crop (1)](https://github.com/tommybuonomo/dotsindicator/assets/15737675/862d7a67-fb78-4767-b5b2-2244dbdbcf31)
```Kotlin
DotsIndicator(
    dotCount = pageCount,
    type = ShiftIndicatorType(dotsGraphic = DotGraphic(color = MaterialTheme.colorScheme.primary)),
    pagerState = pagerState
)
```

## SpringIndicatorType
![ezgif com-crop (2)](https://github.com/tommybuonomo/dotsindicator/assets/15737675/3c421473-4492-408d-a4cd-a29a6c18ba51)
```Kotlin
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
```

## WormIndicatorType
![ezgif com-crop (3)](https://github.com/tommybuonomo/dotsindicator/assets/15737675/fdfd8ffc-1581-49f7-8bfd-59f2e118bfac)
```Kotlin
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
```

## BalloonIndicatorType
![ezgif com-crop (4)](https://github.com/tommybuonomo/dotsindicator/assets/15737675/0687f413-96d4-44ac-8923-0be73d76e2b0)
```Kotlin
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
```
# For XML
## DotsIndicator 
![ezgif com-crop 1](https://user-images.githubusercontent.com/15737675/38328329-e7008c06-384a-11e8-8449-9f2e396d2bc5.gif) ![ezgif com-crop 3](https://user-images.githubusercontent.com/15737675/38328570-8f1e8230-384b-11e8-9be7-738932a4f85e.gif)
#### In your XML layout
```Xml
<com.tbuonomo.viewpagerdotsindicator.DotsIndicator
    android:id="@+id/dots_indicator"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:dotsColor="@color/material_white"
    app:dotsCornerRadius="8dp"
    app:dotsSize="16dp"
    app:dotsSpacing="4dp"
    app:dotsWidthFactor="2.5"
    app:selectedDotColor="@color/md_blue_200"
    app:progressMode="true"
    />
```

#### Custom Attributes
| Attribute | Description |
| --- | --- |
| `dotsColor` | Color of the dots |
| `selectedDotColor` | Color of the selected dot (by default the `dotsColor`) |
| `progressMode` | Lets the selected dot color to the dots behind the current one |
| `dotsSize` | Size in dp of the dots (by default 16dp) |
| `dotsSpacing` | Size in dp of the space between the dots (by default 4dp) |
| `dotsWidthFactor` | The dots scale factor for page indication (by default 2.5) |
| `dotsCornerRadius` | The dots corner radius (by default the half of dotsSize for circularity) |

#### In your Kotlin code
```Kotlin
    val dotsIndicator = findViewById<DotsIndicator>(R.id.dots_indicator)
    val viewPager = findViewById<ViewPager>(R.id.view_pager)
    val adapter = ViewPagerAdapter()
    viewPager.adapter = adapter
    dotsIndicator.attachTo(viewPager)
```


## SpringDotsIndicator 
![ezgif com-crop 4](https://user-images.githubusercontent.com/15737675/38329136-2c470ef0-384d-11e8-88a8-c8719dc1d0b7.gif) ![ezgif com-crop 5](https://user-images.githubusercontent.com/15737675/38329293-b87f68a4-384d-11e8-8a04-c560c60dac7c.gif)
#### In your XML layout
```Xml
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
    />
```

#### Custom Attributes
| Attribute | Description |
| --- | --- |
| `dotsColor` | Color of the indicator dot |
| `dotsStrokeColor` | Color of the stroke dots (by default the indicator color) |
| `dotsSize` | Size in dp of the dots (by default 16dp) |
| `dotsSpacing` | Size in dp of the space between the dots (by default 4dp) |
| `dotsCornerRadius` | The dots corner radius (by default the half of dotsSize for circularity) |
| `dotsStrokeWidth` | The dots stroke width (by default 2dp) |
| `dampingRatio` | The damping ratio of the spring force (by default 0.5) |
| `stiffness` | The stiffness of the spring force (by default 300) |

#### In your Kotlin code
```Kotlin
    val springDotsIndicator = findViewById<SpringDotsIndicator>(R.id.spring_dots_indicator)
    val viewPager = findViewById<ViewPager>(R.id.view_pager)
    val adapter = ViewPagerAdapter()
    viewPager.adapter = adapter
    springDotsIndicator.attachTo(viewPager)
```


## WormDotsIndicator 
![ezgif com-crop 6](https://user-images.githubusercontent.com/15737675/38329969-9cf3de2e-384f-11e8-9ada-fa3fbef04d80.gif) ![ezgif com-crop 7](https://user-images.githubusercontent.com/15737675/38330079-f35908fc-384f-11e8-85aa-4daf64c73115.gif)

#### In your XML layout
```Xml
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
    />
```

#### Custom Attributes
| Attribute | Description |
| --- | --- |
| `dotsColor` | Color of the indicator dot |
| `dotsStrokeColor` | Color of the stroke dots (by default the indicator color) |
| `dotsSize` | Size in dp of the dots (by default 16dp) |
| `dotsSpacing` | Size in dp of the space between the dots (by default 4dp) |
| `dotsCornerRadius` | The dots corner radius (by default the half of dotsSize for circularity) |
| `dotsStrokeWidth` | The dots stroke width (by default 2dp) |

#### In your Kotlin code
```Kotlin
    val wormDotsIndicator = findViewById<WormDotsIndicator>(R.id.worm_dots_indicator)
    val viewPager = findViewById<ViewPager>(R.id.view_pager)
    val adapter = ViewPagerAdapter()
    viewPager.adapter = adapter
    wormDotsIndicator.attachTo(viewPager)
```

## Support of ViewPager2
The `attachTo` can take a `ViewPager` or a `ViewPager2`

## Help Maintenance
If you could help me to continue maintain this repo, buying me a cup of coffee will make my life really happy and get much energy out of it.

<a href="https://www.buymeacoffee.com/tommybuonomo" target="_blank"><img src="https://www.buymeacoffee.com/assets/img/custom_images/purple_img.png" alt="Buy Me A Coffee" style="height: auto !important;width: auto !important;" ></a>

## Changelog
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
