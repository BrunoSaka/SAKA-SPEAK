package com.example.ui.screens

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
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Topic
import com.example.data.repository.CatalogBank100k
import com.example.data.repository.CatalogItem
import com.example.data.repository.CatalogType
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
import com.example.ui.theme.SakaGoldLight
import com.example.ui.theme.SakaMatcha
import com.example.ui.theme.SakaMatchaDark
import com.example.ui.theme.SakaMatchaLight
import com.example.ui.theme.SakaTagBg
import com.example.ui.theme.SakaWhite
import com.example.ui.viewmodel.SakaViewModel

@Composable
fun CatalogScreen(
    viewModel: SakaViewModel,
    onTopicSelected: (Topic) -> Unit,
    modifier: Modifier = Modifier
) {
    val savedTopics by viewModel.savedTopics.collectAsState()
    val allCurated = viewModel.allCuratedTopics

    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0: 100k Bank, 1: Kurasi Utama, 2: Favorit
    var catalogTypeFilter by remember { mutableStateOf(CatalogType.ALL) } // ALL, KATA, KALIMAT
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("SEMUA") }
    var currentPage by remember { mutableIntStateOf(0) }

    // Load 100k catalog items for current page and filters
    val catalog100kItems = remember(currentPage, catalogTypeFilter, selectedCategoryFilter, searchQuery) {
        viewModel.get100kCatalogPage(
            page = currentPage,
            pageSize = 40,
            type = catalogTypeFilter,
            category = selectedCategoryFilter,
            query = searchQuery
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        MegaMendungBackground(alpha = 0.04f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Header
            Text(
                text = "KATALOG & BANK RETORIKA",
                color = SakaMatchaLight,
                fontSize = 11.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "100.000+ Bank Kata & Kalimat",
                fontFamily = FontFamily.Serif,
                fontSize = 22.sp,
                color = SakaGoldLight,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Main Tab Row: 100k Bank vs Kurasi vs Favorit
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = SakaDarkSoft,
                contentColor = SakaGold,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = SakaGold,
                        height = 3.dp
                    )
                }
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = {
                        Text(
                            text = "100.000+ Bank",
                            fontSize = 12.sp,
                            fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == 0) SakaGold else SakaCreamDim
                        )
                    }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = {
                        Text(
                            text = "Kurasi Utama (${allCurated.size})",
                            fontSize = 12.sp,
                            fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == 1) SakaGold else SakaCreamDim
                        )
                    }
                )
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    text = {
                        Text(
                            text = "Tersimpan (${savedTopics.count { it.isFavorite || it.isCustom }})",
                            fontSize = 12.sp,
                            fontWeight = if (selectedTabIndex == 2) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == 2) SakaGold else SakaCreamDim
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sub-filter for 100.000 Bank: Semua vs Kata Saja vs Kalimat
            if (selectedTabIndex == 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        CatalogType.ALL to "Semua (100k)",
                        CatalogType.KALIMAT to "Kalimat (70k)",
                        CatalogType.KATA to "Kata Saja (30k)"
                    ).forEach { (type, label) ->
                        val isSelected = catalogTypeFilter == type
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) SakaGold else SakaDarkCard)
                                .border(
                                    1.dp,
                                    if (isSelected) SakaGold else SakaBorderSubtle,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    catalogTypeFilter = type
                                    currentPage = 0
                                }
                                .padding(vertical = 7.dp),
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
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    currentPage = 0
                },
                placeholder = {
                    Text(
                        if (selectedTabIndex == 0) "Cari dari 100.000 kata atau kalimat..." else "Cari topik, isu, kata kunci...",
                        color = SakaCreamDim.copy(alpha = 0.6f),
                        fontSize = 12.5.sp
                    )
                },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SakaGold) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("catalog_search_bar"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SakaGold,
                    unfocusedBorderColor = SakaBorderGold.copy(alpha = 0.3f),
                    focusedContainerColor = SakaDarkCard,
                    unfocusedContainerColor = SakaDarkCard,
                    focusedTextColor = SakaCream,
                    unfocusedTextColor = SakaCream
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Category Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val availableCategories = listOf("SEMUA") + CatalogBank100k.categories
                availableCategories.forEach { cat ->
                    val isSelected = selectedCategoryFilter == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedCategoryFilter = cat
                            currentPage = 0
                        },
                        label = { Text(cat, fontSize = 10.5.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SakaGold,
                            selectedLabelColor = SakaDark,
                            containerColor = SakaDarkCard,
                            labelColor = SakaCreamDim
                        ),
                        shape = RoundedCornerShape(16.dp),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) SakaGold else SakaBorderGold.copy(alpha = 0.2f),
                            selectedBorderColor = SakaGold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Content Display Based on Active Tab with smooth AnimatedContent
            AnimatedContent(
                targetState = selectedTabIndex,
                transitionSpec = {
                    val direction = if (targetState > initialState) 1 else -1
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
                label = "catalog_tab_content_transition"
            ) { activeTabIndex ->
                when (activeTabIndex) {
                    0 -> {
                        // TAB 1: 100.000+ CATALOG BANK (Kata & Kalimat)
                        Column(modifier = Modifier.fillMaxSize()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Menampilkan Halaman ${currentPage + 1} (${catalog100kItems.size} Item)",
                                    color = SakaCreamMuted,
                                    fontSize = 11.5.sp
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    if (currentPage > 0) {
                                        OutlinedButton(
                                            onClick = { currentPage -= 1 },
                                            shape = RoundedCornerShape(14.dp),
                                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, SakaBorderGold)
                                        ) {
                                            Text("« Sebelumnya", fontSize = 11.sp, color = SakaGold)
                                        }
                                    }
                                    OutlinedButton(
                                        onClick = { currentPage += 1 },
                                        shape = RoundedCornerShape(14.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, SakaBorderGold)
                                    ) {
                                        Text("Berikutnya »", fontSize = 11.sp, color = SakaGold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag("catalog_100k_list"),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(catalog100kItems, key = { it.id }) { item ->
                                    val topic = item.toTopic()
                                    val isFav = savedTopics.any { it.id == topic.id && it.isFavorite }
                                    Catalog100kItemCard(
                                        item = item,
                                        isFavorite = isFav,
                                        onToggleFavorite = { viewModel.toggleFavorite(topic) },
                                        onSelectTopic = {
                                            viewModel.selectSpecificTopic(topic)
                                            onTopicSelected(topic)
                                        }
                                    )
                                }
                            }
                        }
                    }
                    1 -> {
                        // TAB 2: TOPIK UTAMA KURASI
                        val filteredCurated = allCurated.filter { topic ->
                            val matchesCat = selectedCategoryFilter == "SEMUA" || topic.category.equals(selectedCategoryFilter, ignoreCase = true)
                            val matchesQuery = searchQuery.isBlank() || topic.title.contains(searchQuery, ignoreCase = true) || topic.quote.contains(searchQuery, ignoreCase = true)
                            matchesCat && matchesQuery
                        }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("catalog_curated_list"),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredCurated, key = { it.id }) { topic ->
                                val isFav = savedTopics.any { it.id == topic.id && it.isFavorite }
                                CatalogTopicItem(
                                    topic = topic,
                                    isFavorite = isFav,
                                    onToggleFavorite = { viewModel.toggleFavorite(topic) },
                                    onSelectTopic = {
                                        viewModel.selectSpecificTopic(topic)
                                        onTopicSelected(topic)
                                    }
                                )
                            }
                        }
                    }
                    2 -> {
                        // TAB 3: FAVORIT TERSIMPAN
                        val savedFiltered = savedTopics.filter { topic ->
                            (topic.isFavorite || topic.isCustom) &&
                            (selectedCategoryFilter == "SEMUA" || topic.category.equals(selectedCategoryFilter, ignoreCase = true)) &&
                            (searchQuery.isBlank() || topic.title.contains(searchQuery, ignoreCase = true))
                        }

                        if (savedFiltered.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 40.dp),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                Text(
                                    text = "Belum ada kata, kalimat, atau topik yang Anda simpan.",
                                    color = SakaCreamDim,
                                    fontSize = 13.5.sp
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag("catalog_saved_list"),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(savedFiltered, key = { it.id }) { topic ->
                                    CatalogTopicItem(
                                        topic = topic,
                                        isFavorite = true,
                                        onToggleFavorite = { viewModel.toggleFavorite(topic) },
                                        onSelectTopic = {
                                            viewModel.selectSpecificTopic(topic)
                                            onTopicSelected(topic)
                                        },
                                        onDelete = if (topic.isCustom) {
                                            { viewModel.deleteSavedTopic(topic.id) }
                                        } else null
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Catalog100kItemCard(
    item: CatalogItem,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onSelectTopic: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SakaBorderGold.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SakaDarkCard)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Index Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SakaGold.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "#${item.index}",
                            color = SakaGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Type Badge: KATA or KALIMAT
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (item.type == CatalogType.KATA) SakaMatchaDark.copy(alpha = 0.4f) else SakaBrown.copy(alpha = 0.5f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (item.type == CatalogType.KATA) "KATA TUNGGAL" else "KALIMAT REFLEKSI",
                            color = if (item.type == CatalogType.KATA) SakaMatchaLight else SakaGoldLight,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = item.category,
                        color = SakaCreamMuted,
                        fontSize = 10.sp
                    )
                }

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Simpan",
                        tint = if (isFavorite) SakaGold else SakaCreamDim,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Text (Word or Sentence)
            Text(
                text = item.text,
                fontFamily = FontFamily.Serif,
                fontSize = if (item.type == CatalogType.KATA) 19.sp else 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (item.type == CatalogType.KATA) SakaGoldLight else SakaCream,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Fokus: ${item.phonemeFocus}",
                color = SakaMatchaLight,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Practice Button
            Button(
                onClick = onSelectTopic,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp),
                shape = RoundedCornerShape(19.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SakaMatcha,
                    contentColor = SakaWhite
                )
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (item.type == CatalogType.KATA) "LATIH ARTIKULASI KATA INI" else "LATIH BICARA KALIMAT INI",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
fun CatalogTopicItem(
    topic: Topic,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onSelectTopic: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SakaBorderGold.copy(alpha = 0.35f), RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SakaDarkCard)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(SakaTagBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = topic.category,
                        color = SakaMatchaLight,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Row {
                    if (onDelete != null) {
                        IconButton(onClick = onDelete) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Hapus Topik Kustom",
                                tint = SakaCreamDim.copy(alpha = 0.6f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Simpan",
                            tint = if (isFavorite) SakaGold else SakaCreamDim,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = topic.title,
                fontFamily = FontFamily.Serif,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SakaGoldLight,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "“${topic.quote}”",
                fontFamily = FontFamily.Serif,
                fontSize = 13.sp,
                fontStyle = FontStyle.Italic,
                color = SakaCreamMuted,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onSelectTopic,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                shape = RoundedCornerShape(21.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SakaMatcha,
                    contentColor = SakaWhite
                )
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "LATIH TOPIK INI",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

