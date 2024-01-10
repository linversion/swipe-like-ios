@file:Suppress("TestFunctionName")

package me.saket.swipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ReplyAll
import androidx.compose.material.icons.twotone.Snooze
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams.RenderingMode
import com.linversion.swipe.SwipeAction
import com.linversion.swipe.SwipeableActionsBox
import com.linversion.swipe.SwipeableActionsState
import com.linversion.swipe.rememberSwipeableActionsState
import org.junit.Rule
import org.junit.Test

class SwipeableActionsBoxTest {
  @get:Rule val paparazzi = Paparazzi(
    deviceConfig = DeviceConfig.PIXEL_5,
    showSystemUi = false,
    renderingMode = RenderingMode.SHRINK,
  )

  @Test fun `empty actions`() {
    paparazzi.snapshot {
      Scaffold {
        SwipeableActionsBox {
          BatmanIpsumItem()
        }
      }
    }
  }

  @Test fun `non-empty actions`() {
    paparazzi.snapshot {
      Scaffold {
        SwipeableActionsBox(
          startActions = listOf(replyAll),
          endActions = listOf(snooze),
          content = { BatmanIpsumItem() },
        )
      }
    }
  }

  @Test fun `show a each action's background when offset is small`() {
    paparazzi.snapshot {
      Scaffold {
        SwipeableActionsBox(
          state = rememberSwipeActionsState(initialOffset = 30.dp),
          startActions = listOf(snooze, replyAll),
          swipeThreshold = 80.dp,
          content = { BatmanIpsumItem(background = Color.Unspecified) }
        )
      }
    }
  }

  @Test fun `show a each action's background and part of icon when offset is greater than icon offset`() {
    paparazzi.snapshot {
      Scaffold {
        SwipeableActionsBox(
          state = rememberSwipeActionsState(initialOffset = 80.dp),
          startActions = listOf(snooze, replyAll),
          swipeThreshold = 80.dp,
          content = { BatmanIpsumItem(background = Color.Unspecified) }
        )
      }
    }
  }

  @Test fun `should have extra background when offset is greater than threshold`() {
    paparazzi.snapshot {
      Scaffold {
        SwipeableActionsBox(
          state = rememberSwipeActionsState(initialOffset = 180.dp),
          startActions = listOf(snooze, replyAll),
          swipeThreshold = 80.dp,
          content = { BatmanIpsumItem(background = Color.Unspecified) }
        )
      }
    }
  }

  @Composable
  private fun Scaffold(content: @Composable BoxWithConstraintsScope.() -> Unit) {
    BoxWithConstraints(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color.Whisper),
      content = content,
      contentAlignment = Alignment.Center
    )
  }

  @Composable
  private fun BatmanIpsumItem(
    modifier: Modifier = Modifier,
    background: Color = Color.White
  ) {
    Row(
      modifier
        .fillMaxWidth()
        .shadow(if (background.isSpecified) 1.dp else 0.dp)
        .background(background)
        .padding(20.dp)
    ) {
      Box(
        Modifier
          .padding(top = 4.dp)
          .size(52.dp)
          .clip(CircleShape)
          .background(Color(0xFF6B4FA9))
      )

      Column(Modifier.padding(horizontal = 16.dp)) {
        Text(
          text = "The Batman",
          style = MaterialTheme.typography.titleMedium
        )
        Text(
          modifier = Modifier.padding(top = 2.dp),
          text = "Fear is a tool. When that light hits the sky, it’s not just a call. It’s a warning. For them.",
          style = MaterialTheme.typography.bodyMedium
        )
      }
    }
  }

  @Composable
  fun rememberSwipeActionsState(initialOffset: Dp): SwipeableActionsState {
    return rememberSwipeableActionsState().also {
      it.offsetState.value = LocalDensity.current.run { initialOffset.toPx() }
    }
  }

  companion object {
    val snooze
      @Composable get() = SwipeAction(
        icon = rememberVectorPainter(Icons.TwoTone.Snooze),
        background = Color.SeaBuckthorn,
        onSwipe = {},
      )

    val replyAll
      @Composable get() = SwipeAction(
        icon = rememberVectorPainter(Icons.TwoTone.ReplyAll),
        background = Color.Perfume,
        onSwipe = {},
      )
  }
}

val Color.Companion.Whisper get() = Color(0XFFF8F5FA)
val Color.Companion.SeaBuckthorn get() = Color(0xFFF9A825)
val Color.Companion.Perfume get() = Color(0xFFD0BCFF)
val Color.Companion.GraySuit get() = Color(0xFFC1BAC9)
