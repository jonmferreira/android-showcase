package com.jonathan.showcase.qr

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

const val RELEASE_URL = "https://github.com/jonmferreira/android-showcase/releases/latest"

private fun qrBitmap(text: String, size: Int): Bitmap {
    val matrix = QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size)
    val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
    for (x in 0 until size) {
        for (y in 0 until size) {
            bmp.setPixel(x, y, if (matrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
        }
    }
    return bmp
}

// Tela de QR: escaneie para abrir a release e baixar/instalar o APK.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrScreen(onBack: () -> Unit, onBonus: () -> Unit) {
    val ctx = LocalContext.current
    val qr = remember { qrBitmap(RELEASE_URL, 640).asImageBitmap() }
    val accent = Color(0xFF2AA868)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Instalar no seu device") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
            )
        },
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0A0C10))
                .padding(pad)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                "Escaneie para baixar o APK",
                color = Color.White, fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center,
            )
            Text(
                "Abre a release no GitHub · instale habilitando \"fontes desconhecidas\"",
                color = Color(0xFF9FB0BD), style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center, modifier = Modifier.padding(top = 6.dp, bottom = 24.dp),
            )
            Surface(color = Color.White, shape = RoundedCornerShape(20.dp)) {
                Box(Modifier.padding(16.dp)) {
                    Image(bitmap = qr, contentDescription = "QR da release", contentScale = ContentScale.Fit, modifier = Modifier.size(260.dp))
                }
            }
            Text(
                RELEASE_URL,
                color = Color(0xFF9FB0BD), style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center, modifier = Modifier.padding(top = 20.dp),
            )
            Button(
                onClick = { ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(RELEASE_URL))) },
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accent),
                modifier = Modifier.padding(top = 16.dp),
            ) { Text("Abrir release no navegador", color = Color.White, fontWeight = FontWeight.Bold) }

            // bônus: relatórios técnicos pra download
            TextButton(onClick = onBonus, modifier = Modifier.padding(top = 4.dp)) {
                Icon(Icons.Filled.CardGiftcard, contentDescription = null, tint = accent, modifier = Modifier.size(18.dp))
                androidx.compose.foundation.layout.Spacer(Modifier.size(8.dp))
                Text("Bônus: relatórios técnicos", color = accent, fontWeight = FontWeight.SemiBold)
            }
            androidx.compose.foundation.layout.Spacer(Modifier.height(8.dp))
        }
    }
}
