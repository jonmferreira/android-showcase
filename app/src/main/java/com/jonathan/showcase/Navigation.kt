package com.jonathan.showcase

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jonathan.showcase.camera.CameraScreen
import com.jonathan.showcase.home.HomeWebScreen
import com.jonathan.showcase.notes.NotesScreen
import com.jonathan.showcase.qr.QrScreen
import com.jonathan.showcase.threed.Screen3D
import com.jonathan.showcase.vitrine.VitrineScreen

object Routes {
    const val HOME = "home"       // stage web (Three.js) + navegação nativa
    const val VITRINE = "vitrine" // versão híbrida (nativo por cima do webview) — mantida p/ comparar
    const val CAMERA = "camera"
    const val NOTES = "notes"
    const val THREED = "threed" // aceita ?model=N para abrir já num asset (0=prédio … 3=vaultcam)
    const val QR = "qr"         // QR da release do APK
}

@Composable
fun AppNav() {
    val nav = rememberNavController()
    // tela inicial = VITRINE nativa (Compose + SceneView). HOME (stage web) fica como alternativa.
    NavHost(navController = nav, startDestination = Routes.VITRINE) {
        composable(Routes.HOME) {
            HomeWebScreen(onOpen = { route -> nav.navigate(route) })
        }
        composable(Routes.VITRINE) {
            VitrineScreen(onOpen = { route -> nav.navigate(route) })
        }
        composable(Routes.CAMERA) { CameraScreen(onBack = { nav.popBackStack() }) }
        composable(Routes.NOTES) { NotesScreen(onBack = { nav.popBackStack() }) }
        composable(Routes.QR) { QrScreen(onBack = { nav.popBackStack() }) }
        composable(
            route = "${Routes.THREED}?model={model}",
            arguments = listOf(navArgument("model") { type = NavType.IntType; defaultValue = 0 }),
        ) { entry ->
            Screen3D(
                onBack = { nav.popBackStack() },
                startModel = entry.arguments?.getInt("model") ?: 0,
            )
        }
    }
}
