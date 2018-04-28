# Material View Pager Dots Indicator

This library makes it possible to represent View Pager Dots Indicator with 3 different awesome styles !

![ezgif com-optimize](https://user-images.githubusercontent.com/15737675/38327474-981fcae0-3848-11e8-84f9-f3fefcc25ca6.gif)
![ezgif com-crop 8](https://user-images.githubusercontent.com/15737675/38330761-b64c7f3c-3851-11e8-9268-235ba8638467.gif)

## How to
#### Gradle
```Gradle
dependencies {
    implementation 'com.tbuonomo.andrui:viewpagerdotsindicator:2.0.2'
}
```
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
    />
```

#### Custom Attributes
| Attribute | Description |
| --- | --- |
| `dotsColor` | Color of the dots |
| `dotsSize` | Size in dp of the dots (by default 16dp) |
| `dotsSpacing` | Size in dp of the space between the dots (by default 4dp) |
| `dotsWidthFactor` | The dots scale factor for page indication (by default 2.5) |
| `dotsCornerRadius` | The dots corner radius (by default the half of dotsSize for circularity) |

#### In your Java code
```Java
    dotsIndicator = (DotsIndicator) findViewById(R.id.dots_indicator);
    viewPager = (ViewPager) findViewById(R.id.view_pager);
    adapter = new ViewPagerAdapter();
    viewPager.setAdapter(adapter);
    dotsIndicator.setViewPager(viewPager);
```

#### In your Kotlin code
```Kotlin
    val dotsIndicator = findViewById<WormDotsIndicator>(R.id.dots_indicator)
    val viewPager = findViewById<ViewPager>(R.id.view_pager)
    val adapter = ViewPagerAdapter()
    viewPager.adapter = adapter
    dotsIndicator.setViewPager(viewPager)
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
| `dotsColor` | Color of the dots |
| `dotsSize` | Size in dp of the dots (by default 16dp) |
| `dotsSpacing` | Size in dp of the space between the dots (by default 4dp) |
| `dotsCornerRadius` | The dots corner radius (by default the half of dotsSize for circularity) |
| `dotsStrokeWidth` | The dots stroke width (by default 2dp) |
| `dampingRatio` | The damping ratio of the spring force (by default 0.5) |
| `stiffness` | The stiffness of the spring force (by default 300) |

#### In your Java code
```Java
    springDotsIndicator = (SpringDotsIndicator) findViewById(R.id.spring_dots_indicator);
    viewPager = (ViewPager) findViewById(R.id.view_pager);
    adapter = new ViewPagerAdapter();
    viewPager.setAdapter(adapter);
    springDotsIndicator.setViewPager(viewPager);
```

#### In your Kotlin code
```Kotlin
    val springDotsIndicator = findViewById<WormDotsIndicator>(R.id.spring_dots_indicator)
    val viewPager = findViewById<ViewPager>(R.id.view_pager)
    val adapter = ViewPagerAdapter()
    viewPager.adapter = adapter
    springDotsIndicator.setViewPager(viewPager)
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
    app:dotsCornerRadius="8dp"
    app:dotsSize="16dp"
    app:dotsSpacing="4dp"
    app:dotsStrokeWidth="2dp"
    />
```

#### Custom Attributes
| Attribute | Description |
| --- | --- |
| `dotsColor` | Color of the dots |
| `dotsSize` | Size in dp of the dots (by default 16dp) |
| `dotsSpacing` | Size in dp of the space between the dots (by default 4dp) |
| `dotsCornerRadius` | The dots corner radius (by default the half of dotsSize for circularity) |
| `dotsStrokeWidth` | The dots stroke width (by default 2dp) |

#### In your Java code
```Java
    wormDotsIndicator = (WormDotsIndicator) findViewById(R.id.worm_dots_indicator);
    viewPager = (ViewPager) findViewById(R.id.view_pager);
    adapter = new ViewPagerAdapter();
    viewPager.setAdapter(adapter);
    wormDotsIndicator.setViewPager(viewPager);
```

#### In your Kotlin code
```Kotlin
    val wormDotsIndicator = findViewById<WormDotsIndicator>(R.id.worm_dots_indicator)
    val viewPager = findViewById<ViewPager>(R.id.view_pager)
    val adapter = ViewPagerAdapter()
    viewPager.adapter = adapter
    wormDotsIndicator.setViewPager(viewPager)
```

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
