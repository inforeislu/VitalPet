package com.inforeis.vitalpet.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
fun MedicationDoseCalculatorScreen(onBack: () -> Unit) {
    var weightText by remember { mutableStateOf("12.0") }
    var doseMgKgText by remember { mutableStateOf("10.0") }
    var concMgMlText by remember { mutableStateOf("50.0") }

    val weight = weightText.toDoubleOrNull() ?: 10.0
    val dose = doseMgKgText.toDoubleOrNull() ?: 5.0
    val conc = concMgMlText.toDoubleOrNull()

    val (_, explanation) = ClinicalCalculatorService.calculateMedicationDose(weight, dose, conc)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calculadora Farmacológica".localized(), fontWeight = FontWeight.Bold) },
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
            OutlinedTextField(
                value = weightText,
                onValueChange = { weightText = it },
                label = { Text("Peso do Animal (kg)".localized()) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = doseMgKgText,
                onValueChange = { doseMgKgText = it },
                label = { Text("Dose Prescrita (mg/kg)".localized()) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = concMgMlText,
                onValueChange = { concMgMlText = it },
                label = { Text("Concentração da Suspensão Oral (mg/mL)".localized()) },
                modifier = Modifier.fillMaxWidth()
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Resultado Terapêutico".localized(), fontWeight = FontWeight.Bold, color = TealPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(explanation, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}
