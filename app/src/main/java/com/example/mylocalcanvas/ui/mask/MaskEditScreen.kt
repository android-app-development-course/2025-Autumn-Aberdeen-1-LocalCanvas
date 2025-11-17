package com.example.mylocalcanvas.ui.mask

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mylocalcanvas.ui.common.LocalCanvasScreen
import com.example.mylocalcanvas.ui.components.BouncyButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

@Composable
fun MaskEditScreen(
    onBack: () -> Unit = {},
    onNext: () -> Unit = {}
) {
    LocalCanvasScreen {

        // 顶部标题 + 返回 / 下一步
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }


                Spacer(Modifier.width(8.dp))

                Column {
                    Text(
                        text = "遮罩编辑",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "用手指在图片上涂抹需要编辑的区域",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 右上角“下一步”快捷入口
            Text(
                text = "下一步",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        Spacer(Modifier.height(8.dp))

        // 图片 + 遮罩画布区域（占位）
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp) // 固定高度，避免受 weight / 外部布局影响
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF222222)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "这里是图片和遮罩画布\n（后面可以扩展为真正的画笔）",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(Modifier.height(20.dp))

        // 工具栏标题
        Text(
            text = "遮罩工具",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 工具按钮行
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MaskToolButton(
                icon = Icons.Default.Brush,
                label = "画笔"
            ) {
                // TODO: 切换到画笔模式
            }

            MaskToolButton(
                icon = Icons.Default.Visibility,
                label = "预览"
            ) {
                // TODO: 显示/隐藏遮罩
            }

            MaskToolButton(
                icon = Icons.Default.Undo,
                label = "撤销"
            ) {
                // TODO: 撤销上一步
            }

            MaskToolButton(
                icon = Icons.Default.Delete,
                label = "清除"
            ) {
                // TODO: 清除当前遮罩
            }
        }

        Spacer(Modifier.height(20.dp))

        // 底部主按钮
        BouncyButton(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("下一步：选择 AI 工作流")
        }

        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun MaskToolButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
