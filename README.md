# swipe-like-ios

![](https://github.com/linversion/swipe-like-ios/blob/trunk/screenshot.gif)

`swipe-like-ios` builds composables that can be swiped left or right for revealing actions

```groovy
// settings.gradle.kts
repositories {
  maven { setUrl("https://jitpack.io") }
}

// build.gradle.kts
implementation("com.linversion.swipe:swipe-like-ios:latest-version")
```

```kotlin
val archive = SwipeAction(
  icon = rememberVectorPainter(Icons.TwoTone.Archive),
  background = Color.Green,
  onClick = { … }
)

val replyAll = SwipeAction(
  icon = rememberVectorPainter(Icons.TwoTone.ReplyAll),
  background = Color.Perfume,
  onClick = { println("Reply swiped") }
)
  
val snooze = SwipeAction(
  icon = { Text("Snooze") },
  background = Color.Yellow,
  onClick = { … },
)

SwipeableActionsBox(
  startActions = listOf(replyAll),
  endActions = listOf(snooze, archive),
  swipeThreshold = 80.dp,
) {
  // Swipeable content goes here.
}
```

## License

```
Copyright 2024 linversion.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
