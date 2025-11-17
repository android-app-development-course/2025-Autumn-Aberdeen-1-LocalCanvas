package com.example.mylocalcanvas

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mylocalcanvas.ui.theme.CanvasDark
import com.example.mylocalcanvas.ui.theme.LocalCanvasTheme
import kotlinx.coroutines.delay

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LocalCanvasTheme {
                SplashScreen {
                    // 延迟后跳转到MainActivity
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                }
            }
        }
    }
}

@Composable
fun SplashScreen(
    onNavigateToMain: () -> Unit
) {
    // 动画状态
    var startAnimation by remember { mutableStateOf(false) }
    
    // Logo的alpha和scale动画
    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1500,
            easing = FastOutSlowInEasing
        ),
        label = "logoAlpha"
    )
    
    val logoScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logoScale"
    )
    
    // 文字的alpha动画（稍微延迟一点）
    val textAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1200,
            delayMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "textAlpha"
    )
    
    // 启动动画
    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2000) // 显示2秒后跳转
        onNavigateToMain()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CanvasDark),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo图片
            Image(
                painter = painterResource(id = R.drawable.localcanvas4),
                contentDescription = "LocalCanvas Logo",
                modifier = Modifier
                    .size(200.dp)
                    .alpha(logoAlpha)
                    .scale(logoScale),
                contentScale = ContentScale.Fit
            )
            
            Spacer(Modifier.height(24.dp))
            
            // LocalCanvas 文字
            Text(
                text = "LocalCanvas",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.alpha(textAlpha)
            )
        }
    }
}

