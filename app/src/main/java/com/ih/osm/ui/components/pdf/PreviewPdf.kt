package com.ih.osm.ui.components.pdf

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ih.osm.ui.extensions.getTextColor
import com.ih.osm.ui.theme.PaddingNormal
import com.ih.osm.ui.theme.PaddingSmall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

private const val TAG = "PreviewPdf"

@Composable
fun PreviewPdf(
    openPdf: Boolean,
    pdfUrl: String,
    onDismiss: () -> Unit,
) {
    if (openPdf && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        Dialog(
            onDismissRequest = onDismiss,
            properties =
                DialogProperties(
                    usePlatformDefaultWidth = false,
                    dismissOnBackPress = true,
                    dismissOnClickOutside = false,
                ),
        ) {
            PdfViewerContent(pdfUrl = pdfUrl, onDismiss = onDismiss)
        }
    }
}

@Composable
private fun PdfViewerContent(
    pdfUrl: String,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    var pdfPages by remember { mutableStateOf<List<Bitmap>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var currentPage by remember { mutableStateOf(0) }
    var totalPages by remember { mutableStateOf(0) }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Valores para zoom y pan
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    // Control de transformaciones
    val transformState =
        rememberTransformableState { zoomChange, panChange, _ ->
            // Ajusta zoom y lo clampa
            val newScale = (scale * zoomChange).coerceIn(1f, 5f)
            // Calcula límites tras escala
            val maxX = (containerSize.width * (newScale - 1f)) / 2f
            val maxY = (containerSize.height * (newScale - 1f)) / 2f

            // Actualiza scale y offset con clamp
            scale = newScale
            offset =
                Offset(
                    x = (offset.x + panChange.x).coerceIn(-maxX, maxX),
                    y = (offset.y + panChange.y).coerceIn(-maxY, maxY),
                )
        }

    // Conexión nestedScroll para scroll con rueda o swipe
    val nestedScrollConn =
        remember {
            object : NestedScrollConnection {
                override fun onPreScroll(
                    available: Offset,
                    source: NestedScrollSource,
                ): Offset {
                    if (scale > 1f) {
                        // Los mismos límites de pan
                        val maxX = (containerSize.width * (scale - 1f)) / 2f
                        val maxY = (containerSize.height * (scale - 1f)) / 2f
                        offset =
                            Offset(
                                x = (offset.x + available.x).coerceIn(-maxX, maxX),
                                y = (offset.y + available.y).coerceIn(-maxY, maxY),
                            )
                        return available
                    }
                    return Offset.Zero
                }
            }
        }

    // Carga de páginas PDF
    LaunchedEffect(pdfUrl) {
        withContext(Dispatchers.IO) {
            try {
                val file =
                    when {
                        pdfUrl.startsWith("http") -> {
                            val tmp = File(context.cacheDir, "temp_${pdfUrl.hashCode()}.pdf")
                            if (!tmp.exists()) {
                                val conn = URL(pdfUrl).openConnection() as HttpURLConnection
                                conn.connectTimeout = 30000
                                conn.readTimeout = 30000
                                conn.connect()
                                if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                                    conn.inputStream.use { input ->
                                        FileOutputStream(tmp).use { out ->
                                            input.copyTo(out)
                                        }
                                    }
                                } else {
                                    throw Exception("HTTP ${conn.responseCode}")
                                }
                            }
                            tmp
                        }
                        else -> File(pdfUrl)
                    }
                if (!file.exists() || !file.canRead()) throw Exception("Archivo no legible")

                val fd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                val renderer = PdfRenderer(fd)
                totalPages = renderer.pageCount

                val pages = mutableListOf<Bitmap>()
                repeat(renderer.pageCount) { i ->
                    renderer.openPage(i).use { page ->
                        val factor = 2000f / page.width.coerceAtLeast(page.height)
                        val w = (page.width * factor).toInt()
                        val h = (page.height * factor).toInt()
                        val bmp =
                            Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                                .apply { eraseColor(android.graphics.Color.WHITE) }
                        page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        pages += bmp
                    }
                }
                renderer.close()
                fd.close()
                pdfPages = pages
                isLoading = false
            } catch (e: Exception) {
                error = "Error al cargar PDF: ${e.localizedMessage}"
                isLoading = false
                Log.e(TAG, "Error cargando PDF", e)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { pdfPages.forEach { it.recycle() } }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f)),
    ) {
        Column(Modifier.fillMaxSize()) {
            // Header
            Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(0.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(PaddingSmall),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (totalPages > 0) "Página ${currentPage + 1} de $totalPages" else "Cargando…",
                        color = getTextColor(),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = getTextColor())
                    }
                }
            }

            // Contenido con zoom, scroll vertical y horizontal dentro de límites
            Box(
                Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .nestedScroll(nestedScrollConn)
                    .transformable(state = transformState)
                    .pointerInput(Unit) {
                        detectTapGestures { tap ->
                            val target =
                                if (tap.x > size.width / 2f) {
                                    (currentPage + 1).coerceAtMost(totalPages - 1)
                                } else {
                                    (currentPage - 1).coerceAtLeast(0)
                                }
                            scope.launch { listState.animateScrollToItem(target) }
                        }
                    }
                    .onSizeChanged { containerSize = it },
            ) {
                when {
                    isLoading ->
                        CircularProgressIndicator(
                            Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.primary,
                        )
                    error != null ->
                        Text(
                            text = error!!,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.Center),
                        )
                    else ->
                        LazyColumn(
                            Modifier
                                .fillMaxSize()
                                .graphicsLayer(
                                    scaleX = scale,
                                    scaleY = scale,
                                    translationX = offset.x,
                                    translationY = offset.y,
                                ),
                            state = listState,
                            verticalArrangement = Arrangement.spacedBy(PaddingSmall),
                            contentPadding = PaddingValues(PaddingNormal),
                        ) {
                            itemsIndexed(pdfPages) { idx, bmp ->
                                currentPage = listState.firstVisibleItemIndex
                                ZoomablePdfPage(bmp, idx + 1)
                            }
                        }
                }
            }
        }
    }
}

@Composable
private fun ZoomablePdfPage(
    bitmap: Bitmap,
    pageNumber: Int,
) {
    Card(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Box(Modifier.background(Color.White)) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Página $pageNumber",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
