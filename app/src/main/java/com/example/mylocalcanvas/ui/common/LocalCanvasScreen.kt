package com.example.mylocalcanvas.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mylocalcanvas.ui.common.LocalCanvasScreen


/**
 * 所有页面统一使用的基础布局容器：
 * - 处理 Scaffold 的 innerPadding（TopAppBar / BottomBar 占用的部分）
 * - 统一左右 16.dp，上下 12.dp 的边距
 */
@Composable
fun LocalCanvasScreen(
    innerPadding: PaddingValues = PaddingValues(),
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .padding(innerPadding)
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        content = content
    )
}
