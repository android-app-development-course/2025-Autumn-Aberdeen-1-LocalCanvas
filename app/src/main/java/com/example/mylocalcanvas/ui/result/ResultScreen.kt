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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mylocalcanvas.ui.common.LocalCanvasScreen
import com.example.mylocalcanvas.ui.components.BouncyButton
import com.example.mylocalcanvas.ui.components.BouncyOutlinedButton

@Composable
fun ResultScreen(
    onBack: () -> Unit = {},
    onSave: () -> Unit = {},
    onRegenerate: () -> Unit = {}
) {
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
                            Text("这里显示原始图片")
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
                            Text("这里显示最终生成图片")
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
                    onClick = onSave,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("保存到图库")
                }
            }
        }
    }
}
