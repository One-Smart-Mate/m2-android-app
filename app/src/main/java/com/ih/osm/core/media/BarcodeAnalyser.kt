package com.ih.osm.core.media

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class BarcodeAnalyser(
    val callback: (Boolean) -> Unit
) : ImageAnalysis.Analyzer {
    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val options =
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()

        val scanner = BarcodeScanning.getClient(options)
        val mediaImage = imageProxy.image
        mediaImage?.let {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.size > 0) {
                        callback(true)
                    }
                }
                .addOnFailureListener {
                    callback(false)
                }
        }
        imageProxy.close()
    }
}
