package com.example.mylocalcanvas.ui.workflow

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.graphicsLayer
import com.example.mylocalcanvas.ui.components.BouncyButton
import com.example.mylocalcanvas.ui.common.LocalCanvasScreen
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import com.example.mylocalcanvas.data.SiliconFlowRepository
import com.example.mylocalcanvas.util.encodeDrawableToBase64
import com.example.mylocalcanvas.util.encodeUriToBase64
import kotlinx.coroutines.launch
import android.net.Uri
import com.example.mylocalcanvas.data.TaskHistoryItem
import com.example.mylocalcanvas.data.TaskHistoryStore
import com.example.mylocalcanvas.data.TaskStatus
import java.util.UUID



data class WorkflowOption(
    val id: String,
    val title: String,
    val subtitle: String
)

@Composable
fun WorkflowScreen(
    onBack: () -> Unit = {},
    maskBase64: String?,
    inputImageUri: Uri?,
    inputImageRes: Int,
    onGenerate: (String, String, Int, Int) -> Unit = { _, _, _, _ -> }
) {
    // 定义两个工作流选项
    val workflows = listOf(
        WorkflowOption(
            id = "local",
            title = "本地增强工作流",
            subtitle = "保持整体构图不变，只对局部进行修饰、美化和风格增强。"
        ),
        WorkflowOption(
            id = "global",
            title = "全局重构工作流",
            subtitle = "根据遮罩和提示语，对整张图进行重新布局和再创作。"
        )
    )

    var selectedWorkflowId by remember { mutableStateOf("local") }
    var strength by remember { mutableStateOf(0.7f) }
    var steps by remember { mutableStateOf(25f) }
    var useLora by remember { mutableStateOf(true) }
    var prompt by remember { mutableStateOf(TextFieldValue("cinematic, soft light, high detail")) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { SiliconFlowRepository.instance }

    LocalCanvasScreen {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // ===== 顶部：返回 + 标题 =====
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 左侧返回按钮
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }

                    Spacer(Modifier.width(4.dp))

                    Column {
                        Text(
                            text = "AI 工作流",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "选择生成方案并调整生成参数",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // ===== 上半部分：工作流列表 + 参数 =====
                Column(
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Text(
                        text = "选择一个工作流方案",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(8.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(workflows) { wf ->
                            WorkflowCard(
                                option = wf,
                                selected = wf.id == selectedWorkflowId,
                                onClick = { selectedWorkflowId = wf.id }
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "生成参数",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = prompt,
                        onValueChange = { prompt = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Prompt（风格描述）") }
                    )

                    Spacer(Modifier.height(12.dp))

                    // 强度 Slider
                    Text("风格强度：${(strength * 100).toInt()}%")
                    Slider(
                        value = strength,
                        onValueChange = { strength = it },
                        valueRange = 0.1f..1.0f
                    )

                    Spacer(Modifier.height(8.dp))

                    // 步数 Slider
                    Text("采样步数：${steps.toInt()}")
                    Slider(
                        value = steps,
                        onValueChange = { steps = it },
                        valueRange = 10f..50f
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Switch(
                            checked = useLora,
                            onCheckedChange = { useLora = it }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("启用 LoRA / 高级风格模块")
                    }

                    if (errorMessage != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // ===== 底部：开始生成按钮 =====
            BouncyButton(
                onClick = {
                    if (isLoading) return@BouncyButton
                    coroutineScope.launch {
                        isLoading = true
                        errorMessage = null
                        val taskId = UUID.randomUUID().toString()
                        val startTime = System.currentTimeMillis()
                        TaskHistoryStore.upsert(
                            context,
                            TaskHistoryItem(
                                id = taskId,
                                title = if (selectedWorkflowId == "local") "本地增强工作流" else "全局重构工作流",
                                workflowType = if (selectedWorkflowId == "local") "本地增强" else "全局重构",
                                strength = (strength * 100).toInt(),
                                steps = steps.toInt(),
                                timeMillis = startTime,
                                status = TaskStatus.RUNNING
                            )
                        )
                        try {
                            val imageBase64 = if (inputImageUri != null) {
                                encodeUriToBase64(context, inputImageUri)
                            } else {
                                encodeDrawableToBase64(context, inputImageRes)
                            }
                            val url = repository.generateImage(prompt.text, imageBase64, maskBase64)
                            TaskHistoryStore.upsert(
                                context,
                                TaskHistoryItem(
                                    id = taskId,
                                    title = if (selectedWorkflowId == "local") "本地增强工作流" else "全局重构工作流",
                                    workflowType = if (selectedWorkflowId == "local") "本地增强" else "全局重构",
                                    strength = (strength * 100).toInt(),
                                    steps = steps.toInt(),
                                    timeMillis = startTime,
                                    status = TaskStatus.COMPLETED,
                                    resultUrl = url
                                )
                            )
                            onGenerate(
                                url,
                                selectedWorkflowId,
                                (strength * 100).toInt(),
                                steps.toInt()
                            )
                        } catch (e: Exception) {
                            TaskHistoryStore.upsert(
                                context,
                                TaskHistoryItem(
                                    id = taskId,
                                    title = if (selectedWorkflowId == "local") "本地增强工作流" else "全局重构工作流",
                                    workflowType = if (selectedWorkflowId == "local") "本地增强" else "全局重构",
                                    strength = (strength * 100).toInt(),
                                    steps = steps.toInt(),
                                    timeMillis = startTime,
                                    status = TaskStatus.FAILED
                                )
                            )
                            errorMessage = e.message ?: "生成失败，请稍后重试"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 8.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("生成中...")
                } else {
                    Text("开始生成结果预览")
                }
            }
        }
    }
}


@Composable
private fun WorkflowCard(
    option: WorkflowOption,
    selected: Boolean,
    onClick: () -> Unit
) {
    // 根据是否选中，目标 scale 值不同
    val targetScale = if (selected) 1.03f else 1f
    val scale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spring(
            dampingRatio = 0.7f,
            stiffness = 300f
        ),
        label = "workflowCardScale"
    )

    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (selected)
                MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (selected) 12.dp else 2.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale
            )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = option.title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = option.subtitle,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
