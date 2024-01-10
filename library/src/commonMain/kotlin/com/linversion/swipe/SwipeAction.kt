package com.linversion.swipe

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Represents an action that can be shown in [SwipeableActionsBox].
 *
 * @param background Color used as the background of [SwipeableActionsBox] while
 * this action is visible. If this action is swiped, its background color is
 * also used for drawing a ripple over the content for providing a visual
 * feedback to the user.
 *
 * @param weight The proportional width to give to this element, as related
 * to the total of all weighted siblings. [SwipeableActionsBox] will divide its
 * horizontal space and distribute it to actions according to their weight.
 *
 * @param isUndo Determines the direction in which a ripple is drawn when this
 * action is swiped. When false, the ripple grows from this action's position
 * to consume the entire composable, and vice versa. This can be used for
 * actions that can be toggled on and off.
 */
class SwipeAction(
  val onClick: () -> Unit,
  val icon: @Composable () -> Unit,
  val background: Color,
  val iconSize: Dp,
  val resetAfterClick: Boolean
) {

  fun copy(
    onClick: () -> Unit = this.onClick,
    icon: @Composable () -> Unit = this.icon,
    iconSize: Dp = this.iconSize,
    background: Color = this.background,
    resetAfterClick: Boolean = this.resetAfterClick
  ) = SwipeAction(
    onClick = onClick,
    icon = icon,
    iconSize = iconSize,
    background = background,
    resetAfterClick = resetAfterClick
  )
}

/**
 * See [SwipeAction] for documentation.
 */
fun SwipeAction(
  onClick: () -> Unit,
  icon: Painter,
  iconSize: Dp = 24.dp,
  background: Color,
  resetAfterClick: Boolean = true
): SwipeAction {
  return SwipeAction(
    icon = {
      Image(
        modifier = Modifier.size(iconSize),
        painter = icon,
        contentDescription = null
      )
    },
    iconSize = iconSize,
    background = background,
    onClick = onClick,
    resetAfterClick = resetAfterClick
  )
}
