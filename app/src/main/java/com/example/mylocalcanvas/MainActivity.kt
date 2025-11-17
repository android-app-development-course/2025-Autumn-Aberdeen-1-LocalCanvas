package com.example.mylocalcanvas

import com.example.mylocalcanvas.ui.result.ResultScreen
import com.example.mylocalcanvas.ui.workflow.WorkflowScreen
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mylocalcanvas.ui.home.HomeScreen
import com.example.mylocalcanvas.ui.theme.LocalCanvasTheme
import com.example.mylocalcanvas.ui.mask.MaskEditScreen
import com.example.mylocalcanvas.ui.gallery.GalleryScreen
import com.example.mylocalcanvas.ui.history.HistoryScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.animation.core.tween
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.example.mylocalcanvas.ui.theme.CanvasPrimary
import kotlinx.coroutines.delay




// 简单的路由常量
const val ROUTE_HOME = "home"
const val ROUTE_GALLERY = "gallery"
const val ROUTE_HISTORY = "history"

const val ROUTE_MASK = "mask"
const val ROUTE_WORKFLOW = "workflow"
const val ROUTE_RESULT = "result"


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LocalCanvasTheme {
                LocalCanvasApp()
            }
        }
    }
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalCanvasApp() {
    val navController = rememberNavController()

    val bottomItems = listOf(
        BottomNavItem(ROUTE_HOME, "创作", Icons.Default.Home),
        BottomNavItem(ROUTE_GALLERY, "图库", Icons.Default.Image),
        BottomNavItem(ROUTE_HISTORY, "任务", Icons.Default.History)
    )




    Scaffold(
        bottomBar = {
            BottomNavBar(
                navController = navController,
                items = bottomItems
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ROUTE_HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(ROUTE_HOME) {
                HomeScreen(
                    onStartMaskEdit = {navController.navigate(ROUTE_MASK)},
                    onOpenGallery = { navController.navigate(ROUTE_GALLERY) }
                )
            }

            // 🔽 新增：遮罩编辑页
            composable(ROUTE_MASK) {
                MaskEditScreen(
                    onBack = { navController.popBackStack() },
                    onNext = { navController.navigate(ROUTE_WORKFLOW) }
                )
            }

            // 🔽 新增：简单的工作流占位页（下一步我们会完善）
            composable(ROUTE_WORKFLOW) {
                WorkflowScreen(
                    onBack = { navController.popBackStack() },
                    onGenerate = { navController.navigate(ROUTE_RESULT) }
                )
            }

            composable(ROUTE_GALLERY) {
                GalleryScreen()
            }
            composable(ROUTE_HISTORY) {
                HistoryScreen()
            }


            composable(ROUTE_RESULT) {
                ResultScreen(
                    onBack = { navController.popBackStack() },
                    onSave = {
                        // 这里以后可以接入真正的保存逻辑
                        navController.popBackStack(ROUTE_HOME, false)
                    },
                    onRegenerate = {
                        // 回到工作流页重新选择参数
                        navController.popBackStack(ROUTE_WORKFLOW, false)
                    }
                )
            }




        }
    }
}

@Composable
fun BottomNavBar(
    navController: NavHostController,
    items: List<BottomNavItem>
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 判断当前是否在底部导航的任一页面（排除遮罩、工作流、结果等中间页面）
    val isOnBottomNavRoute = remember(currentRoute) {
        currentRoute in items.map { it.route }
    }

    // 动画颜色：如果在底部导航页面，显示紫色；否则显示默认背景色
    val targetColor = if (isOnBottomNavRoute) {
        CanvasPrimary.copy(alpha = 0.15f) // 半透明紫色，更柔和
    } else {
        Color.Transparent
    }

    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = 300),
        label = "bottomNavBackgroundColor"
    )
    
    Box(
        modifier = Modifier
            .background(animatedColor)
    ) {
        NavigationBar {
            items.forEach { item ->
                NavigationBarItem(
                    selected = currentRoute == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            launchSingleTop = true
                            popUpTo(ROUTE_HOME)
                        }
                    },
                    icon = { Icon(item.icon, contentDescription = item.label) },
                    label = { Text(item.label) }
                )
            }
        }
    }
}

// ------- 占位页面：图库 / 历史任务 -------


