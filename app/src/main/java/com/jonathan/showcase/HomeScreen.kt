package com.jonathan.showcase

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private data class Showcase(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val tile: List<Color>,
    val onClick: () -> Unit,
)

@Composable
fun HomeScreen(onOpenCamera: () -> Unit, onOpenNotes: () -> Unit, onOpen3D: () -> Unit) {
    val items = listOf(
        Showcase(
            "Vitrine ML Kit + CameraX",
            "QR, rosto e texto reconhecidos no próprio device",
            Icons.Filled.CameraAlt,
            listOf(Color(0xFF1F8F55), Color(0xFF34C77B)),
            onOpenCamera,
        ),
        Showcase(
            "Offline-first + Sync",
            "Room local, fila idempotente e sync em lote",
            Icons.Filled.CloudSync,
            listOf(Color(0xFF2563EB), Color(0xFF60A5FA)),
            onOpenNotes,
        ),
        Showcase(
            "3D nativo (Filament)",
            "Modelo GLB renderizado nativo, câmera orbital + luz",
            Icons.Filled.ViewInAr,
            listOf(Color(0xFF8B5CF6), Color(0xFFA78BFA)),
            onOpen3D,
        ),
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.surfaceVariant),
                ),
            ),
    ) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding().padding(20.dp)) {
            Text(
                "DevShowcase",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                "Showcases mobile · Android",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(22.dp))
            items.forEach { s ->
                ShowcaseCard(s)
                Spacer(Modifier.height(14.dp))
            }
        }
    }
}

@Composable
private fun ShowcaseCard(s: Showcase) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { s.onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(s.tile)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(s.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    s.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    s.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    AppTheme { HomeScreen(onOpenCamera = {}, onOpenNotes = {}, onOpen3D = {}) }
}
