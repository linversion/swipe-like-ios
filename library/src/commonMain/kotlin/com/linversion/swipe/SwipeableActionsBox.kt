package com.linversion.swipe

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation.Horizontal
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * A composable that can be swiped left or right for revealing actions.
 *
 * @param swipeThreshold Minimum drag distance before any [SwipeAction] is
 * activated and can be swiped.
 *
 * @param backgroundUntilSwipeThreshold Color drawn behind the content until
 * [swipeThreshold] is reached. When the threshold is passed, this color is
 * replaced by the currently visible [SwipeAction]'s background.
 */
@Composable
fun SwipeableActionsBox(
  modifier: Modifier = Modifier,
  state: SwipeableActionsState = rememberSwipeableActionsState(),
  startActions: List<SwipeAction> = emptyList(),
  endActions: List<SwipeAction> = emptyList(),
  swipeThreshold: Dp = 40.dp,
  content: @Composable BoxScope.() -> Unit
) = BoxWithConstraints(modifier) {
  state.also {
    it.layoutWidth = constraints.maxWidth
    it.swipeThresholdPx = LocalDensity.current.run { swipeThreshold.toPx() }
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    it.actions = remember(endActions, startActions, isRtl) {
      ActionFinder(
        left = if (isRtl) endActions else startActions,
        right = if (isRtl) startActions else endActions,
      )
    }
  }

  val scope = rememberCoroutineScope()
  val offsetX = state.offset.value.roundToInt()

  Box(
    modifier = Modifier
      .absoluteOffset { IntOffset(x = offsetX, y = 0) }
      .draggable(
        orientation = Horizontal,
        enabled = !state.isResettingOnRelease,
        onDragStopped = {
          scope.launch {
            state.handleOnDragStopped()
          }
        },
        state = state.draggableState,
      ),
    content = content
  )

  val actionWidthDp = LocalDensity.current.run { abs(offsetX).toDp() }

  if (state.actions.right.isNotEmpty() && offsetX < 0) {
    val rightActionOffset = constraints.maxWidth + offsetX

    Row(
      Modifier.absoluteOffset { IntOffset(x = rightActionOffset, y = 0) }.matchParentSize(),
      horizontalArrangement = Arrangement.Start
    ) {
      val actionWidth = actionWidthDp / state.actions.right.size.toFloat()

      for (action in state.actions.right) {
        ActionIconBox(
          action = action,
          actionWidth = actionWidth,
          swipeThreshold = swipeThreshold
        ) {
          if (action.resetAfterClick) {
            scope.launch {
              state.handleReset()
              action.onClick()
            }
          }
        }
      }
    }
  }

  if (state.actions.left.isNotEmpty() && offsetX > 0) {
    val leftActionOffset = -constraints.maxWidth + offsetX

    Row(
      Modifier.absoluteOffset { IntOffset(x = leftActionOffset, y = 0) }.matchParentSize(),
      horizontalArrangement = Arrangement.End
    ) {
      val actionWidth = actionWidthDp / state.actions.left.size.toFloat()

      for (action in state.actions.left) {
        ActionIconBox(
          action = action,
          actionWidth = actionWidth,
          swipeThreshold = swipeThreshold
        ) {
          if (action.resetAfterClick) {
            scope.launch {
              state.handleReset()
              action.onClick()
            }
          }

        }
      }
    }
  }
}

@Composable
private fun ActionIconBox(
  action: SwipeAction,
  actionWidth: Dp,
  swipeThreshold: Dp,
  onClick: () -> Unit
) {
  Box(
    Modifier.width(actionWidth).fillMaxHeight().background(color = action.background).clip(RectangleShape),
    contentAlignment = Alignment.CenterStart
  ) {
    Box(
      modifier = Modifier
        .graphicsLayer {
          translationX = ((swipeThreshold - action.iconSize) / 2).toPx()
        }
        .clipToBounds()
        .pointerInput(Unit) {
          detectTapGestures {
            onClick()
          }
        },
      contentAlignment = Alignment.Center
    ) {
      action.icon()
    }
  }
}

private fun Modifier.drawOverContent(onDraw: DrawScope.() -> Unit): Modifier {
  return drawWithContent {
    drawContent()
    onDraw(this)
  }
}
