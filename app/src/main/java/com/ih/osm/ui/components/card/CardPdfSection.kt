package com.ih.osm.ui.components.card

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ih.osm.domain.model.Evidence
import com.ih.osm.ui.components.CustomSpacer
import com.ih.osm.ui.components.SpacerSize
import com.ih.osm.ui.components.pdf.PreviewPdf
import com.ih.osm.ui.extensions.getTextColor
import com.ih.osm.ui.theme.PaddingNormal
import com.ih.osm.ui.theme.PaddingSmall
import com.ih.osm.ui.theme.Size200
import com.ih.osm.ui.theme.Size250
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun CardPdfSection(
    title: String,
    evidences: List<Evidence>,
) {
    var selectedPdfUrl by remember { mutableStateOf("") }
    var openPdf by remember { mutableStateOf(false) }

    Column {
        if (evidences.isNotEmpty() && title.isNotEmpty()) {
            Text(
                text = title,
                style =
                    MaterialTheme.typography.titleLarge
                        .copy(fontWeight = FontWeight.Bold),
            )
            CustomSpacer(space = SpacerSize.SMALL)
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(PaddingSmall),
        ) {
            items(evidences) { evidence ->
                PdfCardItem(
                    pdfUrl = evidence.url,
                    fileName = evidence.url.substringAfterLast("/").substringAfterLast("\\"),
                    modifier =
                        Modifier
                            .width(Size200)
                            .height(Size250)
                            .clickable {
                                selectedPdfUrl = evidence.url
                                openPdf = true
                            },
                )
            }
        }

        // Preview del PDF
        PreviewPdf(
            openPdf = openPdf,
            pdfUrl = selectedPdfUrl,
            onDismiss = {
                openPdf = false
                selectedPdfUrl = ""
            },
        )
    }
}

