package com.linversion.swipe

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun rememberSwipeableActionsState(): SwipeableActionsState {
  return remember { SwipeableActionsState() }
}

/**
 * The state of a [SwipeableActionsBox].
 */
@Stable
class SwipeableActionsState internal constructor() {
  /**
   * The current position (in pixels) of a [SwipeableActionsBox].
   */
  val offset: State<Float> get() = offsetState
  internal var offsetState = mutableStateOf(0f)

  /**
   * Whether [SwipeableActionsBox] is currently animating to reset its offset after it was swiped.
   */
  val isResettingOnRelease: Boolean by derivedStateOf {
    swipedAction != null
  }
  private var isAnimating = false
  internal var layoutWidth: Int by mutableIntStateOf(0)
  internal var swipeThresholdPx: Float by mutableFloatStateOf(0f)

  internal var actions: ActionFinder by mutableStateOf(
    ActionFinder(left = emptyList(), right = emptyList())
  )

  internal var swipedAction: SwipeActionMeta? by mutableStateOf(null)

  internal val draggableState = DraggableState { delta ->
    val targetOffset = offsetState.value + delta

    val canSwipeTowardsRight = actions.left.isNotEmpty()
    val canSwipeTowardsLeft = actions.right.isNotEmpty()

    val isAllowed = isResettingOnRelease
      || targetOffset == 0f
      || (targetOffset > 0f && canSwipeTowardsRight)
      || (targetOffset < 0f && canSwipeTowardsLeft)

    val isReachLimit = hasCrossedSwipeLimit()
    offsetState.value += if ((isAllowed && !isReachLimit) || isAnimating) delta else delta / 10
  }

  internal fun hasCrossedSwipeLimit(): Boolean {
    return abs(offsetState.value) > (swipeThresholdPx * if (offsetState.value > 0f) actions.left.size else actions.right.size)
  }

  internal suspend fun handleOnDragStopped() = coroutineScope {
    launch {
      draggableState.drag(MutatePriority.PreventUserInput) {

        val limit = (swipeThresholdPx * if (offsetState.value > 0f) actions.left.size else actions.right.size)
        val isReachLimit = abs(offsetState.value) > (limit * 2) / 3 // 达到2/3既展开 or 处理过渡滑动
        val factor = if (offsetState.value > 0) 1 else -1
        isAnimating = true
        Animatable(offsetState.value).animateTo(
          targetValue = if (isReachLimit) limit * factor else 0f,
          animationSpec = tween(
            durationMillis = if (isReachLimit) animationLimitMs else animationDurationMs,
            easing = LinearEasing
          ),
        ) {
          dragBy(value - offsetState.value)
        }
        isAnimating = false
      }
      swipedAction = null
    }
  }

  internal suspend fun handleReset() = coroutineScope {
    launch {
      draggableState.drag(MutatePriority.PreventUserInput) {
        isAnimating = true
        Animatable(offsetState.value).animateTo(
          targetValue = 0f,
          animationSpec = tween(
            durationMillis = animationDurationMs,
            easing = LinearEasing
          ),
        ) {
          dragBy(value - offsetState.value)
        }
        isAnimating = false
      }
    }
  }
}
