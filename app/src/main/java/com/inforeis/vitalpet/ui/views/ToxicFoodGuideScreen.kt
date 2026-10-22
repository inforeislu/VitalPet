package com.inforeis.vitalpet.ui.views

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inforeis.vitalpet.models.Species
import com.inforeis.vitalpet.models.ToxicCategory
import com.inforeis.vitalpet.models.ToxicFood
import com.inforeis.vitalpet.models.ToxicityLevel
import com.inforeis.vitalpet.services.LanguageManager.localized
import com.inforeis.vitalpet.services.ToxicFoodDatabase
import kotlinx.coroutines.launch

@Composable
fun getToxicityColor(level: ToxicityLevel): Color {
    return when (level) {
        ToxicityLevel.EXTREME -> Color(0xFFC2185B) // Magenta / Pink / Purple
        ToxicityLevel.HIGH -> Color(0xFFE53935)    // Vibrant Red
        ToxicityLevel.MODERATE -> Color(0xFFFB8C00)// Orange
        ToxicityLevel.LOW -> Color(0xFFFBC02D)     // Amber / Yellow
    }
}

@Composable
fun getToxicityContainerColor(level: ToxicityLevel): Color {
    val isDark = MaterialTheme.colorScheme.surface.hashCode() < 0 // generic check or alpha
    return getToxicityColor(level).copy(alpha = 0.14f)
}

