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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.inforeis.vitalpet.services.ClinicalCalculatorService
import com.inforeis.vitalpet.services.LanguageManager.localized
import com.inforeis.vitalpet.ui.theme.TealPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetFoodLabelAnalyzerScreen(onBack: () -> Unit) {
    var proteinText by remember { mutableStateOf("26.0") }
    var fatText by remember { mutableStateOf("14.0") }
    var fiberText by remember { mutableStateOf("3.0") }
    var moistureText by remember { mutableStateOf("10.0") }
    var ashText by remember { mutableStateOf("7.5") }

    val protein = proteinText.toDoubleOrNull() ?: 25.0
    val fat = fatText.toDoubleOrNull() ?: 12.0
    val fiber = fiberText.toDoubleOrNull() ?: 3.0
    val moisture = moistureText.toDoubleOrNull() ?: 10.0
    val ash = ashText.toDoubleOrNull() ?: 7.5

    val result = ClinicalCalculatorService.analyzeFoodLabel(protein, fat, fiber, moisture, ash)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analisador de Rótulos".localized(), fontWeight = FontWeight.Bold) },
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
            Text(
                "Insira os valores garantidos presentes no verso do saco de ração (%):".localized(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = proteinText,
                    onValueChange = { proteinText = it },
                    label = { Text("Proteína Bruta (%)".localized()) },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = fatText,
                    onValueChange = { fatText = it },
                    label = { Text("Gordura Bruta (%)".localized()) },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = fiberText,
                    onValueChange = { fiberText = it },
                    label = { Text("Fibra Bruta (%)".localized()) },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = moistureText,
                    onValueChange = { moistureText = it },
                    label = { Text("Humidade (%)".localized()) },
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = ashText,
                onValueChange = { ashText = it },
                label = { Text("Cinzas / Matéria Mineral (%)".localized()) },
                modifier = Modifier.fillMaxWidth()
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Resultados na Matéria Seca (DM)".localized(), fontWeight = FontWeight.Bold, color = TealPrimary)
                    Spacer(modifier = Modifier.height(10.dp))
                    BarfRow("Matéria Seca Total".localized(), "%.1f%%".format(result.dryMatterPercent))
                    BarfRow("Proteína Real (DM)".localized(), "%.1f%%".format(result.dryMatterProtein))
                    BarfRow("Gordura Real (DM)".localized(), "%.1f%%".format(result.dryMatterFat))
                    BarfRow("Carboidratos Ocultos (NFE)".localized(), "%.1f%%".format(result.dryMatterCarbs))
                    BarfRow("Estimativa Calórica".localized(), "%.0f kcal/100g".format(result.calculatedKcalPer100g))
                }
            }
        }
    }
}
