package com.example.mylocalcanvas.ui.home

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mylocalcanvas.ui.components.BouncyButton
import com.example.mylocalcanvas.ui.components.BouncyOutlinedButton
import androidx.compose.foundation.layout.width


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onStartMaskEdit: () -> Unit,
    onOpenGallery: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            // 整个页面上下左右边距统一压缩一些
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // ① 顶部 App 名称：改成紧凑的标题，不再占一大块区域
        Text(
            text = "LocalCanvas",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(12.dp))

        // ② 主标题区域：稍微大一点，但只占很小的高度
        Text(
            text = "您好：Caleb",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "拍摄或导入图片，使用用遮罩和 AI 工作流进行自由艺术创作。",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(24.dp))

        // ③ 主操作按钮区（保持你之前的两个按钮 + 弹跳动效）
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            BouncyButton(
                onClick = onStartMaskEdit,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("拍摄")
            }

            BouncyOutlinedButton(
                onClick = onStartMaskEdit,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Image, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("导入")
            }
        }

        Spacer(Modifier.height(32.dp))

        // ④ 最近作品标题
        Text(
            text = "最近作品",
            style = MaterialTheme.typography.titleSmall
        )

        Spacer(Modifier.height(12.dp))

        // ⑤ 打开图库按钮（保持你之前的样式）
        OutlinedButton(
            onClick = onOpenGallery,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
        ) {
            Text("打开我的图库")
        }

        // 下面的空间就留给将来要加的“最近几张缩略图”等
    }
}
