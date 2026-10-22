package com.inforeis.vitalpet.ui.views

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inforeis.vitalpet.models.PetBreed
import com.inforeis.vitalpet.models.Species
import com.inforeis.vitalpet.services.BreedDatabase
import com.inforeis.vitalpet.services.LanguageManager.localized
import com.inforeis.vitalpet.ui.theme.AccentAmber
import com.inforeis.vitalpet.ui.theme.AccentBlue
import com.inforeis.vitalpet.ui.theme.TealPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreedComparisonScreen(
    initialBreedId: String? = null,
    onBack: () -> Unit
) {
    val allBreeds = remember { BreedDatabase.allBreeds }
    var breed1 by remember {
        mutableStateOf(allBreeds.find { it.id == initialBreedId } ?: allBreeds.firstOrNull() ?: BreedDatabase.allBreeds[0])
    }
    var breed2 by remember {
        mutableStateOf(allBreeds.find { it.id != breed1.id && it.species == breed1.species } ?: allBreeds.lastOrNull() ?: breed1)
    }

    var showPickerForSlot by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comparador de Raças".localized(), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar".localized())
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Breed Selectors Side by Side
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BreedPickerCard(
                    modifier = Modifier.weight(1f),
                    label = "Raça A".localized(),
                    breed = breed1,
                    color = TealPrimary,
                    onClick = { showPickerForSlot = 1 }
                )
                BreedPickerCard(
                    modifier = Modifier.weight(1f),
                    label = "Raça B".localized(),
                    breed = breed2,
                    color = AccentBlue,
                    onClick = { showPickerForSlot = 2 }
                )
            }

            // Comparison Table Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Comparação de Parâmetros".localized(),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    ComparisonRow(
                        title = "Porte".localized(),
                        val1 = breed1.sizeCategory.displayName,
                        val2 = breed2.sizeCategory.displayName
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 10.dp))

                    ComparisonRow(
                        title = "Peso Médio".localized(),
                        val1 = breed1.averageWeightString,
                        val2 = breed2.averageWeightString
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 10.dp))

                    ComparisonRow(
                        title = "Esperança de Vida".localized(),
                        val1 = "${breed1.expectedLifeYearsMin}-${breed1.expectedLifeYearsMax} " + "anos".localized(),
                        val2 = "${breed2.expectedLifeYearsMin}-${breed2.expectedLifeYearsMax} " + "anos".localized()
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 10.dp))

                    ComparisonRow(
                        title = "Nível de Energia".localized(),
                        val1 = breed1.energyLevel.displayName,
                        val2 = breed2.energyLevel.displayName
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 10.dp))

                    ComparisonRow(
                        title = "Escovagem / Cuidados".localized(),
                        val1 = breed1.groomingNeeds.displayName,
                        val2 = breed2.groomingNeeds.displayName
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 10.dp))

                    ComparisonRow(
                        title = "Sensibilidades".localized(),
                        val1 = if (breed1.commonSensitivities.isEmpty()) "Geral".localized() else breed1.commonSensitivities.joinToString(", ") { it.localized() },
                        val2 = if (breed2.commonSensitivities.isEmpty()) "Geral".localized() else breed2.commonSensitivities.joinToString(", ") { it.localized() }
                    )
                }
            }
        }
    }

    if (showPickerForSlot != null) {
        var pickerQuery by remember { mutableStateOf("") }
        val filtered = allBreeds.filter {
            pickerQuery.isEmpty() ||
            it.name.lowercase().contains(pickerQuery.lowercase()) ||
            it.name.localized().lowercase().contains(pickerQuery.lowercase())
        }

        AlertDialog(
            onDismissRequest = { showPickerForSlot = null },
            title = { Text("Selecionar Raça".localized(), fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.height(350.dp)) {
                    OutlinedTextField(
                        value = pickerQuery,
                        onValueChange = { pickerQuery = it },
                        placeholder = { Text("Pesquisar...".localized()) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(filtered) { b ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (showPickerForSlot == 1) breed1 = b else breed2 = b
                                        showPickerForSlot = null
                                    }
                                    .padding(vertical = 10.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = b.name.localized(),
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = b.species.displayName,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showPickerForSlot = null }) {
                    Text("Fechar".localized())
                }
            }
        )
    }
}

@Composable
fun BreedPickerCard(
    modifier: Modifier = Modifier,
    label: String,
    breed: PetBreed,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = color.copy(alpha = 0.12f)
            ) {
                Text(
                    text = label,
                    color = color,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = breed.name.localized(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = color)
            }
            Text(
                text = "${breed.species.displayName} • ${breed.sizeCategory.displayName}",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun ComparisonRow(
    title: String,
    val1: String,
    val2: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = val1,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = val2,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
