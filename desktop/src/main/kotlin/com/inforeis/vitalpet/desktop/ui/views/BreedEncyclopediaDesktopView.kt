package com.inforeis.vitalpet.desktop.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
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
import com.inforeis.vitalpet.desktop.ui.theme.AmberAccent
import com.inforeis.vitalpet.desktop.ui.theme.TealPrimary
import com.inforeis.vitalpet.models.PetBreed
import com.inforeis.vitalpet.models.Species
import com.inforeis.vitalpet.services.BreedDatabase
import com.inforeis.vitalpet.services.LanguageManager.localized

@Composable
fun BreedEncyclopediaDesktopView() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedSpecies by remember { mutableStateOf<Species?>(null) }
    var selectedBreed by remember { mutableStateOf<PetBreed?>(null) }

    val allBreeds = remember { BreedDatabase.allBreeds }

    val filteredBreeds = allBreeds.filter { breed ->
        val matchesQuery = searchQuery.isBlank() ||
                breed.name.contains(searchQuery, ignoreCase = true) ||
                breed.description.contains(searchQuery, ignoreCase = true) ||
                breed.healthRisks.any { it.contains(searchQuery, ignoreCase = true) }

        val matchesSpecies = selectedSpecies == null || breed.species == selectedSpecies

        matchesQuery && matchesSpecies
    }

    Row(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        // Coluna Esquerda: Pesquisa e Lista
        Column(modifier = Modifier.weight(1.2f)) {
            Text(
                text = "Enciclopédia de Raças".localized(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Perfis morfológicos, portes, pesos médios e predisposições clínicas veterinárias.".localized(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Pesquisar raça ou predisposição clínica...".localized()) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (selectedSpecies == null) TealPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.clickable { selectedSpecies = null }
                ) {
                    Text(
                        "Todas as Raças".localized(),
                        color = if (selectedSpecies == null) Color.White else MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (selectedSpecies == null) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (selectedSpecies == Species.DOG) TealPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.clickable { selectedSpecies = Species.DOG }
                ) {
                    Text(
                        "🐶 Cães".localized(),
                        color = if (selectedSpecies == Species.DOG) Color.White else MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (selectedSpecies == Species.DOG) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (selectedSpecies == Species.CAT) TealPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.clickable { selectedSpecies = Species.CAT }
                ) {
                    Text(
                        "🐱 Gatos".localized(),
                        color = if (selectedSpecies == Species.CAT) Color.White else MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (selectedSpecies == Species.CAT) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredBreeds) { breed ->
                    val isSelected = selectedBreed?.id == breed.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedBreed = breed },
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
                                    .background(if (breed.species == Species.DOG) Color(0xFF3B82F6).copy(alpha = 0.2f) else Color(0xFFEC4899).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(if (breed.species == Species.DOG) "🐶" else "🐱", fontSize = 20.sp)
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(breed.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                Text("${breed.species.displayName} • ${breed.minWeightKg} - ${breed.maxWeightKg} kg", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = TealPrimary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    breed.sizeCategory.displayName,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TealPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(24.dp))

        // Coluna Direita: Ficha da Raça
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            val breed = selectedBreed ?: filteredBreeds.firstOrNull()
            if (breed != null) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(breed.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Text("${breed.species.displayName} • ${breed.sizeCategory.displayName}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = TealPrimary
                        ) {
                            Text(
                                "${breed.minWeightKg} - ${breed.maxWeightKg} kg",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Descrição e Comportamento:".localized(), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(breed.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(20.dp))

                    // Riscos e Predisposições Clínicas
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AmberAccent.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AmberAccent.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Predisposições Clínicas Conhecidas:".localized(), fontWeight = FontWeight.Bold, color = AmberAccent)
                            Spacer(modifier = Modifier.height(8.dp))
                            breed.healthRisks.forEach { risk ->
                                Text("⚠️ $risk", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Selecione uma raça para visualizar a ficha morfológica.".localized())
                }
            }
        }
    }
}
