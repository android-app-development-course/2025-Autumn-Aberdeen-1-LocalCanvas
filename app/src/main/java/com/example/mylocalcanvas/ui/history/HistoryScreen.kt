package com.example.mylocalcanvas.ui.history

import com.example.mylocalcanvas.ui.common.LocalCanvasShapes
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import com.example.mylocalcanvas.ui.common.LocalCanvasScreen


enum class TaskStatus {
    RUNNING,
    COMPLETED,
    FAILED
}

data class TaskItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val timeInfo: String,
    val status: TaskStatus
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen() {
    // 假数据：展示不同状态的任务
    val tasks = remember {
        listOf(
            TaskItem(
                id = "t1",
                title = "梦幻城市重构",
                subtitle = "全局重构 · 步数 30 · 强度 80%",
                timeInfo = "正在生成 · 预计还需 20 秒",
                status = TaskStatus.RUNNING
            ),
            TaskItem(
                id = "t2",
                title = "街景局部修复",
                subtitle = "本地增强 · 步数 25 · 强度 60%",
                timeInfo = "完成于 2025-11-16 14:32",
                status = TaskStatus.COMPLETED
            ),
            TaskItem(
                id = "t3",
                title = "人像艺术风格化",
                subtitle = "全局重构 · 步数 40 · 强度 90%",
                timeInfo = "失败：网络断开，请稍后重试",
                status = TaskStatus.FAILED
            )
        )
    }

    // ✅ 统一使用 LocalCanvasScreen，和首页/图库一致
    LocalCanvasScreen {

        // 顶部主标题
        Text(
            text = "历史任务",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(16.dp))

        // 小标题
        Text(
            text = "最近的生成任务",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))

        // 列表
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(tasks) { task ->
                TaskCard(task = task)
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }

}

@Composable
private fun TaskCard(task: TaskItem) {
    val (icon, statusText, statusColor) = when (task.status) {
        TaskStatus.RUNNING -> Triple(
            Icons.Default.Schedule,
            "进行中",
            MaterialTheme.colorScheme.primary
        )
        TaskStatus.COMPLETED -> Triple(
            Icons.Default.CheckCircle,
            "已完成",
            Color(0xFF4CAF50)
        )
        TaskStatus.FAILED -> Triple(
            Icons.Default.ErrorOutline,
            "失败",
            Color(0xFFFF7043)
        )
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = LocalCanvasShapes.CardMedium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // 第一行：标题 + 状态
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = statusColor
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.bodySmall,
                        color = statusColor
                    )
                }
            }

            // 第二行：配置说明
            Text(
                text = task.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // 第三行：时间 / 错误信息
            Text(
                text = task.timeInfo,
                style = MaterialTheme.typography.bodySmall
            )

            // 进行中任务显示一个进度条
            if (task.status == TaskStatus.RUNNING) {
                Spacer(Modifier.height(6.dp))
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant
                        )
                )
            }
        }
    }
}
