package com.bitdroid.snap.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.OpenInBrowser
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreSheet(
    onDismiss: () -> Unit,
    onOpenInBrowser: () -> Unit,
    onLogout: () -> Unit,
    onTheme: (String) -> Unit,
) {
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = state,
        containerColor = Bit.Surface,
        dragHandle = {
            Box(Modifier.padding(top = 12.dp)) {
                Box(Modifier.size(width = 36.dp, height = 4.dp).background(Bit.Lav.copy(alpha = 0.4f), CircleShape))
            }
        },
    ) {
        Column(Modifier.fillMaxWidth().padding(start = 18.dp, end = 18.dp, bottom = 28.dp)) {
            Text("bitSnap", style = androidx.compose.material3.MaterialTheme.typography.titleLarge, color = Bit.Text)
            Spacer(Modifier.height(2.dp))
            Text("SNAPCHAT · WEB", style = mono(10f, color = Bit.Lav.copy(alpha = 0.8f), spacing = 2.4f))
            Spacer(Modifier.height(18.dp))

            SheetRow(Icons.Rounded.OpenInBrowser, "Open in browser", onOpenInBrowser)
            SheetRow(Icons.Rounded.Logout, "Log out / clear session", onLogout)

            Spacer(Modifier.height(18.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Palette, null, tint = Bit.Lav, modifier = Modifier.size(20.dp))
                Spacer(Modifier.size(10.dp))
                Text("Theme", color = Bit.TextDim, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(10.dp))
            var sel by remember { mutableStateOf("") }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Themes.all.forEach { choice ->
                    val active = sel == choice.key
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (active) Bit.Accent else Bit.Raised)
                            .squish(onClick = { sel = choice.key; onTheme(choice.key) })
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                    ) {
                        Text(
                            choice.label,
                            color = if (active) Bit.OnAccent else Bit.Lav,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SheetRow(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .squish(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, label, tint = Bit.Lav, modifier = Modifier.size(22.dp))
        Spacer(Modifier.size(14.dp))
        Text(label, color = Bit.Text, fontSize = 16.sp)
    }
}
