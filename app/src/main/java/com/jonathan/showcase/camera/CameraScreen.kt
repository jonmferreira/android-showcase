package com.jonathan.showcase.camera

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.Executors
import kotlin.coroutines.resume

private enum class Mode(val label: String) { BARCODE("QR / Código"), FACE("Rosto"), TEXT("Texto") }

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalGetImage
@Composable
fun CameraScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var granted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED,
        )
    }
    val permLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted = it }
    LaunchedEffect(Unit) { if (!granted) permLauncher.launch(Manifest.permission.CAMERA) }

    var lensFront by remember { mutableStateOf(false) }
    var mode by remember { mutableStateOf(Mode.BARCODE) }
    var result by remember { mutableStateOf("Aponte a câmera...") }

    val previewView = remember { PreviewView(context) }
    val analysisExecutor = remember { Executors.newSingleThreadExecutor() }
    val barcodeScanner = remember { BarcodeScanning.getClient() }
    val faceDetector = remember { FaceDetection.getClient() }
    val textRecognizer = remember { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) }

    // (re)liga a câmera sempre que lente/modo mudam
    LaunchedEffect(granted, lensFront, mode) {
        if (!granted) return@LaunchedEffect
        val provider = awaitProvider(context)
        provider.unbindAll()
        val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
        val selector = CameraSelector.Builder()
            .requireLensFacing(if (lensFront) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK)
            .build()
        val analysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        analysis.setAnalyzer(analysisExecutor) { proxy ->
            analyze(proxy, mode, barcodeScanner, faceDetector, textRecognizer) { result = it }
        }
        provider.bindToLifecycle(lifecycleOwner, selector, preview, analysis)
    }

    DisposableEffect(Unit) {
        onDispose {
            analysisExecutor.shutdown()
            barcodeScanner.close(); faceDetector.close(); textRecognizer.close()
            ProcessCameraProvider.getInstance(context).get().unbindAll()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ML Kit on-device") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar") }
                },
                actions = {
                    IconButton(onClick = { lensFront = !lensFront }) {
                        Icon(Icons.Filled.Cameraswitch, contentDescription = "Trocar câmera")
                    }
                },
            )
        },
    ) { pad ->
        Column(modifier = Modifier.fillMaxSize().padding(pad)) {
            if (!granted) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Button(onClick = { permLauncher.launch(Manifest.permission.CAMERA) }) {
                        Text("Permitir câmera")
                    }
                }
                return@Column
            }
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
                Surface(
                    color = Color(0xCC000000),
                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                ) {
                    Text(
                        result,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp),
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Mode.entries.forEach { m ->
                    FilterChip(selected = mode == m, onClick = { mode = m }, label = { Text(m.label) })
                }
            }
        }
    }
}

private suspend fun awaitProvider(context: android.content.Context): ProcessCameraProvider =
    suspendCancellableCoroutine { cont ->
        val future = ProcessCameraProvider.getInstance(context)
        future.addListener({ cont.resume(future.get()) }, ContextCompat.getMainExecutor(context))
    }

@ExperimentalGetImage
private fun analyze(
    proxy: ImageProxy,
    mode: Mode,
    barcode: com.google.mlkit.vision.barcode.BarcodeScanner,
    face: com.google.mlkit.vision.face.FaceDetector,
    text: com.google.mlkit.vision.text.TextRecognizer,
    onResult: (String) -> Unit,
) {
    val media = proxy.image
    if (media == null) { proxy.close(); return }
    val input = InputImage.fromMediaImage(media, proxy.imageInfo.rotationDegrees)
    when (mode) {
        Mode.BARCODE -> barcode.process(input)
            .addOnSuccessListener { list ->
                onResult(if (list.isEmpty()) "Nenhum código" else "Código: " + list.joinToString { it.rawValue ?: "?" })
            }
            .addOnCompleteListener { proxy.close() }

        Mode.FACE -> face.process(input)
            .addOnSuccessListener { faces -> onResult("Rostos detectados: ${faces.size}") }
            .addOnCompleteListener { proxy.close() }

        Mode.TEXT -> text.process(input)
            .addOnSuccessListener { r ->
                val t = r.text.replace("\n", " ").take(80)
                onResult(if (t.isBlank()) "Nenhum texto" else "Texto: $t")
            }
            .addOnCompleteListener { proxy.close() }
    }
}
