package com.example.mylocalcanvas.ui.gallery

import com.example.mylocalcanvas.ui.common.LocalCanvasShapes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.mylocalcanvas.ui.common.LocalCanvasScreen


// 假数据模型
data class GalleryItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val workflowType: String, // "local" / "global"
    val colorStart: Color,
    val colorEnd: Color
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun GalleryScreen() {
    // 假数据：用于课程展示
    val allItems = remember {
        listOf(
            GalleryItem(
                id = "1",
                title = "街景局部修复",
                subtitle = "本地增强 · 2025-11-17",
                workflowType = "local",
                colorStart = Color(0xFF7C4DFF),
                colorEnd = Color(0xFF536DFE)
            ),
            GalleryItem(
                id = "2",
                title = "梦幻城市重构",
                subtitle = "全局重构 · 2025-11-16",
                workflowType = "global",
                colorStart = Color(0xFFEC407A),
                colorEnd = Color(0xFFFFA726)
            ),
            GalleryItem(
                id = "3",
                title = "人像肤质优化",
                subtitle = "本地增强 · 2025-11-15",
                workflowType = "local",
                colorStart = Color(0xFF26C6DA),
                colorEnd = Color(0xFF00ACC1)
            ),
            GalleryItem(
                id = "4",
                title = "科幻室内重绘",
                subtitle = "全局重构 · 2025-11-12",
                workflowType = "global",
                colorStart = Color(0xFFAB47BC),
                colorEnd = Color(0xFF5C6BC0)
            )
        )
    }

    val filters = listOf("全部", "增强", "重构")
    var selectedFilterIndex by remember { mutableStateOf(0) }

    // 按筛选条件过滤
    val filteredItems by remember(selectedFilterIndex) {
        mutableStateOf(
            when (selectedFilterIndex) {
                1 -> allItems.filter { it.workflowType == "local" }
                2 -> allItems.filter { it.workflowType == "global" }
                else -> allItems
            }
        )
    }

    // ✅ 直接用我们统一的页面容器
    LocalCanvasScreen {

        // 页面标题（效果类似首页“开始创作”那样）
        Text(
            text = "图库",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "按工作流筛选",
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(Modifier.height(8.dp))

        SingleChoiceSegmentedButtonRow {
            filters.forEachIndexed { index, label ->
                SegmentedButton(
                    selected = index == selectedFilterIndex,
                    onClick = { selectedFilterIndex = index },
                    shape = SegmentedButtonDefaults.itemShape(index, filters.size),
                    label = { Text(label) }
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredItems) { item ->
                GalleryCard(item = item)
            }
        }
    }

}

@Composable
private fun GalleryCard(
    item: GalleryItem
) {
    Card(
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
            // 上方：缩略图占位（渐变背景）
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(item.colorStart, item.colorEnd)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "生成预览",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(6.dp))

            // 标题 + 描述
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