@Composable
fun PdfCardItem(
    pdfUrl: String,
    fileName: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val isPdf = pdfUrl.endsWith(".pdf", ignoreCase = true)
    val iconColor = if (isPdf) Color(0xFFDC3545) else MaterialTheme.colorScheme.primary
    val iconBackground = iconColor.copy(alpha = 0.1f)

    var pdfBitmap by remember(pdfUrl) { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember(pdfUrl) { mutableStateOf(false) }
    var hasError by remember(pdfUrl) { mutableStateOf(false) }

    // Solo intentar renderizar si es PDF y la API lo soporta
    LaunchedEffect(pdfUrl) {
        if (isPdf && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && pdfBitmap == null) {
            isLoading = true
            hasError = false

            withContext(Dispatchers.IO) {
                try {
                    val file =
                        when {
                            pdfUrl.startsWith("http://") || pdfUrl.startsWith("https://") -> {
                                // Descargar archivo desde URL
                                val tempFile = File(context.cacheDir, "temp_pdf_${pdfUrl.hashCode()}.pdf")
                                if (!tempFile.exists()) {
                                    val url = URL(pdfUrl)
                                    val connection = url.openConnection() as HttpURLConnection
                                    connection.connectTimeout = 10000
                                    connection.readTimeout = 10000
                                    connection.connect()

                                    if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                                        connection.inputStream.use { input ->
                                            FileOutputStream(tempFile).use { output ->
                                                input.copyTo(output)
                                            }
                                        }
                                    } else {
                                        hasError = true
                                        return@withContext
                                    }
                                }
                                tempFile
                            }
                            pdfUrl.startsWith("file://") -> {
                                File(Uri.parse(pdfUrl).path ?: "")
                            }
                            else -> {
                                // Asumir que es una ruta local
                                File(pdfUrl)
                            }
                        }

                    if (file.exists() && file.canRead()) {
                        val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                        val pdfRenderer =
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                PdfRenderer(fileDescriptor)
                            } else {
                                null
                            }

                        pdfRenderer?.let { renderer ->
                            if (renderer.pageCount > 0) {
                                val page = renderer.openPage(0)

                                // Calcular el tamaño del bitmap manteniendo la proporción
                                val scale = 200f / page.width.coerceAtLeast(page.height)
                                val scaledWidth = (page.width * scale).toInt()
                                val scaledHeight = (page.height * scale).toInt()

                                // Crear bitmap con el tamaño escalado
                                val bitmap =
                                    Bitmap.createBitmap(
                                        scaledWidth,
                                        scaledHeight,
                                        Bitmap.Config.ARGB_8888,
                                    )

                                // Fondo blanco para el PDF
                                bitmap.eraseColor(android.graphics.Color.WHITE)

                                // Renderizar la página en el bitmap
                                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                                pdfBitmap = bitmap

                                page.close()
                                renderer.close()
                            }
                        }

                        fileDescriptor.close()
                    } else {
                        hasError = true
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    hasError = true
                } finally {
                    isLoading = false
                }
            }
        }
    }

    // Limpiar recursos cuando el composable se destruya
    DisposableEffect(pdfUrl) {
        onDispose {
            pdfBitmap?.recycle()
            pdfBitmap = null
        }
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            ),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            when {
                isLoading -> {
                    // Mostrar indicador de carga
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = iconColor,
                        strokeWidth = 3.dp,
                    )
                }
                pdfBitmap != null && !hasError -> {
                    // Mostrar vista previa del PDF
                    Column(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .padding(PaddingSmall)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White),
                        ) {
                            Image(
                                bitmap = pdfBitmap!!.asImageBitmap(),
                                contentDescription = "Vista previa PDF",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit,
                            )

                            // Indicador de PDF en la esquina
                            Box(
                                modifier =
                                    Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                        .background(
                                            color = Color(0xFFDC3545).copy(alpha = 0.9f),
                                            shape = RoundedCornerShape(4.dp),
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                            ) {
                                Text(
                                    text = "PDF",
                                    style =
                                        MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                        ),
                                )
                            }
                        }

                        // Nombre del archivo en la parte inferior
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = MaterialTheme.colorScheme.surface,
                                        shape =
                                            RoundedCornerShape(
                                                topStart = 0.dp,
                                                topEnd = 0.dp,
                                                bottomStart = 12.dp,
                                                bottomEnd = 12.dp,
                                            ),
                                    )
                                    .padding(PaddingSmall),
                        ) {
                            val displayName =
                                when {
                                    fileName.endsWith(".pdf", ignoreCase = true) ->
                                        fileName.removeSuffix(".pdf").removeSuffix(".PDF")
                                    fileName.isEmpty() -> "Documento"
                                    else -> fileName.substringBeforeLast(".")
                                }

                            Text(
                                text = displayName,
                                style =
                                    MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = getTextColor(),
                                    ),
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
                else -> {
                    // Mostrar icono por defecto
                    PdfDefaultView(
                        isPdf = isPdf,
                        fileName = fileName,
                        iconColor = iconColor,
                        iconBackground = iconBackground,
                    )
                }
            }
        }
    }
}

@Composable
private fun PdfDefaultView(
    isPdf: Boolean,
    fileName: String,
    iconColor: Color,
    iconBackground: Color,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(PaddingNormal),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Icono de PDF o archivo
        Box(
            modifier =
                Modifier
                    .size(80.dp)
                    .background(
                        color = iconBackground,
                        shape = RoundedCornerShape(16.dp),
                    ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (isPdf) Icons.Default.AddCircle else Icons.Default.Info,
                contentDescription = if (isPdf) "PDF" else "Archivo",
                modifier = Modifier.size(48.dp),
                tint = iconColor,
            )
        }

        CustomSpacer()

        // Nombre del archivo
        val displayName =
            when {
                fileName.endsWith(".pdf", ignoreCase = true) ->
                    fileName.removeSuffix(".pdf").removeSuffix(".PDF")
                fileName.isEmpty() -> "Documento"
                else -> fileName.substringBeforeLast(".")
            }

        Text(
            text = displayName,
            style =
                MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = getTextColor(),
                ),
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
        )

        CustomSpacer(space = SpacerSize.SMALL)

        // Texto indicativo
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp),
                    )
                    .padding(horizontal = PaddingSmall, vertical = 4.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            CustomSpacer(space = SpacerSize.TINY)
            Text(
                text = "Tocar para abrir",
                style =
                    MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                    ),
            )
        }
    }
}
