package com.example.mylocalcanvas.ui.gallery

import com.example.mylocalcanvas.ui.common.LocalCanvasShapes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.mylocalcanvas.ui.common.LocalCanvasScreen
import coil.compose.AsyncImage
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close

data class GalleryItem(
    val id: Long,
    val uri: Uri,
    val title: String,
    val subtitle: String
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun GalleryScreen() {
    val context = LocalContext.current
    var hasPermission by remember { mutableStateOf(false) }
    var previewUri by remember { mutableStateOf<Uri?>(null) }
    val permission = remember { requiredPermission() }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    LaunchedEffect(Unit) {
        hasPermission = checkPermission(context)
        if (!hasPermission) {
            permissionLauncher.launch(permission)
        }
    }

    val items by produceState<List<GalleryItem>>(initialValue = emptyList(), hasPermission) {
        value = if (hasPermission) {
            loadGalleryItems(context)
        } else {
            emptyList()
        }
    }

    LocalCanvasScreen {
        Text(
            text = "图库",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(16.dp))

        if (!hasPermission) {
            Text(
                text = "需要读取系统相册权限以展示已保存图片",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))
            androidx.compose.material3.Button(onClick = { permissionLauncher.launch(permission) }) {
                Text("授予权限")
            }
            return@LocalCanvasScreen
        }

        if (items.isEmpty()) {
            Text(
                text = "暂无已保存的图片",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            return@LocalCanvasScreen
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(items) { item ->
                GalleryCard(
                    item = item,
                    onClick = { previewUri = item.uri }
                )
            }
        }
    }

    if (previewUri != null) {
        Dialog(onDismissRequest = { previewUri = null }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                AsyncImage(
                    model = previewUri,
                    contentDescription = "预览图片",
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                )
                IconButton(
                    onClick = { previewUri = null },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "关闭",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun GalleryCard(
    item: GalleryItem,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = LocalCanvasShapes.CardLarge,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(3f / 4f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            AsyncImage(
                model = item.uri,
                contentDescription = item.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun requiredPermission(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        android.Manifest.permission.READ_MEDIA_IMAGES
    } else {
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    }
}

private fun checkPermission(context: Context): Boolean {
    val permission = requiredPermission()
    return androidx.core.content.ContextCompat.checkSelfPermission(
        context,
        permission
    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
}

private fun loadGalleryItems(context: Context): List<GalleryItem> {
    val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATE_ADDED,
        MediaStore.Images.Media.RELATIVE_PATH,
        MediaStore.Images.Media.DATA
    )

    val selection: String?
    val selectionArgs: Array<String>?
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        selection = "${MediaStore.Images.Media.RELATIVE_PATH}=?"
        selectionArgs = arrayOf("Pictures/LocalCanvas/")
    } else {
        selection = "${MediaStore.Images.Media.DATA} LIKE ?"
        selectionArgs = arrayOf("%/Pictures/LocalCanvas/%")
    }

    val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
    val resolver = context.contentResolver
    val items = mutableListOf<GalleryItem>()

    resolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        selection,
        selectionArgs,
        sortOrder
    )?.use { cursor ->
        val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
        val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idCol)
            val name = cursor.getString(nameCol) ?: "LocalCanvas"
            val date = cursor.getLong(dateCol) * 1000L
            val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            val meta = parseMetaFromName(name)
            val timeText = java.text.SimpleDateFormat(
                "yyyy-MM-dd HH:mm",
                java.util.Locale.getDefault()
            ).format(java.util.Date(date))
            val subtitle = if (meta != null) {
                "${meta.workflowLabel} · 强度${meta.strength} · 步数${meta.steps} · 保存于 $timeText"
            } else {
                "保存于 $timeText"
            }
            items.add(
                GalleryItem(
                    id = id,
                    uri = uri,
                    title = "LocalCanvas",
                    subtitle = subtitle
                )
            )
        }
    }

    return items
}

private data class GalleryMeta(
    val workflowLabel: String,
    val strength: Int,
    val steps: Int
)

private fun parseMetaFromName(name: String): GalleryMeta? {
    val regex = Regex("^LC_(local|global)_s(\\d+)_steps(\\d+)_\\d{8}_\\d{6}\\.png$")
    val match = regex.find(name) ?: return null
    val type = match.groupValues[1]
    val strength = match.groupValues[2].toIntOrNull() ?: return null
    val steps = match.groupValues[3].toIntOrNull() ?: return null
    val label = if (type == "local") "本地增强" else "全局重构"
    return GalleryMeta(label, strength, steps)
}
