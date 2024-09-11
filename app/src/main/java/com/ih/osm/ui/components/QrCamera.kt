package com.ih.osm.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Create
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.ih.osm.R
import com.ih.osm.ui.components.buttons.CustomButton
import com.ih.osm.ui.theme.PaddingNormal
import com.ih.osm.ui.utils.EMPTY
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

@Composable
fun QrCamera(
    onCameraError: (String) -> Unit,
    onClick: (String) -> Unit
) {
    var code by remember {
        mutableStateOf(EMPTY)
    }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        val scanQrLauncher = rememberLauncherForActivityResult(contract = ScanContract(),
            onResult = { result ->
                if (result.contents != null) {
                    onClick(result.contents)
                } else {
                    onCameraError(context.getString(R.string.unable_to_read_the_qr_code))
                }
            })
        LaunchedEffect(Unit) {
            scanQrLauncher.launch(getScannerOptions())
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(PaddingNormal),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomTextField(
                label = stringResource(R.string.enter_the_qr_code),
                icon = Icons.Rounded.Create,
                modifier = Modifier.fillMaxWidth()
            ) {
                code = it
            }
            CustomSpacer()
            CustomButton(
                text = stringResource(id = R.string.apply),
                modifier = Modifier.fillMaxWidth(),
            ) {
                onClick(code)
            }
            CustomSpacer()
        }
    }

}

private fun getScannerOptions(): ScanOptions {
    return ScanOptions().apply {
        setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        setPrompt("")
        setCameraId(0)
        setBeepEnabled(true)
        setOrientationLocked(false)
    }
}

//    val localContext = LocalContext.current
//    val lifecycleOwner = LocalLifecycleOwner.current
//    val cameraProviderFuture = remember {
//        ProcessCameraProvider.getInstance(localContext)
//    }
//    AndroidView(
//        modifier = modifier,
//        factory = { context ->
//            val previewView = PreviewView(context)
//            val preview = Preview.Builder().build()
//            val selector = CameraSelector.Builder()
//                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
//                .build()
//
//            preview.setSurfaceProvider(previewView.surfaceProvider)
//
//            val imageAnalysis = ImageAnalysis.Builder().build()
//            imageAnalysis.setAnalyzer(
//                ContextCompat.getMainExecutor(context),
//                BarcodeAnalyzerHelper(context)
//            )
//
//            runCatching {
//                cameraProviderFuture.get().bindToLifecycle(
//                    lifecycleOwner,
//                    selector,
//                    preview,
//                    imageAnalysis
//                )
//            }.onFailure {
//                Log.e("CAMERA", "Camera bind error ${it.localizedMessage}", it)
//            }
//            previewView
//        }
//    )
//}