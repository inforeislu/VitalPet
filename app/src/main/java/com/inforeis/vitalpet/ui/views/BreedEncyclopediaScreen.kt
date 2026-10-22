package com.inforeis.vitalpet.ui.views

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inforeis.vitalpet.models.BreedEnergy
import com.inforeis.vitalpet.models.DogSizeCategory
import com.inforeis.vitalpet.models.GroomingNeed
import com.inforeis.vitalpet.models.PetBreed
import com.inforeis.vitalpet.models.Species
import com.inforeis.vitalpet.services.BreedDatabase
import com.inforeis.vitalpet.services.LanguageManager.localized
import com.inforeis.vitalpet.ui.theme.AccentAmber
import com.inforeis.vitalpet.ui.theme.AccentBlue
import com.inforeis.vitalpet.ui.theme.TealPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreedEncyclopediaScreen(
    onBack: () -> Unit,
    onOpenQuiz: () -> Unit,
    onOpenComparison: (String?) -> Unit,
    onSelectBreedDetail: (PetBreed) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedSpecies by remember { mutableStateOf<Species?>(null) }
    var selectedSize by remember { mutableStateOf<DogSizeCategory?>(null) }
    var selectedEnergy by remember { mutableStateOf<BreedEnergy?>(null) }

    val allBreeds = remember { BreedDatabase.allBreeds }

    val filteredBreeds = remember(searchQuery, selectedSpecies, selectedSize, selectedEnergy) {
        allBreeds.filter { breed ->
            if (selectedSpecies != null && breed.species != selectedSpecies) return@filter false
            if (selectedSize != null && breed.sizeCategory != selectedSize) return@filter false
            if (selectedEnergy != null && breed.energyLevel != selectedEnergy) return@filter false

            val query = searchQuery.trim().lowercase()
            if (query.isNotEmpty()) {
                val matchesName = breed.name.lowercase().contains(query) || breed.name.localized().lowercase().contains(query)
                val matchesDesc = breed.description.lowercase().contains(query) || breed.description.localized().lowercase().contains(query)
                val matchesSensitivity = breed.commonSensitivities.any { it.lowercase().contains(query) || it.localized().lowercase().contains(query) }
                if (!matchesName && !matchesDesc && !matchesSensitivity) return@filter false
            }
            true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Enciclopédia de Raças".localized(), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar".localized())
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Quick Tools: Quiz & Comparison Action Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = TealPrimary.copy(alpha = 0.12f)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onOpenQuiz() }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = TealPrimary,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Psychology, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Quiz de Raça".localized(), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TealPrimary)
                                Text("Descobrir compatibilidade".localized(), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                            }
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = AccentBlue.copy(alpha = 0.12f)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onOpenComparison(null) }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = AccentBlue,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Compare, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Comparador".localized(), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AccentBlue)
                                Text("Comparar 2 raças".localized(), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                            }
                        }
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Pesquisar por raça, porte ou característica...".localized()) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Limpar".localized())
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Species & Size Filter Chips
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Species
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            FilterChip(
                                selected = selectedSpecies == null,
                                onClick = { selectedSpecies = null },
                                label = { Text("Todas as Espécies".localized(), fontSize = 12.sp) }
                            )
                        }
                        item {
                            FilterChip(
                                selected = selectedSpecies == Species.DOG,
                                onClick = { selectedSpecies = if (selectedSpecies == Species.DOG) null else Species.DOG },
                                label = { Text("Cães".localized(), fontSize = 12.sp) }
                            )
                        }
                        item {
                            FilterChip(
                                selected = selectedSpecies == Species.CAT,
                                onClick = { selectedSpecies = if (selectedSpecies == Species.CAT) null else Species.CAT },
                                label = { Text("Gatos".localized(), fontSize = 12.sp) }
                            )
                        }
                    }

                    // Size Filter
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            FilterChip(
                                selected = selectedSize == null,
                                onClick = { selectedSize = null },
                                label = { Text("Todos os Portes".localized(), fontSize = 12.sp) }
                            )
                        }
                        items(DogSizeCategory.values()) { size ->
                            FilterChip(
                                selected = selectedSize == size,
                                onClick = { selectedSize = if (selectedSize == size) null else size },
                                label = { Text(size.displayName, fontSize = 12.sp) }
                            )
                        }
                    }
                }
            }

            // Count Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${filteredBreeds.size} " + "raças encontradas".localized(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            // Breed Cards
            items(filteredBreeds) { breed ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectBreedDetail(breed) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (breed.species == Species.DOG) TealPrimary.copy(alpha = 0.15f) else AccentAmber.copy(alpha = 0.15f),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Pets,
                                    contentDescription = null,
                                    tint = if (breed.species == Species.DOG) TealPrimary else AccentAmber,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = breed.name.localized(),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = breed.sizeCategory.displayName,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Text("•", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                                Text(
                                    text = breed.averageWeightString,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Text("•", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                                Text(
                                    text = "${breed.expectedLifeYearsMin}-${breed.expectedLifeYearsMax} " + "anos".localized(),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            if (breed.commonSensitivities.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Atenção: ".localized() + breed.commonSensitivities.take(2).joinToString(", ") { it.localized() },
                                    fontSize = 10.sp,
                                    color = AccentAmber,
                                    maxLines = 1
                                )
                            }
                        }

                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
