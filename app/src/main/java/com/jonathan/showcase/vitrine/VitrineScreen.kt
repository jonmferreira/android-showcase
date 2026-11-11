package com.jonathan.showcase.vitrine

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.sceneview.Scene
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes

private data class Figure(
    val name: String,
    val concept: String,
    val model: String,
    val stack: List<String>,
    val glow: Color,
    val route: String?,
)

// Vitrine NATIVA mínima: fundo escuro liso + só o objeto 3D (Filament) girando sozinho, alinhado
// no ALTO. Sem efeitos de fundo. Sem setas: swipe navega e os dots indicam. Troca com fade/slide.
@Composable
fun VitrineScreen(onOpen: (String) -> Unit) {
    val figures = remember {
        listOf(
            Figure("ML Kit Vision", "Reconhecimento on-device: QR, rosto e texto",
                "models/hero-camera.glb", listOf("Compose", "CameraX", "ML Kit"), Color(0xFF2AA868), "camera"),
            Figure("Offline-first Sync", "Fila idempotente, sync em lote, sem redundância",
                "models/hero-phone.glb", listOf("Room", "Coroutines", "MVVM"), Color(0xFF3B82F6), "notes"),
            Figure("3D nativo (Filament)", "GLB renderizado nativo, câmera orbital + luz",
                "models/hero-building.glb", listOf("SceneView", "Filament", "glTF"), Color(0xFF14B8A6), "threed?model=0"),
            Figure("VaultCam", "3 câmeras registram a cena: evidência descentralizada",
                "models/hero-vaultcam.glb", listOf("Compose", "CameraX", "Solana", "LiveKit"), Color(0xFF8B5CF6), "threed?model=3"),
        )
    }
    var index by remember { mutableIntStateOf(0) }
    val f = figures[index]
    val glow by animateColorAsState(f.glow, label = "glow")

    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val childNodes = rememberNodes()
    // câmera afastada e olhando pra BAIXO → o modelo sobe (alinhado no alto)
    val cameraNode = rememberCameraNode(engine) {
        position = Position(z = 4.8f)
        lookAt(Position(0f, -0.62f, 0f))
    }
    var modelNode by remember { mutableStateOf<ModelNode?>(null) }

    LaunchedEffect(index) {
        childNodes.clear()
        val node = ModelNode(
            modelInstance = modelLoader.createModelInstance(f.model),
            scaleToUnits = if (f.route?.startsWith("threed?model=3") == true) 1.9f else 1.05f,
        )
        childNodes.add(node)
        modelNode = node
    }
    LaunchedEffect(modelNode) {
        val node = modelNode ?: return@LaunchedEffect
        while (true) {
            withFrameNanos { }
            val r = node.rotation
            node.rotation = Rotation(r.x, r.y + 0.35f, r.z)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0A0C10))) {
        // só o 3D nativo girando, alinhado no alto
        Scene(
            modifier = Modifier.fillMaxSize(),
            engine = engine,
            modelLoader = modelLoader,
            cameraNode = cameraNode,
            childNodes = childNodes,
            cameraManipulator = null,
            isOpaque = false,
        )

        // swipe navega (sem setas)
        var dragAccum by remember { mutableFloatStateOf(0f) }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(figures.size) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (dragAccum < -70) index = (index + 1) % figures.size
                            else if (dragAccum > 70) index = (index - 1 + figures.size) % figures.size
                            dragAccum = 0f
                        },
                    ) { _, delta -> dragAccum += delta }
                },
        )

        // opção no topo: QR pra baixar o APK (release)
        IconButton(
            onClick = { onOpen("qr") },
            modifier = Modifier.align(Alignment.TopEnd).statusBarsPadding().padding(8.dp),
        ) {
            Icon(Icons.Filled.QrCode2, contentDescription = "Baixar APK (QR)", tint = Color.White, modifier = Modifier.size(30.dp))
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 96.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AnimatedContent(
                targetState = index,
                transitionSpec = {
                    (fadeIn(tween(380)) + slideInVertically(tween(380)) { it / 6 }) togetherWith fadeOut(tween(180))
                },
                label = "plate",
            ) { idx ->
                val fg = figures[idx]
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(fg.name.uppercase(), color = Color.White, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(4.dp))
                    Text(fg.concept, color = Color(0xFF9FB0BD), style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        fg.stack.forEach { s ->
                            Surface(color = fg.glow.copy(alpha = 0.14f), shape = RoundedCornerShape(20.dp)) {
                                Text(s, color = fg.glow, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 4.dp))
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                figures.indices.forEach { i ->
                    Box(
                        modifier = Modifier
                            .height(9.dp)
                            .width(if (i == index) 26.dp else 9.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (i == index) glow else Color(0xFF2A333D)),
                    )
                }
            }
            if (f.route != null) {
                Spacer(Modifier.height(14.dp))
                Button(
                    onClick = { onOpen(f.route) },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = glow),
                ) { Text("Abrir demo", color = Color.White, fontWeight = FontWeight.Bold) }
            }
        }
    }
}
