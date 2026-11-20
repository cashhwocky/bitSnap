package com.bitdroid.snap.ui

import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/** Screen background: the Bloom Home purple-to-teal wallpaper glow, or flat black for the AMOLED theme. */
fun Modifier.backdrop(): Modifier =
    if (!Bit.expressive) background(Bit.Bg) else drawBehind {
        drawRect(Brush.verticalGradient(0f to Color(0xFF221A45), 0.45f to Color(0xFF120F1C), 1f to Color(0xFF0F1419)))
        drawRect(
            Brush.radialGradient(
                listOf(Color(0xFF5C40C8).copy(alpha = 0.7f), Color.Transparent),
                center = Offset(size.width * 0.15f, 0f), radius = size.width * 1.0f,
            ),
        )
        drawRect(
            Brush.radialGradient(
                listOf(Color(0xFF1E6E6E).copy(alpha = 0.45f), Color.Transparent),
                center = Offset(size.width, size.height), radius = size.width * 0.9f,
            ),
        )
    }
