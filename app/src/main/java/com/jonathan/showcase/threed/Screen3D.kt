package com.jonathan.showcase.threed

import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

// 3D NATIVO (Filament/SceneView) — objeto preso no centro. Interações:
//  • ARRASTAR (1 dedo) gira o modelo
//  • PINÇA (2 dedos) ou botões −/+ dão ZOOM (câmera afastando/aproximando, CLAMPADO — sem extremo)
//  • botão embaixo TROCA o asset 3D (cicla a lista de GLBs)
private val MODELS = listOf(
    "Prédio" to "models/hero-building.glb",
    "Câmera" to "models/hero-camera.glb",
    "Telefone" to "models/hero-phone.glb",
    "VaultCam" to "models/hero-vaultcam.glb",
)

private const val CAM_NEAR = 2.5f // mais perto permitido
private const val CAM_FAR = 8.0f  // mais longe permitido
private const val CAM_START = 4.5f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Screen3D(onBack: () -> Unit, startModel: Int = 0) {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val childNodes = rememberNodes()
    val cameraNode = rememberCameraNode(engine) { position = Position(z = CAM_START) }
    var modelNode by remember { mutableStateOf<ModelNode?>(null) }
    var modelIndex by remember { mutableIntStateOf(startModel.coerceIn(0, MODELS.size - 1)) }
    var camZ by remember { mutableFloatStateOf(CAM_START) }

    // troca o asset ao mudar o índice (e reseta o zoom)
    LaunchedEffect(modelIndex) {
        childNodes.clear()
        val node = ModelNode(
            modelInstance = modelLoader.createModelInstance(MODELS[modelIndex].second),
            scaleToUnits = 1.6f,
        )
        childNodes.add(node)
        modelNode = node
        camZ = CAM_START
    }
    // aplica o zoom na câmera (clampado) sempre que camZ muda
    LaunchedEffect(camZ) { cameraNode.position = Position(z = camZ) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("3D nativo (Filament)") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
            )
        },
    ) { pad ->
        Box(modifier = Modifier.fillMaxSize().padding(pad)) {
            Scene(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(modelNode) {
                        // 1 dedo = gira · 2 dedos (pinça) = zoom clampado
                        detectTransformGestures { _, pan, zoom, _ ->
                            if (zoom != 1f) {
                                camZ = (camZ / zoom).coerceIn(CAM_NEAR, CAM_FAR)
                            } else {
                                val n = modelNode ?: return@detectTransformGestures
                                val r = n.rotation
                                n.rotation = Rotation(r.x + pan.y * 0.3f, r.y + pan.x * 0.3f, r.z)
                            }
                        }
                    },
                engine = engine,
                modelLoader = modelLoader,
                cameraNode = cameraNode,
                childNodes = childNodes,
                cameraManipulator = null, // sem zoom/pan livre → controlamos nós (preso no centro)
            )

            // único controle: trocar o asset 3D (zoom é pinça, giro é arraste)
            Surface(
                color = Color(0xFF14B8A6),
                shape = RoundedCornerShape(24.dp),
                onClick = { modelIndex = (modelIndex + 1) % MODELS.size },
                modifier = Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom = 24.dp),
            ) {
                Text(
                    "Trocar modelo · ${MODELS[modelIndex].first}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 13.dp),
                )
            }

            Surface(
                color = Color(0xCC000000),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 10.dp),
            ) {
                Text(
                    "arraste gira · pinça dá zoom · botão troca o modelo",
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                )
            }
        }
    }
}
