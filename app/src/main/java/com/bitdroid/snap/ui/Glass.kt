package com.bitdroid.snap.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

/** Quiet frosted Bloom surface: a deep purple-gray fill with a faint lavender edge. */
fun Modifier.glass(shape: Shape, strength: Float = 1f): Modifier =
    this.clip(shape)
        .background(Color(0xFF2A2440).copy(alpha = 0.9f * strength))
        .border(0.75.dp, Bit.Lav.copy(alpha = 0.16f), shape)

/** Clickable with a gentle press-in and settle. */
fun Modifier.squish(enabled: Boolean = true, onClick: () -> Unit): Modifier = composed {
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val s by animateFloatAsState(
        if (pressed) 0.95f else 1f,
        spring(dampingRatio = if (pressed) Spring.DampingRatioNoBouncy else 0.6f, stiffness = 600f),
        label = "squish",
    )
    this.graphicsLayer { scaleX = s; scaleY = s }
        .clickable(interactionSource = source, indication = null, enabled = enabled, onClick = onClick)
}
