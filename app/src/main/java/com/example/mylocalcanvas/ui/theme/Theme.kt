package com.example.mylocalcanvas.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// 这里用你在 Color.kt 里定义的颜色名
private val DarkColorScheme = darkColorScheme(
    primary = CanvasPrimary,
    secondary = CanvasSecondary,
    background = CanvasDark,
    surface = CanvasSurface,
    onPrimary = CanvasOnPrimary,
    onSecondary = CanvasOnDark,
    onBackground = CanvasOnDark,
    onSurface = CanvasOnDark,
)

/**
 * 注意：函数名叫 LocalCanvasTheme
 */
@Composable
fun LocalCanvasTheme(
    useDarkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme // 这里先固定深色

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Type.kt 里默认生成的
        content = content
    )
}
