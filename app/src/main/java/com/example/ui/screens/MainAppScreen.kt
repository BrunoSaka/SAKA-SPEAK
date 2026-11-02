package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EvaluationDialog
import com.example.ui.components.SakaTopBar
import com.example.ui.theme.SakaBorderGold
import com.example.ui.theme.SakaCream
import com.example.ui.theme.SakaCreamDim
import com.example.ui.theme.SakaDark
import com.example.ui.theme.SakaDarkSoft
import com.example.ui.theme.SakaGold
import com.example.ui.theme.SakaMatchaLight
import com.example.ui.viewmodel.SakaViewModel
import com.example.ui.viewmodel.TimerPhase

enum class AppTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    PRACTICE("Latihan", Icons.Filled.Mic, Icons.Outlined.Mic),
    CATALOG("Katalog", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome),
    HISTORY("Riwayat", Icons.Filled.History, Icons.Outlined.History),
    TIPS("Panduan", Icons.Filled.Lightbulb, Icons.Outlined.Lightbulb)
}

@Composable
fun MainAppScreen(
    viewModel: SakaViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(AppTab.PRACTICE) }
    var isInTimerMode by remember { mutableStateOf(false) }

    val timerPhase by viewModel.timerPhase.collectAsState()
    val completedInfo by viewModel.completedPracticeInfo.collectAsState()
    val isPlayingAudio by viewModel.isPlayingAudio.collectAsState()
    val playbackProgress by viewModel.playbackProgress.collectAsState()

    // Handle back stack navigation
    BackHandler(enabled = selectedTab != AppTab.PRACTICE && !isInTimerMode) {
        selectedTab = AppTab.PRACTICE
    }

    val isTimerActive = isInTimerMode || timerPhase == TimerPhase.SPEAKING || timerPhase == TimerPhase.PREPARATION || timerPhase == TimerPhase.PAUSED

    AnimatedContent(
        targetState = isTimerActive,
        transitionSpec = {
            if (targetState) {
                (slideInVertically(
                    animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow),
                    initialOffsetY = { it / 3 }
                ) + fadeIn(tween(280, easing = FastOutSlowInEasing)) + scaleIn(
                    initialScale = 0.95f,
                    animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow)
                )).togetherWith(
                    fadeOut(tween(180, easing = FastOutSlowInEasing)) + scaleOut(
                        targetScale = 0.95f,
                        animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow)
                    )
                )
            } else {
                (fadeIn(tween(280, easing = FastOutSlowInEasing)) + scaleIn(
                    initialScale = 0.95f,
                    animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow)
                )).togetherWith(
                    slideOutVertically(
                        animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow),
                        targetOffsetY = { it / 3 }
                    ) + fadeOut(tween(200, easing = FastOutSlowInEasing)) + scaleOut(
                        targetScale = 0.95f,
                        animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow)
                    )
                )
            }
        },
        label = "timer_mode_transition"
    ) { showTimer ->
        if (showTimer) {
            TimerScreen(
                viewModel = viewModel,
                onCloseTimer = { isInTimerMode = false }
            )
        } else {
            Scaffold(
                modifier = modifier.fillMaxSize(),
                topBar = {
                    SakaTopBar()
                },
                bottomBar = {
                    NavigationBar(
                        containerColor = SakaDarkSoft,
                        contentColor = SakaGold,
                        tonalElevation = 8.dp,
                        modifier = Modifier
                            .testTag("bottom_nav_bar")
                            .windowInsetsPadding(WindowInsets.navigationBars)
                    ) {
                        AppTab.entries.forEach { tab ->
                            val isSelected = selectedTab == tab
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { selectedTab = tab },
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = tab.title
                                    )
                                },
                                label = {
                                    Text(
                                        text = tab.title,
                                        fontSize = 11.5.sp
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = SakaDark,
                                    selectedTextColor = SakaGold,
                                    indicatorColor = SakaGold,
                                    unselectedIconColor = SakaCreamDim,
                                    unselectedTextColor = SakaCreamDim
                                ),
                                modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                            )
                        }
                    }
                },
                containerColor = SakaDark
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    AnimatedContent(
                        targetState = selectedTab,
                        transitionSpec = {
                            val direction = if (targetState.ordinal > initialState.ordinal) 1 else -1
                            (slideInHorizontally(
                                animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow),
                                initialOffsetX = { fullWidth -> direction * (fullWidth / 4) }
                            ) + fadeIn(tween(260, easing = FastOutSlowInEasing)) + scaleIn(
                                initialScale = 0.97f,
                                animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow)
                            )).togetherWith(
                                slideOutHorizontally(
                                    animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow),
                                    targetOffsetX = { fullWidth -> -direction * (fullWidth / 4) }
                                ) + fadeOut(tween(180, easing = FastOutSlowInEasing)) + scaleOut(
                                    targetScale = 0.97f,
                                    animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow)
                                )
                            )
                        },
                        label = "tab_transition"
                    ) { targetTab ->
                        when (targetTab) {
                            AppTab.PRACTICE -> {
                                PracticeScreen(
                                    viewModel = viewModel,
                                    onStartPractice = {
                                        isInTimerMode = true
                                        viewModel.startSpeechTimer()
                                    }
                                )
                            }
                            AppTab.CATALOG -> {
                                CatalogScreen(
                                    viewModel = viewModel,
                                    onTopicSelected = {
                                        selectedTab = AppTab.PRACTICE
                                    }
                                )
                            }
                            AppTab.HISTORY -> {
                                HistoryScreen(viewModel = viewModel)
                            }
                            AppTab.TIPS -> {
                                TipsScreen()
                            }
                        }
                    }
                }
            }
        }
    }

    // Evaluation Dialog overlay when speech session ends
    if (completedInfo != null) {
        val info = completedInfo!!
        EvaluationDialog(
            topicTitle = info.topicTitle,
            category = info.category,
            actualSeconds = info.actualSeconds,
            targetSeconds = info.targetSeconds,
            audioFilePath = info.audioFilePath,
            aiAssessment = info.aiAssessment,
            isPlayingAudio = isPlayingAudio,
            audioPlaybackProgress = playbackProgress,
            onPlayAudio = { path -> viewModel.playAudio(path) },
            onPauseAudio = { viewModel.pauseAudio() },
            onSave = { rStruct, rClarity, rPacing, rConf, notes ->
                viewModel.saveCompletedSession(rStruct, rClarity, rPacing, rConf, notes)
                isInTimerMode = false
            },
            onDismiss = {
                viewModel.dismissEvaluationDialog()
                isInTimerMode = false
            }
        )
    }
}
