package com.example.mylocalcanvas.ui.mask

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mylocalcanvas.ui.common.LocalCanvasScreen
import com.example.mylocalcanvas.ui.components.BouncyButton
import coil.compose.AsyncImage
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import android.net.Uri
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Canvas as AndroidCanvas
import android.util.Base64
import java.io.ByteArrayOutputStream

@Composable
fun MaskEditScreen(
    onBack: () -> Unit = {},
    inputImageUri: Uri?,
    inputImageRes: Int,
    onMaskReady: (String?) -> Unit = {},
    onNext: () -> Unit = {}
) {
    val strokes = remember { mutableStateListOf<List<Offset>>() }
    var currentStroke by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var brushSize by remember { mutableStateOf(28f) }
    var showMask by remember { mutableStateOf(true) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

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


        }

        Spacer(Modifier.height(8.dp))

        // 图片 + 遮罩画布区域
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp) // 固定高度，避免受 weight / 外部布局影响
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF222222)),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = inputImageUri ?: inputImageRes,
                contentDescription = "待编辑图片",
                modifier = Modifier.fillMaxSize()
            )
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged { canvasSize = it }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                currentStroke = listOf(offset)
                            },
                            onDrag = { change, _ ->
                                currentStroke = currentStroke + change.position
                            },
                            onDragEnd = {
                                if (currentStroke.isNotEmpty()) {
                                    strokes.add(currentStroke)
                                }
                                currentStroke = emptyList()
                            },
                            onDragCancel = {
                                currentStroke = emptyList()
                            }
                        )
                    }
            ) {
                if (showMask) {
                    val paintColor = Color(0xFF7C4DFF).copy(alpha = 0.45f)
                    strokes.forEach { points ->
                        drawStroke(points, brushSize, paintColor)
                    }
                    drawStroke(currentStroke, brushSize, paintColor)
                }
            }
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
                showMask = true
            }

            MaskToolButton(
                icon = Icons.Default.Visibility,
                label = "预览"
            ) {
                showMask = !showMask
            }

            MaskToolButton(
                icon = Icons.Default.Undo,
                label = "撤销"
            ) {
                if (strokes.isNotEmpty()) {
                    strokes.removeAt(strokes.size - 1)
                }
            }

            MaskToolButton(
                icon = Icons.Default.Delete,
                label = "清除"
            ) {
                strokes.clear()
                currentStroke = emptyList()
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = "笔刷大小：${brushSize.toInt()}",
            style = MaterialTheme.typography.bodySmall
        )
        Slider(
            value = brushSize,
            onValueChange = { brushSize = it },
            valueRange = 8f..64f
        )

        Spacer(Modifier.height(20.dp))

        // 底部主按钮
        BouncyButton(
            onClick = {
                val maskBase64 = createMaskBase64(strokes, canvasSize, brushSize)
                onMaskReady(maskBase64)
                onNext()
            },
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

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawStroke(
    points: List<Offset>,
    brushSize: Float,
    color: Color
) {
    if (points.size < 2) return
    val path = Path().apply { moveTo(points.first().x, points.first().y) }
    for (point in points.drop(1)) {
        path.lineTo(point.x, point.y)
    }
    drawPath(
        path = path,
        color = color,
        style = Stroke(
            width = brushSize,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
}

private fun createMaskBase64(
    strokes: List<List<Offset>>,
    canvasSize: IntSize,
    brushSize: Float
): String? {
    if (strokes.isEmpty() || canvasSize.width <= 0 || canvasSize.height <= 0) return null

    val bitmap = Bitmap.createBitmap(
        canvasSize.width,
        canvasSize.height,
        Bitmap.Config.ARGB_8888
    )
    val canvas = AndroidCanvas(bitmap)
    val paint = Paint().apply {
        color = android.graphics.Color.WHITE
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        strokeWidth = brushSize
        isAntiAlias = true
    }

    for (stroke in strokes) {
        if (stroke.size < 2) continue
        val path = android.graphics.Path().apply {
            moveTo(stroke.first().x, stroke.first().y)
            for (point in stroke.drop(1)) {
                lineTo(point.x, point.y)
            }
        }
        canvas.drawPath(path, paint)
    }

    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    val base64 = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    return "data:image/png;base64,$base64"
}
