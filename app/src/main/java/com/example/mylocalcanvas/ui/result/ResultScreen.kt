package com.example.mylocalcanvas.ui.result

import com.example.mylocalcanvas.ui.common.LocalCanvasShapes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mylocalcanvas.ui.common.LocalCanvasScreen
import com.example.mylocalcanvas.ui.components.BouncyButton
import com.example.mylocalcanvas.ui.components.BouncyOutlinedButton
import coil.compose.AsyncImage
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import com.example.mylocalcanvas.util.saveImageToGallery
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ResultScreen(
    inputImageRes: Int,
    inputImageUri: Uri?,
    resultUrl: String?,
    workflowTypeId: String?,
    strength: Int?,
    steps: Int?,
    onBack: () -> Unit = {},
    onSave: () -> Unit = {},
    onRegenerate: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isSaving by remember { mutableStateOf(false) }
    var saveMessage by remember { mutableStateOf<String?>(null) }

    LocalCanvasScreen {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // ===== 上半部分：头部 + 卡片 =====
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 顶部标题栏（返回 + 标题 + 副标题）
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }

                    Spacer(Modifier.width(4.dp))

                    Column {
                        Text(
                            text = "结果预览",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "对比原图和生成结果，并决定是否保存到图库",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // 分段标题
                Text(
                    text = "预览对比",
                    style = MaterialTheme.typography.titleMedium
                )

                // 原图卡片
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = LocalCanvasShapes.CardLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "原始图像",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(top = 8.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF222222)),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = inputImageUri ?: inputImageRes,
                                contentDescription = "原始图片",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                // 生成结果卡片
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    ),
                    shape = LocalCanvasShapes.CardLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "生成结果",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(top = 8.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF222222)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (resultUrl.isNullOrBlank()) {
                                Text("生成结果尚未返回")
                            } else {
                                AsyncImage(
                                    model = resultUrl,
                                    contentDescription = "生成结果",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }

                // 任务信息卡片
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = LocalCanvasShapes.CardMedium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "本次任务信息",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = "工作流：本地增强工作流（示例）",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "风格强度：70%，采样步数：25",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "LoRA：已启用",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // ===== 底部操作按钮 =====
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BouncyOutlinedButton(
                    onClick = onRegenerate,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Replay, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("重新生成")
                }
                BouncyButton(
                    onClick = {
                        if (isSaving) return@BouncyButton
                        if (resultUrl.isNullOrBlank()) {
                            saveMessage = "暂无可保存的结果图"
                            return@BouncyButton
                        }
                        coroutineScope.launch {
                            isSaving = true
                            saveMessage = null
                            val saved = withContext(Dispatchers.IO) {
                                saveImageToGallery(
                                    context = context,
                                    imageUrl = resultUrl,
                                    workflowTypeId = workflowTypeId,
                                    strength = strength,
                                    steps = steps
                                )
                            }
                            isSaving = false
                            if (saved != null) {
                                saveMessage = "已保存到系统图库"
                                onSave()
                            } else {
                                saveMessage = "保存失败，请稍后重试"
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text(if (isSaving) "保存中..." else "保存到图库")
                }
            }

            if (saveMessage != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = saveMessage ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
