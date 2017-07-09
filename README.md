# Material Squares Loading Animation

A cool squares loading animation ready to be imported in your project

![ezgif com-crop 1](https://cloud.githubusercontent.com/assets/15737675/24829547/2d1675dc-1c74-11e7-91f9-91614468b751.gif)
![ezgif com-crop](https://cloud.githubusercontent.com/assets/15737675/24829479/aece5dc6-1c72-11e7-87a0-bf34e95f2146.gif)![ezgif com-crop 2](https://cloud.githubusercontent.com/assets/15737675/24878125/27dfaed4-1e32-11e7-8c79-fdd9b6ab537a.gif)

## How to
#### Gradle
```Gradle
dependencies {
    compile 'com.tbuonomo.andrui:materialsquareloading:1.0.0'
}
```
#### In your XML layout
```Xml
    <com.tbuonomo.materialsquareloading.MaterialSquareLoading
        android:id="@+id/material_square_loading_view"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:innerColor="#69F0AE"
        app:outerColor="#3F51B5"
        app:rotationOuterDuration="9850"
        app:rotationInnerDuration="6423"
        app:innerRadius="8dp"
        app:outerRadius="8dp"
        />
```

#### Attributes
| Attribute | Description |
| --- | --- |
| `innerColor` | Color of the inner square |
| `outerColor` | Color of the outer square |
| `rotationOuterDuration` | Step duration of the outer rotation animation in ms |
| `rotationInnerDuration` | Step duration of the inner rotation animation in ms |
| `innerRadius` | Radius in dp of the inner square |
| `innerRadius` | Radius in dp of the outer square |

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
