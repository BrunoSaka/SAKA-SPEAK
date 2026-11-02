package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.ui.draw.rotate
import com.example.ui.components.RollingTopicWheel
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.model.Topic
import com.example.ui.components.MegaMendungBackground
import com.example.ui.theme.SakaBorderGold
import com.example.ui.theme.SakaBorderSubtle
import com.example.ui.theme.SakaBrown
import com.example.ui.theme.SakaCream
import com.example.ui.theme.SakaCreamDim
import com.example.ui.theme.SakaCreamMuted
import com.example.ui.theme.SakaDark
import com.example.ui.theme.SakaDarkCard
import com.example.ui.theme.SakaDarkSoft
import com.example.ui.theme.SakaGold
import com.example.ui.theme.SakaGoldDim
import com.example.ui.theme.SakaGoldLight
import com.example.ui.theme.SakaMatcha
import com.example.ui.theme.SakaMatchaDark
import com.example.ui.theme.SakaMatchaLight
import com.example.ui.theme.SakaTagBg
import com.example.ui.theme.SakaWhite
import com.example.ui.viewmodel.SakaViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PracticeScreen(
    viewModel: SakaViewModel,
    onStartPractice: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentTopic by viewModel.currentTopic.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val targetSpeechSeconds by viewModel.targetSpeechSeconds.collectAsState()
    val isPrepEnabled by viewModel.isPrepEnabled.collectAsState()
    val isAudioRecordEnabled by viewModel.isAudioRecordEnabled.collectAsState()
    val savedTopics by viewModel.savedTopics.collectAsState()

    var showCustomTopicDialog by remember { mutableStateOf(false) }
    var hasGeneratedTopic by remember { mutableStateOf(false) }

    var isRollingTopic by remember { mutableStateOf(false) }
    var targetRollingTopic by remember { mutableStateOf(currentTopic) }

    val candidateTopics = remember(selectedCategory, viewModel.allCuratedTopics) {
        val pool = if (selectedCategory == "SEMUA") {
            viewModel.allCuratedTopics
        } else {
            viewModel.allCuratedTopics.filter { it.category.equals(selectedCategory, ignoreCase = true) }
        }
        if (pool.isNotEmpty()) pool else viewModel.allCuratedTopics
    }

    val triggerRoll: () -> Unit = {
        if (!isRollingTopic) {
            val next = viewModel.getNextRandomTopic()
            targetRollingTopic = next
            isRollingTopic = true
            hasGeneratedTopic = true
        }
    }

    val infiniteSpinTransition = rememberInfiniteTransition(label = "wheelSpin")
    val spinAngle by infiniteSpinTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spinAngle"
    )

    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.toggleAudioRecordEnabled(isGranted)
    }

    val isFavorite = savedTopics.any { it.id == currentTopic.id && it.isFavorite }

    Box(modifier = modifier.fillMaxSize()) {
        MegaMendungBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Logo Emblem from User Photo
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(2.dp, SakaGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_launcher_user_logo),
                    contentDescription = "Logo SAKA SPEAK",
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Hero Section
            Text(
                text = "RUANG LATIHAN PUBLIC SPEAKING",
                color = SakaMatchaLight,
                fontSize = 11.sp,
                letterSpacing = 3.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Berpikir Lebih Dalam.\nBicara Lebih Bermakna.",
                fontFamily = FontFamily.Serif,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = SakaCream,
                lineHeight = 34.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Temukan topik secara acak, pahami sudut pandangnya, dan latih kemampuanmu untuk berpikir kritis sebelum berbicara.",
                color = SakaCreamMuted,
                fontSize = 13.5.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Category Filter Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                viewModel.categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectCategory(cat) },
                        label = {
                            Text(
                                text = cat,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SakaGold,
                            selectedLabelColor = SakaDark,
                            containerColor = SakaDarkCard,
                            labelColor = SakaCreamDim
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) SakaGold else SakaBorderGold.copy(alpha = 0.3f),
                            selectedBorderColor = SakaGold
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("filter_chip_$cat")
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Silinder Roda Acak Topik Mendatar Menggulung ke Bawah (Wheel)
            RollingTopicWheel(
                isRolling = isRollingTopic,
                candidateTopics = candidateTopics,
                targetTopic = targetRollingTopic,
                onRollFinished = {
                    isRollingTopic = false
                    viewModel.selectSpecificTopic(targetRollingTopic)
                },
                onTriggerRoll = triggerRoll,
                modifier = Modifier.padding(bottom = 18.dp)
            )

            // The Main Topic Card (matching HTML design with ultra-smooth AnimatedContent)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SakaBorderGold, RoundedCornerShape(24.dp))
                    .testTag("topic_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    SakaBrown.copy(alpha = 0.85f),
                                    SakaDarkSoft.copy(alpha = 0.98f)
                                )
                            )
                        )
                        .padding(24.dp)
                ) {
                    AnimatedContent(
                        targetState = currentTopic,
                        transitionSpec = {
                            (slideInVertically(
                                animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow),
                                initialOffsetY = { it / 6 }
                            ) + fadeIn(animationSpec = tween(300, easing = FastOutSlowInEasing)) + scaleIn(
                                initialScale = 0.96f,
                                animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow)
                            )).togetherWith(
                                slideOutVertically(
                                    animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow),
                                    targetOffsetY = { -it / 6 }
                                ) + fadeOut(animationSpec = tween(200, easing = FastOutSlowInEasing)) + scaleOut(
                                    targetScale = 0.96f,
                                    animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow)
                                )
                            )
                        },
                        label = "topic_card_smooth_transition"
                    ) { activeTopic ->
                        Column {
                            // Category Pill and Bookmark Favorite Icon
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(SakaTagBg)
                                        .border(1.dp, SakaMatcha.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                                        .padding(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = activeTopic.category,
                                        color = SakaMatchaLight,
                                        fontSize = 11.sp,
                                        letterSpacing = 1.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.testTag("topic_category_text")
                                    )
                                }

                                val isTopicFav = savedTopics.any { it.id == activeTopic.id && it.isFavorite }
                                IconButton(
                                    onClick = { viewModel.toggleFavorite(activeTopic) },
                                    modifier = Modifier.testTag("favorite_button")
                                ) {
                                    Icon(
                                        imageVector = if (isTopicFav) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                        contentDescription = "Simpan Topik",
                                        tint = if (isTopicFav) SakaGold else SakaCreamDim
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Topic Title
                            Text(
                                text = activeTopic.title,
                                fontFamily = FontFamily.Serif,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = SakaGoldLight,
                                lineHeight = 32.sp,
                                modifier = Modifier.testTag("topic_title_text")
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Quote
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp))
                                    .background(SakaCream.copy(alpha = 0.04f))
                                    .border(
                                        width = 1.dp,
                                        color = SakaBorderSubtle,
                                        shape = RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp)
                                    )
                                    .padding(vertical = 12.dp, horizontal = 16.dp)
                            ) {
                                Row {
                                    Box(
                                        modifier = Modifier
                                            .width(3.dp)
                                            .height(48.dp)
                                            .background(SakaGold)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "“${activeTopic.quote}”",
                                        fontFamily = FontFamily.Serif,
                                        fontSize = 14.5.sp,
                                        fontStyle = FontStyle.Italic,
                                        color = SakaCream,
                                        lineHeight = 22.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Info Boxes Grid (Points to think & Perspectives)
                            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                // Info Box 1: Hal yang Perlu Dipikirkan
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = SakaDark.copy(alpha = 0.5f)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, SakaBorderSubtle)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = "HAL YANG PERLU DIPIKIRKAN",
                                            color = SakaMatchaLight,
                                            fontSize = 11.sp,
                                            letterSpacing = 1.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        activeTopic.points.forEach { pt ->
                                            Row(
                                                modifier = Modifier.padding(vertical = 3.dp),
                                                verticalAlignment = Alignment.Top
                                            ) {
                                                Text("• ", color = SakaGold, fontSize = 13.sp)
                                                Text(
                                                    text = pt,
                                                    color = SakaCream.copy(alpha = 0.85f),
                                                    fontSize = 13.sp,
                                                    lineHeight = 18.sp
                                                )
                                            }
                                        }
                                    }
                                }

                                // Info Box 2: Sudut Pandang
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = SakaDark.copy(alpha = 0.5f)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, SakaBorderSubtle)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = "SUDUT PANDANG",
                                            color = SakaMatchaLight,
                                            fontSize = 11.sp,
                                            letterSpacing = 1.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        activeTopic.perspectives.forEach { psp ->
                                            Row(
                                                modifier = Modifier.padding(vertical = 3.dp),
                                                verticalAlignment = Alignment.Top
                                            ) {
                                                Text("❖ ", color = SakaMatchaLight, fontSize = 11.sp)
                                                Text(
                                                    text = psp,
                                                    color = SakaCream.copy(alpha = 0.85f),
                                                    fontSize = 13.sp,
                                                    lineHeight = 18.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Challenge Box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(SakaGold.copy(alpha = 0.08f))
                                    .border(1.dp, SakaBorderGold.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                                    .padding(16.dp)
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "🎙️", fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Tantangan Berbicara",
                                            color = SakaGold,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = activeTopic.challenge,
                                        color = SakaCream,
                                        fontSize = 13.5.sp,
                                        lineHeight = 21.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Action Buttons: Randomize Topic & Start Practice
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // ✦ Acak Topik Baru (Putar Wheel) Button
                                Button(
                                    onClick = triggerRoll,
                                    enabled = !isRollingTopic,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("random_topic_button"),
                                    shape = RoundedCornerShape(26.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = SakaGold,
                                        contentColor = SakaDark
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(18.dp)
                                            .rotate(if (isRollingTopic) spinAngle else 0f)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isRollingTopic) "MENGGULUNG WHEEL..." else "✦ ACAK TOPIK BARU (WHEEL)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        letterSpacing = 0.5.sp
                                    )
                                }

                                // ▶ MULAI LATIHAN Button
                                Button(
                                    onClick = onStartPractice,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("start_practice_button"),
                                    shape = RoundedCornerShape(26.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = SakaMatcha,
                                        contentColor = SakaWhite
                                    )
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    val durationText = when (targetSpeechSeconds) {
                                        60 -> "1 MENIT"
                                        120 -> "2 MENIT"
                                        180 -> "3 MENIT"
                                        300 -> "5 MENIT"
                                        else -> "${targetSpeechSeconds / 60} MENIT"
                                    }
                                    Text(
                                        text = "▶ MULAI LATIHAN ($durationText)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Speech Settings Card: Duration, Prep Time & Audio Recording
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SakaDarkCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, SakaBorderSubtle)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "PENGATURAN LATIHAN",
                        color = SakaGoldLight,
                        fontSize = 11.5.sp,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Duration choices
                    Text(text = "Target Waktu Berbicara:", color = SakaCream, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(60 to "1 Menit", 120 to "2 Menit", 180 to "3 Menit", 300 to "5 Menit").forEach { (sec, label) ->
                            val isSelected = targetSpeechSeconds == sec
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) SakaGold else SakaDarkSoft)
                                    .border(
                                        1.dp,
                                        if (isSelected) SakaGold else SakaBorderSubtle,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { viewModel.setTargetSpeechDuration(sec) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) SakaDark else SakaCreamDim,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Prep time toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Waktu Persiapan Berpikir (30 Detik)",
                                color = SakaCream,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Beri jeda untuk merancang struktur PREP sebelum berbicara",
                                color = SakaCreamMuted,
                                fontSize = 11.5.sp
                            )
                        }
                        Switch(
                            checked = isPrepEnabled,
                            onCheckedChange = { viewModel.togglePrepEnabled(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = SakaGold,
                                checkedTrackColor = SakaGoldDim,
                                uncheckedThumbColor = SakaCreamDim,
                                uncheckedTrackColor = SakaDarkSoft
                            ),
                            modifier = Modifier.testTag("prep_toggle_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Audio recording toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Rekam Suara untuk Evaluasi",
                                color = SakaCream,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Dengarkan kembali artikulasi, intonasi, dan jeda bicaramu",
                                color = SakaCreamMuted,
                                fontSize = 11.5.sp
                            )
                        }
                        Switch(
                            checked = isAudioRecordEnabled,
                            onCheckedChange = { enabled ->
                                if (enabled) {
                                    val hasPermission = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.RECORD_AUDIO
                                    ) == PackageManager.PERMISSION_GRANTED

                                    if (hasPermission) {
                                        viewModel.toggleAudioRecordEnabled(true)
                                    } else {
                                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    }
                                } else {
                                    viewModel.toggleAudioRecordEnabled(false)
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = SakaMatcha,
                                checkedTrackColor = SakaMatchaDark,
                                uncheckedThumbColor = SakaCreamDim,
                                uncheckedTrackColor = SakaDarkSoft
                            ),
                            modifier = Modifier.testTag("audio_record_toggle_switch")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Custom Topic Button
            OutlinedButton(
                onClick = { showCustomTopicDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("create_custom_topic_button"),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SakaGoldLight),
                border = androidx.compose.foundation.BorderStroke(1.dp, SakaBorderGold.copy(alpha = 0.5f))
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Tulis Topik Latihan Sendiri",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Dialog for adding custom topic
    if (showCustomTopicDialog) {
        CustomTopicDialog(
            onDismiss = { showCustomTopicDialog = false },
            onSave = { category, title, quote, points, perspectives, challenge ->
                viewModel.addCustomTopic(category, title, quote, points, perspectives, challenge)
                showCustomTopicDialog = false
            }
        )
    }
}

@Composable
fun CustomTopicDialog(
    onDismiss: () -> Unit,
    onSave: (category: String, title: String, quote: String, points: List<String>, perspectives: List<String>, challenge: String) -> Unit
) {
    var category by remember { mutableStateOf("UMUM") }
    var title by remember { mutableStateOf("") }
    var quote by remember { mutableStateOf("") }
    var pointsText by remember { mutableStateOf("") }
    var perspectivesText by remember { mutableStateOf("") }
    var challenge by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, SakaBorderGold, RoundedCornerShape(20.dp)),
            color = SakaDarkSoft
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Buat Topik Latihan Sendiri",
                    fontFamily = FontFamily.Serif,
                    fontSize = 20.sp,
                    color = SakaGoldLight,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Kategori (misal: BISNIS, PENDIDIKAN)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = customTextFieldColors()
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul Topik") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = customTextFieldColors()
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = quote,
                    onValueChange = { quote = it },
                    label = { Text("Kutipan / Pemikiran Utama") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = customTextFieldColors()
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = pointsText,
                    onValueChange = { pointsText = it },
                    label = { Text("Poin Pemikiran (pisahkan dengan baris baru)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    colors = customTextFieldColors()
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = perspectivesText,
                    onValueChange = { perspectivesText = it },
                    label = { Text("Sudut Pandang (pisahkan dengan koma atau baris baru)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = customTextFieldColors()
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = challenge,
                    onValueChange = { challenge = it },
                    label = { Text("Tantangan Berbicara (Prompt)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    colors = customTextFieldColors()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(contentColor = SakaCreamDim)
                    ) {
                        Text("Batal")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                val pts = pointsText.split("\n").filter { it.isNotBlank() }
                                val pers = perspectivesText.split("\n", ",").map { it.trim() }.filter { it.isNotBlank() }
                                onSave(
                                    category.ifBlank { "UMUM" },
                                    title,
                                    quote.ifBlank { "Bicara dengan kejelasan dan keyakinan diri." },
                                    if (pts.isNotEmpty()) pts else listOf("Fokus pada argumen yang runut."),
                                    if (pers.isNotEmpty()) pers else listOf("Sudut pandang umum"),
                                    challenge.ifBlank { "Sampaikan pandanganmu tentang $title secara meyakinkan!" }
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SakaGold,
                            contentColor = SakaDark
                        ),
                        enabled = title.isNotBlank()
                    ) {
                        Text("Simpan Topik", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun customTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = SakaGold,
    unfocusedBorderColor = SakaBorderGold.copy(alpha = 0.4f),
    focusedContainerColor = SakaDarkCard,
    unfocusedContainerColor = SakaDarkCard,
    focusedTextColor = SakaCream,
    unfocusedTextColor = SakaCream,
    focusedLabelColor = SakaGoldLight,
    unfocusedLabelColor = SakaCreamDim
)
