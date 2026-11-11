# DevShowcase — Android (Jetpack Compose)

Showcase de engenharia Android nativa: uma vitrine 3D (Filament) na home e demos funcionais de
SDKs/libs de mercado, cada uma numa tela própria.

## Demos

| Tela | O que mostra | Stack |
|---|---|---|
| **Vitrine (home)** | Objeto 3D girando, navegação por swipe | Jetpack Compose · SceneView/Filament |
| **ML Kit Vision** | Reconhecimento on-device: QR, rosto e texto | CameraX · ML Kit |
| **Offline-first Sync** | Fila idempotente, sync em lote, funciona sem rede | Room · Coroutines · MVVM |
| **3D nativo** | Arrasta pra girar, pinça pra zoom, troca de asset | SceneView/Filament · glTF |
| **VaultCam** | Conceito de evidência descentralizada (3 câmeras registram a cena) | Compose · CameraX |

## Stack

- Kotlin · Jetpack Compose · Material 3 · Navigation Compose
- CameraX 1.3 · ML Kit (barcode / face / text, on-device)
- Room + KSP (persistência offline-first)
- SceneView 2.2.1 (Filament) — modelos GLB (Blender, Poly Pizza CC0/CC-BY)
- compileSdk 35 · AGP 8.7 · Java 21

## Build

```bash
./gradlew assembleDebug
```

Modelos 3D em `app/src/main/assets/models/`. Os GLBs de herói foram modelados/compostos no Blender.

## Licença

MIT.
