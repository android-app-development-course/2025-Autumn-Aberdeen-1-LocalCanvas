package com.example.mylocalcanvas.ui.home

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mylocalcanvas.R
import com.example.mylocalcanvas.ui.components.BouncyButton
import com.example.mylocalcanvas.ui.components.BouncyOutlinedButton
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import com.example.mylocalcanvas.util.saveBitmapToCache


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onImageSelected: (android.net.Uri) -> Unit,
    onOpenGallery: () -> Unit
) {
    val context = LocalContext.current
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            onImageSelected(uri)
        }
    }
    val takePreviewLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val uri = saveBitmapToCache(context, bitmap)
            onImageSelected(uri)
        }
    }

    // 底部弹出卡片的状态
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    // 底部弹出卡片内容
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            ActionOptionsSheet(
                onCapture = {
                    showBottomSheet = false
                    takePreviewLauncher.launch(null)
                },
                onPickImage = {
                    showBottomSheet = false
                    pickImageLauncher.launch("image/*")
                },
                onOpenGallery = {
                    showBottomSheet = false
                    onOpenGallery()
                }
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // ① 顶部 App Logo：使用图片代替文字
            Image(
                painter = painterResource(id = R.drawable.localcanvas4),
                contentDescription = "LocalCanvas Logo",
                modifier = Modifier
                    .height(120.dp)
                    .width(360.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(Modifier.height(32.dp))

            // ② 主标题区域：稍微大一点，但只占很小的高度
            Text(
                text = "您好：Caleb",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "拍摄或导入图片，使用用遮罩和 AI 工作流进行自由艺术创作。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            // ③ 触发底部弹出卡片的按钮
            BouncyButton(
                onClick = { showBottomSheet = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("开始创作")
            }
        }
    }
}

/**
 * 底部弹出卡片内容：包含拍摄、导入、打开图库三个操作
 */
@Composable
private fun ActionOptionsSheet(
    onCapture: () -> Unit,
    onPickImage: () -> Unit,
    onOpenGallery: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 卡片标题


        // 拍摄按钮
        BouncyButton(
            onClick = onCapture,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.CameraAlt, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("拍摄")
        }

        // 导入按钮
        BouncyOutlinedButton(
            onClick = onPickImage,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Image, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("导入")
        }

        // 打开图库按钮
        OutlinedButton(
            onClick = onOpenGallery,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("打开我的图库")
        }

        // 底部留白，避免被系统导航栏遮挡
        Spacer(Modifier.height(16.dp))
    }
}
