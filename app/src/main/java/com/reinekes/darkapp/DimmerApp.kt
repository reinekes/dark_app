package com.reinekes.darkapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Nightlight
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.reinekes.darkapp.dimming.DimFilter
import com.reinekes.darkapp.dimming.DimMath
import com.reinekes.darkapp.dimming.DimPreset
import com.reinekes.darkapp.overlay.DimOverlayService
import kotlin.math.roundToInt

private val AppColors = darkColorScheme(
    primary = Color(0xFF9AE6B4),
    secondary = Color(0xFFFFC857),
    tertiary = Color(0xFFFF8A65),
    background = Color(0xFF101114),
    surface = Color(0xFF191B20),
    surfaceVariant = Color(0xFF23262D),
    onPrimary = Color(0xFF082112),
    onSecondary = Color(0xFF2B1D00),
    onBackground = Color(0xFFF5F7FA),
    onSurface = Color(0xFFF5F7FA),
    onSurfaceVariant = Color(0xFFC7CBD4),
)

@Composable
fun DimmerApp() {
    MaterialTheme(colorScheme = AppColors) {
        DimmerScreen()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DimmerScreen() {
    val context = LocalContext.current
    var percent by remember { mutableIntStateOf(36) }
    var enabled by remember { mutableStateOf(false) }
    var filter by remember { mutableStateOf(DimFilter.Neutral) }

    fun startOverlay(nextPercent: Int = percent, nextFilter: DimFilter = filter) {
        percent = nextPercent
        filter = nextFilter
        enabled = true
        ContextCompat.startForegroundService(
            context,
            DimOverlayService.startIntent(context, nextPercent, nextFilter),
        )
    }

    fun stopOverlay() {
        enabled = false
        context.startService(DimOverlayService.stopIntent(context))
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF101114), Color(0xFF15191B), Color(0xFF101114)),
                    ),
                )
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Header(enabled = enabled, onToggle = { checked ->
                if (checked) startOverlay() else stopOverlay()
            })

            PermissionCard(
                hasOverlayPermission = Settings.canDrawOverlays(context),
                onOpenSettings = { context.openOverlaySettings() },
            )

            ControlPanel(
                percent = percent,
                enabled = enabled,
                filter = filter,
                onPercentChange = { next ->
                    percent = next
                    if (enabled) startOverlay(next, filter)
                },
                onPreset = { preset -> startOverlay(preset.percent, filter) },
                onFilter = { next ->
                    filter = next
                    if (enabled) startOverlay(percent, next)
                },
                onStart = { startOverlay() },
                onStop = { stopOverlay() },
            )
        }
    }
}

@Composable
private fun Header(enabled: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Dark App", fontSize = 31.sp, fontWeight = FontWeight.Bold)
            Text(
                text = if (enabled) "Smart dimming is active" else "Ready for low-light control",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Switch(checked = enabled, onCheckedChange = onToggle)
    }
}

@Composable
private fun PermissionCard(hasOverlayPermission: Boolean, onOpenSettings: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (hasOverlayPermission) {
                Color(0xFF17251D)
            } else {
                Color(0xFF2B2115)
            },
        ),
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = if (hasOverlayPermission) Icons.Rounded.LightMode else Icons.Rounded.Tune,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = if (hasOverlayPermission) Color(0xFF9AE6B4) else Color(0xFFFFC857),
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = if (hasOverlayPermission) "Overlay permission granted" else "Overlay permission needed",
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = if (hasOverlayPermission) {
                        "The dim layer can appear above other apps."
                    } else {
                        "Allow display over other apps to dim the whole phone."
                    },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                )
            }
            if (!hasOverlayPermission) {
                OutlinedButton(onClick = onOpenSettings) {
                    Text("Open")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ControlPanel(
    percent: Int,
    enabled: Boolean,
    filter: DimFilter,
    onPercentChange: (Int) -> Unit,
    onPreset: (DimPreset) -> Unit,
    onFilter: (DimFilter) -> Unit,
    onStart: () -> Unit,
    onStop: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Dim level", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("$percent%", fontSize = 52.sp, fontWeight = FontWeight.Bold)
                }
                DimPreview(percent = percent, filter = filter)
            }

            Slider(
                value = percent.toFloat(),
                onValueChange = { onPercentChange(it.roundToInt()) },
                valueRange = 0f..100f,
                steps = 99,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
            )

            Text(
                text = "Overlay alpha ${(DimMath.alphaForPercent(percent) * 100).roundToInt()}%",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                DimPreset.entries.forEach { preset ->
                    FilterChip(
                        selected = percent == preset.percent,
                        onClick = { onPreset(preset) },
                        label = { Text(preset.label) },
                        leadingIcon = {
                            Icon(Icons.Rounded.Nightlight, contentDescription = null, modifier = Modifier.size(18.dp))
                        },
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Tone", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    DimFilter.entries.forEach { item ->
                        FilterChip(
                            selected = filter == item,
                            onClick = { onFilter(item) },
                            label = { Text(item.label) },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onStart,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                ) {
                    Icon(Icons.Rounded.PowerSettingsNew, contentDescription = null, modifier = Modifier.size(19.dp))
                    Spacer(Modifier.size(8.dp))
                    Text(if (enabled) "Update" else "Start")
                }
                OutlinedButton(onClick = onStop, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Rounded.DarkMode, contentDescription = null, modifier = Modifier.size(19.dp))
                    Spacer(Modifier.size(8.dp))
                    Text("Stop")
                }
            }
        }
    }
}

@Composable
private fun DimPreview(percent: Int, filter: DimFilter) {
    val alpha = DimMath.alphaForPercent(percent).coerceIn(0f, 1f)
    val tone = Color(filter.red, filter.green, filter.blue, 255)

    Box(
        modifier = Modifier
            .size(width = 86.dp, height = 54.dp)
            .background(Color(0xFFE6ECF2), RoundedCornerShape(8.dp))
            .padding(5.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color(tone.red, tone.green, tone.blue, alpha), RoundedCornerShape(6.dp)),
        )
        Text("preview", color = Color(0xFF101114), fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

private fun Context.openOverlaySettings() {
    val intent = Intent(
        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
        Uri.parse("package:$packageName"),
    )
    startActivity(intent)
}
