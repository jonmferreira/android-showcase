package com.jonathan.showcase.bonus

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private data class Report(val title: String, val desc: String, val file: String)

private const val BASE =
    "https://github.com/jonmferreira/android-showcase/releases/download/v0.1.0/"

private val REPORTS = listOf(
    Report(
        "Telemetria & Observabilidade",
        "Como medir, de um site externo, a saúde e o sucesso de um app em produção: 9 tópicos (estabilidade, performance, device, rede, versão, uso, sucesso por feature, segurança) + funil tentativa→sucesso→falha + 3 abordagens de dashboard.",
        "01_telemetria-observabilidade-android.pdf",
    ),
    Report(
        "Requisitos Google Play 2026-2027",
        "O que exige pra manter o app na loja, com datas de enforcement: Restore Credentials (troca de device), thresholds de memória/bitmap/DEX, target API 36, baseline permanente.",
        "02_google-play-requisitos-2026-2027.pdf",
    ),
    Report(
        "Operação em produção",
        "Pôr e operar app em produção além da telemetria: rollout escalonado, feature flags/kill switch, segurança (Integrity/secrets), contrato de API, observabilidade do backend, CI/CD, FCM, LGPD (A-J).",
        "03_producao-operacao-app-mobile.pdf",
    ),
)

// Página BÔNUS: relatórios técnicos pra download (hospedados na release do GitHub).
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BonusScreen(onBack: () -> Unit) {
    val ctx = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bônus · Relatórios técnicos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
            )
        },
    ) { pad ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0A0C10))
                .padding(pad)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text(
                    "Material de estudo que escrevi sobre levar um app Android pra produção. Toque em baixar para pegar o PDF.",
                    color = Color(0xFF9FB0BD), style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
                )
            }
            items(REPORTS) { r ->
                Surface(color = Color(0xFF161B22), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(r.title, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(6.dp))
                        Text(r.desc, color = Color(0xFF9FB0BD), style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(12.dp))
                        FilledTonalButton(
                            onClick = { ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(BASE + r.file))) },
                        ) {
                            Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.size(8.dp))
                            Text("Baixar PDF")
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}
