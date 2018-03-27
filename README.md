# Material View Pager Dots Indicator

This is a cool Android View Pager Dots Indicator ready to be imported in your project.

![ezgif com-crop](https://user-images.githubusercontent.com/15737675/27997565-f6f18efe-64fa-11e7-9aef-396339c01bd2.gif)
![ezgif com-crop 1](https://user-images.githubusercontent.com/15737675/27997576-4cceb6b2-64fb-11e7-8cc2-d91f28ec2aa4.gif)
![ezgif com-crop 2](https://user-images.githubusercontent.com/15737675/27997585-93e78b96-64fb-11e7-99cb-94e28760ceaf.gif)

## How to
#### Gradle
```Gradle
dependencies {
    implementation 'com.tbuonomo.andrui:viewpagerdotsindicator:1.1.0'
}
```
#### In your XML layout
```Xml
  <com.tbuonomo.viewpagerdotsindicator.DotsIndicator
      android:id="@+id/dots_indicator"
      android:layout_width="wrap_content"
      android:layout_height="wrap_content"
      android:layout_centerHorizontal="true"
      app:dotsColor="@color/colorPrimary"
      app:dotsSize="16dp"
      app:dotsWidthFactor="3"
      />
```

#### In your Java code
```Java
    dotsIndicator = (DotsIndicator) findViewById(R.id.dots_indicator);
    viewPager = (ViewPager) findViewById(R.id.view_pager);
    adapter = new ViewPagerAdapter();
    viewPager.setAdapter(adapter);
    dotsIndicator.setViewPager(viewPager);
```

#### Attributes
| Attribute | Description |
| --- | --- |
| `dotsColor` | Color of the dots |
| `dotsSize` | Size in dp of the dots (by default 8dp) |
| `dotsSpacing` | Size in dp of the space between the dots (by default 4dp) |
| `dotsWidthFactor` | The dots scale factor for page indication (by default 2.5) |

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
