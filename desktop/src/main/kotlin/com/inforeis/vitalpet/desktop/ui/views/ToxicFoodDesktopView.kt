package com.inforeis.vitalpet.desktop.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inforeis.vitalpet.desktop.ui.theme.TealPrimary
import com.inforeis.vitalpet.models.ToxicCategory
import com.inforeis.vitalpet.models.ToxicFood
import com.inforeis.vitalpet.models.ToxicityLevel
import com.inforeis.vitalpet.services.LanguageManager.localized
import com.inforeis.vitalpet.services.ToxicFoodDatabase

@Composable
fun ToxicFoodDesktopView() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ToxicCategory?>(null) }
    var selectedItem by remember { mutableStateOf<ToxicFood?>(null) }

    val allItems: List<ToxicFood> = remember { ToxicFoodDatabase.toxicItems }

    val filteredItems = allItems.filter { item ->
        val matchesQuery = searchQuery.isBlank() ||
                item.name.contains(searchQuery, ignoreCase = true) ||
                item.toxicAgent.contains(searchQuery, ignoreCase = true) ||
                item.symptoms.contains(searchQuery, ignoreCase = true)

        val matchesCategory = selectedCategory == null || item.category == selectedCategory

        matchesQuery && matchesCategory
    }

    Row(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        // Coluna Esquerda: Pesquisa e Lista
        Column(modifier = Modifier.weight(1.2f)) {
            Text(
                text = "Toxidez & Envenenamento".localized(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Guia completo de alimentos, plantas, fármacos e químicos nocivos para cães e gatos.".localized(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Barra de Pesquisa
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Pesquisar substância tóxica ou sintoma...".localized()) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Filtros de Categoria
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CategoryFilterChip("Todos".localized(), selectedCategory == null) { selectedCategory = null }
                CategoryFilterChip("Alimentos".localized(), selectedCategory == ToxicCategory.FOODS) { selectedCategory = ToxicCategory.FOODS }
                CategoryFilterChip("Plantas".localized(), selectedCategory == ToxicCategory.PLANTS) { selectedCategory = ToxicCategory.PLANTS }
                CategoryFilterChip("Fármacos".localized(), selectedCategory == ToxicCategory.MEDICATIONS) { selectedCategory = ToxicCategory.MEDICATIONS }
                CategoryFilterChip("Químicos".localized(), selectedCategory == ToxicCategory.CHEMICALS) { selectedCategory = ToxicCategory.CHEMICALS }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lista
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredItems) { item ->
                    val isSelected = selectedItem?.name == item.name
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedItem = item },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) TealPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                        ),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, TealPrimary) else null
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(getCategoryColor(item.category).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (item.category) {
                                        ToxicCategory.FOODS -> Icons.Default.Restaurant
                                        ToxicCategory.PLANTS -> Icons.Default.LocalFlorist
                                        ToxicCategory.MEDICATIONS -> Icons.Default.Medication
                                        ToxicCategory.CHEMICALS -> Icons.Default.Science
                                    },
                                    contentDescription = null,
                                    tint = getCategoryColor(item.category)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                Text("Princípio: ${item.toxicAgent}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            ToxicityBadge(item.level)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(24.dp))

        // Coluna Direita: Ficha de Toxicidade
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            val item = selectedItem ?: filteredItems.firstOrNull()
            if (item != null) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(item.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        ToxicityBadge(item.level)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Substância Tóxica:".localized(), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                            Text(item.toxicAgent, style = MaterialTheme.typography.bodyMedium)
                            if (item.affectedSpecies != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Espécie Afetada:".localized(), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                                Text(item.affectedSpecies.displayName, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Sintomas Típicos:".localized(), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(item.symptoms, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(20.dp))

                    // Ação de Emergência
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFEF4444).copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Ação de Emergência:".localized(), fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(item.firstAidAdvice, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Selecione um item para ver detalhes de toxicidade.".localized())
                }
            }
        }
    }
}

@Composable
private fun CategoryFilterChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) TealPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun ToxicityBadge(severity: ToxicityLevel) {
    val (bgColor, textColor, label) = when (severity) {
        ToxicityLevel.EXTREME -> Triple(Color(0xFFEF4444), Color.White, "EXTREMA / LETAL")
        ToxicityLevel.HIGH -> Triple(Color(0xFFF97316), Color.White, "ALTO RISCO")
        ToxicityLevel.MODERATE -> Triple(Color(0xFFF59E0B), Color.Black, "MODERADO")
        ToxicityLevel.LOW -> Triple(Color(0xFF10B981), Color.White, "BAIXO RISCO")
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bgColor
    ) {
        Text(
            text = label,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

private fun getCategoryColor(category: ToxicCategory): Color = when (category) {
    ToxicCategory.FOODS -> Color(0xFFF59E0B)
    ToxicCategory.PLANTS -> Color(0xFF10B981)
    ToxicCategory.MEDICATIONS -> Color(0xFF8B5CF6)
    ToxicCategory.CHEMICALS -> Color(0xFFEC4899)
}
