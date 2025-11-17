package com.example.mylocalcanvas

import com.example.mylocalcanvas.ui.result.ResultScreen
import com.example.mylocalcanvas.ui.workflow.WorkflowScreen
import androidx.compose.material3.ExperimentalMaterial3Api
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import androidx.compose.animation.core.tween
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

    // 判断当前是否在底部导航的任一页面
    val isOnBottomNavRoute = remember(currentRoute) {
        currentRoute in items.map { it.route }
    }
    
    // 点击高亮状态：用于在切换时显示短暂的胶囊高亮
    var showHighlight by remember { mutableStateOf(false) }
    
    // 当路由改变且是底部导航页面时，触发短暂高亮
    LaunchedEffect(currentRoute) {
        if (isOnBottomNavRoute) {
            showHighlight = true
            delay(400) // 高亮持续400ms
            showHighlight = false
        }
    }
    
    // 指示器颜色动画：只在 showHighlight 为 true 时显示
    val indicatorColor by animateColorAsState(
        targetValue = if (showHighlight) {
            CanvasPrimary.copy(alpha = 0.25f) // 高亮时的紫色指示器
        } else {
            Color.Transparent // 高亮结束后透明（消失）
        },
        animationSpec = tween(durationMillis = 300),
        label = "indicatorColor"
    )

    NavigationBar(
        modifier = Modifier.height(72.dp) // 降低高度，默认是80dp，改为56dp
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            
            // 图标颜色：选中时显示主题紫色，未选中时使用默认颜色
            val iconColor by animateColorAsState(
                targetValue = if (isSelected) {
                    CanvasPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                animationSpec = tween(durationMillis = 300),
                label = "iconColor"
            )
            
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                        popUpTo(ROUTE_HOME)
                    }
                },
                icon = { 
                    Icon(
                        imageVector = item.icon, 
                        contentDescription = item.label,
                        tint = iconColor,
                        modifier = Modifier.size(28.dp) // 增大图标尺寸，默认是24dp，改为28dp
                    )
                },
                label = null, // 移除文字标签
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    // 使用动画的指示器颜色：高亮时显示紫色，否则透明
                    indicatorColor = if (isSelected) indicatorColor else Color.Transparent
                )
            )
        }
    }
}

// ------- 占位页面：图库 / 历史任务 -------


