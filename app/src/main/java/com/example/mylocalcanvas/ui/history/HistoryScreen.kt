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
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import com.example.mylocalcanvas.ui.common.LocalCanvasScreen
import com.example.mylocalcanvas.data.TaskHistoryItem
import com.example.mylocalcanvas.data.TaskHistoryStore
import com.example.mylocalcanvas.data.TaskStatus
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.compose.ui.platform.LocalLifecycleOwner
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onOpenResult: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var tasks by remember { mutableStateOf<List<TaskHistoryItem>>(emptyList()) }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            tasks = TaskHistoryStore.load(context)
        }
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
                TaskCard(
                    task = task,
                    onOpenResult = onOpenResult
                )
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }

}

@Composable
private fun TaskCard(
    task: TaskHistoryItem,
    onOpenResult: (String) -> Unit
) {
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

    val subtitle = "${task.workflowType} · 步数 ${task.steps} · 强度 ${task.strength}%"
    val timeText = formatTime(task.timeMillis)
    val timeInfo = when (task.status) {
        TaskStatus.RUNNING -> "正在生成"
        TaskStatus.COMPLETED -> "完成于 $timeText"
        TaskStatus.FAILED -> "失败于 $timeText"
    }

    Card(
        onClick = {
            if (task.status == TaskStatus.COMPLETED && !task.resultUrl.isNullOrBlank()) {
                onOpenResult(task.resultUrl)
            }
        },
        enabled = task.status == TaskStatus.COMPLETED && !task.resultUrl.isNullOrBlank(),
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
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // 第三行：时间 / 错误信息
            Text(
                text = timeInfo,
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

private fun formatTime(timeMillis: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return formatter.format(Date(timeMillis))
}