fun getCategoryIcon(category: ToxicCategory): ImageVector {
    return when (category) {
        ToxicCategory.FOODS -> Icons.Default.Restaurant
        ToxicCategory.PLANTS -> Icons.Default.LocalFlorist
        ToxicCategory.MEDICATIONS -> Icons.Default.Medication
        ToxicCategory.CHEMICALS -> Icons.Default.Science
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToxicFoodGuideScreen(onBack: (() -> Unit)? = null) {
    Scaffold(
        topBar = {
            if (onBack != null) {
                TopAppBar(
                    title = {
                        Text(
                            text = "Toxidez & Envenenamento".localized(),
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Voltar".localized()
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            ToxicFoodGuideContent(
                showHeader = onBack == null,
                sosPhone = "+352 112"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToxicFoodGuideContent(
    modifier: Modifier = Modifier,
    showHeader: Boolean = true,
    sosPhone: String = "+352 112",
    searchQuery: String = "",
    onSearchQueryChange: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    var internalSearchQuery by remember { mutableStateOf("") }
    val effectiveQuery = onSearchQueryChange?.let { searchQuery } ?: internalSearchQuery
    val setQuery: (String) -> Unit = onSearchQueryChange ?: { internalSearchQuery = it }

    var selectedSpeciesFilter by remember { mutableStateOf<Species?>(null) }
    var selectedCategory by remember { mutableStateOf<ToxicCategory?>(null) }
    var selectedFood by remember { mutableStateOf<ToxicFood?>(null) }

    val filteredFoods = remember(effectiveQuery, selectedSpeciesFilter, selectedCategory) {
        val q = effectiveQuery.trim().lowercase()
        ToxicFoodDatabase.toxicItems.filter { food ->
            // Species filter
            val matchesSpecies = selectedSpeciesFilter == null ||
                    food.affectedSpecies == null ||
                    food.affectedSpecies == selectedSpeciesFilter

            // Category filter
            val matchesCategory = selectedCategory == null || food.category == selectedCategory

            // Search filter
            val matchesSearch = q.isEmpty() ||
                    food.name.lowercase().contains(q) ||
                    food.name.localized().lowercase().contains(q) ||
                    food.toxicAgent.lowercase().contains(q) ||
                    food.toxicAgent.localized().lowercase().contains(q) ||
                    food.symptoms.lowercase().contains(q) ||
                    food.symptoms.localized().lowercase().contains(q)

            matchesSpecies && matchesCategory && matchesSearch
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Optional Top Header with SOS Pill button (iOS style)
        if (showHeader) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Toxidez & Envenenamento".localized(),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(10.dp))

                // SOS Call pill
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFE53935),
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${sosPhone.replace(" ", "")}"))
                        context.startActivity(intent)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = sosPhone,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Search Bar
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                OutlinedTextField(
                    value = effectiveQuery,
                    onValueChange = setQuery,
                    placeholder = {
                        Text(
                            text = "Pesquisar alimento, planta ou medicamento...".localized(),
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    },
                    trailingIcon = {
                        if (effectiveQuery.isNotEmpty()) {
                            IconButton(onClick = { setQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Limpar",
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF007AFF),
                        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Species Segmented Control (Todos os Animais / Cães / Gatos)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(3.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                val speciesOptions = listOf(
                    null to "Todos os Animais".localized(),
                    Species.DOG to "Cães".localized(),
                    Species.CAT to "Gatos".localized()
                )

                speciesOptions.forEach { (species, label) ->
                    val isSelected = selectedSpeciesFilter == species
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(9.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.surface
                                else Color.Transparent
                            )
                            .clickable { selectedSpeciesFilter = species }
                            .padding(vertical = 7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.onSurface
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // Horizontal Category Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // "Todas as Categorias" Chip
            val isAllSelected = selectedCategory == null
            Surface(
                shape = CircleShape,
                color = if (isAllSelected) Color(0xFF007AFF) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.clickable { selectedCategory = null }
            ) {
                Text(
                    text = "Todas as Categorias".localized(),
                    color = if (isAllSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }

            // Categories
            ToxicCategory.entries.forEach { cat ->
                val isSelected = selectedCategory == cat
                Surface(
                    shape = CircleShape,
                    color = if (isSelected) Color(0xFF007AFF) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.clickable { selectedCategory = cat }
                ) {
                    Text(
                        text = cat.displayName,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // Section Counter Label (e.g. "36 substâncias identificadas")
        Text(
            text = "%d substâncias identificadas".localized().format(filteredFoods.size),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )

        // List of Toxic Substances
        if (filteredFoods.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "Nenhuma substância tóxica encontrada para os critérios selecionados.".localized(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredFoods, key = { it.name }) { food ->
                    val levelColor = getToxicityColor(food.level)
                    val levelContainer = getToxicityContainerColor(food.level)
                    val categoryIcon = getCategoryIcon(food.category)

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                selectedFood = food
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Category & Severity Squircle Icon
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(
                                        color = levelContainer,
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = categoryIcon,
                                    contentDescription = null,
                                    tint = levelColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Content Details
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = food.name.localized(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f, fill = false)
                                    )

                                    Spacer(modifier = Modifier.width(6.dp))

                                    // Severity Badge
                                    Surface(
                                        shape = CircleShape,
                                        color = levelContainer
                                    ) {
                                        Text(
                                            text = food.level.displayName,
                                            color = levelColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp,
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                            maxLines = 1
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                // Subtitle Row: Affected Species chip + Chemical agent
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    val (speciesLabel, speciesColor, speciesBg) = when (food.affectedSpecies) {
                                        Species.DOG -> Triple(
                                            "🐾 " + "Apenas Cães".localized(),
                                            Color(0xFF007AFF),
                                            Color(0xFF007AFF).copy(alpha = 0.12f)
                                        )
                                        Species.CAT -> Triple(
                                            "🐾 " + "Apenas Gatos".localized(),
                                            Color(0xFF9C27B0),
                                            Color(0xFF9C27B0).copy(alpha = 0.12f)
                                        )
                                        null -> Triple(
                                            "🐾 " + "Cães & Gatos".localized(),
                                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                            MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    }

                                    Surface(
                                        shape = CircleShape,
                                        color = speciesBg
                                    ) {
                                        Text(
                                            text = speciesLabel,
                                            color = speciesColor,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Text(
                                        text = "•",
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
                                        fontSize = 11.sp
                                    )

                                    Text(
                                        text = food.toxicAgent.localized(),
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f, fill = false)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Detail Bottom Sheet
    selectedFood?.let { food ->
        ToxicFoodDetailSheet(
            food = food,
            onDismiss = { selectedFood = null }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToxicFoodDetailSheet(
    food: ToxicFood,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    val levelColor = getToxicityColor(food.level)
    val levelContainer = getToxicityContainerColor(food.level)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header: Severity Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = levelContainer, shape = RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = levelColor,
                    modifier = Modifier.size(36.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        text = food.name.localized(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = food.level.displayName,
                            color = levelColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "•",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            fontSize = 12.sp
                        )
                        Text(
                            text = food.category.displayName,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Biological Vulnerability Alert
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (food.affectedSpecies == Species.CAT) Color(0xFF9C27B0) else Color(0xFF007AFF),
                    modifier = Modifier.size(24.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Vulnerabilidade Biológica:".localized(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    val vulnerabilityText = when (food.affectedSpecies) {
                        Species.DOG -> "Altamente perigoso para Cães.".localized()
                        Species.CAT -> "Extremamente perigoso e letal para Gatos.".localized()
                        null -> "Perigoso para Cães e Gatos.".localized()
                    }
                    Text(
                        text = vulnerabilityText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (food.affectedSpecies == Species.CAT) Color(0xFF9C27B0)
                        else if (food.affectedSpecies == Species.DOG) Color(0xFF007AFF)
                        else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Chemical Agent
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Agente Químico / Princípio Tóxico:".localized(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = food.toxicAgent.localized(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

            // Clinical Symptoms
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = null,
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Sintomas Clínicos de Intoxicação".localized(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFFE53935)
                    )
                }
                Text(
                    text = food.symptoms.localized(),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                    lineHeight = 18.sp
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

            // First Aid & Action (Green container)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFF4CAF50).copy(alpha = 0.12f),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Primeiros Socorros & Ação Recomendada".localized(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF2E7D32)
                    )
                }
                Text(
                    text = food.firstAidAdvice.localized(),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 18.sp
                )
            }

            // Legal Warning
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MedicalServices,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Em caso de suspeita de ingestão, nunca aguarde pelo aparecimento de sintomas graves. Contacte um hospital veterinário 24h de imediato.".localized(),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                    lineHeight = 15.sp
                )
            }

            // Close Button
            Button(
                onClick = {
                    coroutineScope.launch {
                        sheetState.hide()
                        onDismiss()
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Fechar".localized(),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
