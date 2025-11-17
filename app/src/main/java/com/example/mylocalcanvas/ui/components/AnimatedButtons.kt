package com.example.mylocalcanvas.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp


@Composable
fun BouncyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 400f
        ),
        label = "bouncyButtonScale"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .height(48.dp) // 统一按钮高度
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale
            ),
        enabled = enabled,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(999.dp), // 胶囊按钮
        colors = colors,
        content = content
    )

}

@Composable
fun BouncyOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors =
        ButtonDefaults.outlinedButtonColors(),
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 400f
        ),
        label = "bouncyOutlinedButtonScale"
    )

    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .height(48.dp)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale
            ),
        enabled = enabled,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(999.dp),
        colors = colors,
        content = content
    )


}
