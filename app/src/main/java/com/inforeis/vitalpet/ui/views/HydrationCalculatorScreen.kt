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
fun HydrationCalculatorScreen(onBack: () -> Unit) {
    var weightText by remember { mutableStateOf("8.0") }
    var dehydrationPercent by remember { mutableFloatStateOf(5.0f) }
    var ongoingLossesText by remember { mutableStateOf("100.0") }
    var species by remember { mutableStateOf(Species.DOG) }

    val weight = weightText.toDoubleOrNull() ?: 8.0
    val losses = ongoingLossesText.toDoubleOrNull() ?: 0.0
    val res = ClinicalCalculatorService.calculateFluids(weight, dehydrationPercent.toDouble(), losses, species)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calculadora de Fluidos".localized(), fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
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
                modifier = Modifier.fillMaxWidth()
            )

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Grau de Desidratação Clínico (%)".localized(), fontWeight = FontWeight.SemiBold)
                    Text("%.0f%%".format(dehydrationPercent), fontWeight = FontWeight.Bold, color = TealPrimary)
                }
                Slider(
                    value = dehydrationPercent,
                    onValueChange = { dehydrationPercent = it },
                    valueRange = 0f..12f,
                    steps = 11
                )
            }

            OutlinedTextField(
                value = ongoingLossesText,
                onValueChange = { ongoingLossesText = it },
                label = { Text("Perdas Contínuas Estimadas (Vómito/Diarreia) mL".localized()) },
                modifier = Modifier.fillMaxWidth()
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Plano de Fluídos nas primeiras 24 horas".localized(), fontWeight = FontWeight.Bold, color = TealPrimary)
                    Spacer(modifier = Modifier.height(10.dp))
                    BarfRow("Volume de Manutenção".localized(), "%.0f mL".format(res.maintenanceMlPerDay))
                    BarfRow("Reposição de Défice".localized(), "%.0f mL".format(res.dehydrationDeficitMl))
                    BarfRow("Perdas Contínuas".localized(), "%.0f mL".format(res.ongoingLossesMl))
                    BarfRow("Volume Total 24 Horas".localized(), "%.0f mL".format(res.totalFirst24HoursMl))
                    BarfRow("Taxa de Infusão Contínua".localized(), "%.1f mL/hora".format(res.hourlyInfusionRateMl))
                }
            }
        }
    }
}
