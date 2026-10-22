package com.inforeis.vitalpet.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.inforeis.vitalpet.models.Species
import com.inforeis.vitalpet.services.ClinicalCalculatorService
import com.inforeis.vitalpet.services.LanguageManager.localized
import com.inforeis.vitalpet.ui.theme.TealPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarfCalculatorScreen(onBack: () -> Unit) {
    var weightText by remember { mutableStateOf("15.0") }
    var percentage by remember { mutableFloatStateOf(2.5f) }
    var species by remember { mutableStateOf(Species.DOG) }

    val weightKg = weightText.toDoubleOrNull() ?: 10.0
    val dist = ClinicalCalculatorService.calculateBarf(weightKg, percentage.toDouble(), species == Species.CAT)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dieta Crua (BARF)".localized(), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar".localized())
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Species.entries.forEach { sp ->
                    FilterChip(
                        selected = species == sp,
                        onClick = { species = sp },
                        label = { Text(sp.displayName) }
                    )
                }
            }

            OutlinedTextField(
                value = weightText,
                onValueChange = { weightText = it },
                label = { Text("Peso do Animal (kg)".localized()) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Percentagem Diária do Peso Corporal".localized(), fontWeight = FontWeight.SemiBold)
                    Text("%.1f%%".format(percentage), fontWeight = FontWeight.Bold, color = TealPrimary)
                }
                Slider(
                    value = percentage,
                    onValueChange = { percentage = it },
                    valueRange = 1.5f..5.0f,
                    steps = 7
                )
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total Diário: %.0f g".format(dist.totalDailyGrams), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TealPrimary))
                    Spacer(modifier = Modifier.height(12.dp))
                    BarfRow("Carne Muscular".localized(), "%.0f g".format(dist.muscleMeatGrams))
                    BarfRow("Ossos Carnudos Crús".localized(), "%.0f g".format(dist.rawMeatyBonesGrams))
                    BarfRow("Fígado".localized(), "%.0f g".format(dist.liverGrams))
                    BarfRow("Outras Vísceras".localized(), "%.0f g".format(dist.organGrams))
                    BarfRow("Vegetais Puré".localized(), "%.0f g".format(dist.vegetableGrams))
                    if (species == Species.DOG) {
                        BarfRow("Fruta Triturada".localized(), "%.0f g".format(dist.fruitGrams))
                    }
                }
            }
        }
    }
}

@Composable
fun BarfRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, fontWeight = FontWeight.SemiBold)
    }
}
