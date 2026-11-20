package com.bitdroid.snap.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChatBubble
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.ui.res.painterResource
import com.bitdroid.snap.R

/** The lilac bitSnap mark on the Bloom backdrop with a wordmark and a thin load bar. */
@Composable
fun LoadingOverlay(progress: Float, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().backdrop(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier.size(104.dp).clip(RoundedCornerShape(30.dp)).background(Bit.Lav.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Image(painterResource(R.drawable.ic_launcher_foreground_light), contentDescription = null, modifier = Modifier.size(104.dp))
            }
            Spacer(Modifier.height(18.dp))
            Text("bitSnap", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium, color = Bit.Text)
            Spacer(Modifier.height(4.dp))
            Text("SNAPCHAT · WEB", style = mono(10f, color = Bit.Lav.copy(alpha = 0.8f), spacing = 2.4f))
            Spacer(Modifier.height(22.dp))
            LinearProgressIndicator(
                progress = { progress.coerceIn(0.02f, 1f) },
                modifier = Modifier.width(132.dp).height(4.dp).clip(CircleShape),
                color = Bit.Accent,
                trackColor = Bit.Lav.copy(alpha = 0.18f),
            )
        }
    }
}

/** Shown when the page fails to load (offline, timeout). */
@Composable
fun ErrorScreen(onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().backdrop(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp),
        ) {
            Text("⚡", fontSize = 48.sp)
            Spacer(Modifier.height(12.dp))
            Text("Can't reach Snapchat", style = androidx.compose.material3.MaterialTheme.typography.titleLarge, color = Bit.Text)
            Spacer(Modifier.height(8.dp))
            Text(
                "Check your connection and try again.",
                color = Bit.TextDim, textAlign = TextAlign.Center,
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
            )
            Spacer(Modifier.height(24.dp))
            Box(
                Modifier.clip(CircleShape).background(Bit.Accent).squish(onClick = onRetry)
                    .padding(horizontal = 28.dp, vertical = 12.dp),
            ) {
                Text("Retry", color = Bit.OnAccent, fontWeight = FontWeight.Bold)
            }
        }
    }
}

class SnapTab(val icon: ImageVector, val label: String)

/**
 * Floating Bloom tab bar: a frosted pill with a sliding lavender lens on the active tab.
 * Only the selected tab shows its label, like the bitMessages bar.
 */
@Composable
fun SnapTabBar(
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabs = listOf(
        SnapTab(Icons.Rounded.ChatBubble, "Chats"),
        SnapTab(Icons.Rounded.Refresh, "Reload"),
        SnapTab(Icons.Rounded.MoreHoriz, "More"),
    )
    Row(
        modifier.fillMaxWidth().navigationBarsPadding().padding(start = 40.dp, end = 40.dp, bottom = 14.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        Row(
            Modifier
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0xFF241F38).copy(alpha = 0.92f))
                .padding(6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            tabs.forEachIndexed { i, tab ->
                val active = i == selected
                val lensAlpha by animateFloatAsState(if (active) 1f else 0f, tween(220), label = "lens")
                val pad by animateDpAsState(if (active) 16.dp else 12.dp, label = "pad")
                Row(
                    Modifier
                        .clip(RoundedCornerShape(22.dp))
                        .background(Bit.Accent.copy(alpha = 0.95f * lensAlpha))
                        .squish(onClick = { onSelect(i) })
                        .padding(horizontal = pad, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        tab.icon, tab.label,
                        tint = if (active) Bit.OnAccent else Bit.Lav,
                        modifier = Modifier.size(22.dp),
                    )
                    AnimatedVisibility(active) {
                        Text(
                            tab.label,
                            color = Bit.OnAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                }
            }
        }
    }
}
