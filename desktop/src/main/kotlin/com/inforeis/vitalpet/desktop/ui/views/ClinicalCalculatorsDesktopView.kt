package com.inforeis.vitalpet.desktop.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
import com.inforeis.vitalpet.desktop.ui.theme.AmberAccent
import com.inforeis.vitalpet.desktop.ui.theme.TealPrimary
import com.inforeis.vitalpet.models.DogSizeCategory
import com.inforeis.vitalpet.models.Species
import com.inforeis.vitalpet.services.ClinicalCalculatorService
import com.inforeis.vitalpet.services.LanguageManager.localized
import kotlin.math.roundToInt


@Composable
fun ClinicalCalculatorsDesktopView() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf(
        "BARF & Dieta Natural".localized(),
        "Fluidoterapia & Desidratação".localized(),
        "Rótulos & Matéria Seca".localized(),
        "Calculadora de Dosagem Farmacológica".localized(),
        "Conversão de Idade".localized(),
        "Condição Corporal (BCS)".localized()
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Calculadoras Clínicas Veterinárias".localized(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Fórmulas de precisão médica baseadas nas diretrizes WSAVA e NRC.".localized(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = TealPrimary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontSize = 12.sp, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        when (selectedTab) {
            0 -> BarfCalculatorSection()
            1 -> FluidTherapySection()
            2 -> FoodLabelSection()
            3 -> MedicationDoseSection()
            4 -> AgeConversionSection()
            5 -> BCSEvaluationSection()
        }
    }
}

@Composable
private fun BarfCalculatorSection() {
    var weightInput by remember { mutableStateOf("15.0") }
    var percentage by remember { mutableStateOf(2.5f) }
    var isCat by remember { mutableStateOf(false) }

    val weight = weightInput.toDoubleOrNull() ?: 15.0
    val result = ClinicalCalculatorService.calculateBarf(weight, percentage.toDouble(), isCat)

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
        // Entradas
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Parâmetros do Animal".localized(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { isCat = false },
                        colors = ButtonDefaults.buttonColors(containerColor = if (!isCat) TealPrimary else MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text("🐶 Cão (50% carne, 17% osso carnosos)".localized())
                    }
                    Button(
                        onClick = { isCat = true },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isCat) TealPrimary else MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text("🐱 Gato (Carnívoro estrito)".localized())
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { weightInput = it },
                    label = { Text("Peso Corporal (kg)".localized()) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Percentagem Diária: ${String.format("%.1f", percentage)}%", fontWeight = FontWeight.SemiBold)
                Slider(
                    value = percentage,
                    onValueChange = { percentage = it },
                    valueRange = 1.5f..4.5f,
                    steps = 5
                )
            }
        }

        // Resultados
        Card(
            modifier = Modifier.weight(1.2f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Restaurant, contentDescription = null, tint = TealPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Dose Diária Recomendada:".localized(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "${result.totalDailyGrams.roundToInt()} g / dia",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TealPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                BarfPortionRow("🥩 Carne Muscular:".localized(), "${result.muscleMeatGrams.roundToInt()} g")
                BarfPortionRow("🦴 Ossos Carnosos Crus:".localized(), "${result.rawMeatyBonesGrams.roundToInt()} g")
                BarfPortionRow("🫀 Vísceras / Fígado:".localized(), "${result.liverGrams.roundToInt()} g + ${result.organGrams.roundToInt()} g")
                if (!isCat) {
                    BarfPortionRow("🥦 Legumes e Vegetais:".localized(), "${result.vegetableGrams.roundToInt()} g")
                    BarfPortionRow("🍎 Frutas:".localized(), "${result.fruitGrams.roundToInt()} g")
                }
            }
        }
    }
}

@Composable
private fun BarfPortionRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun FluidTherapySection() {
    var weightInput by remember { mutableStateOf("10.0") }
    var dehydrationPercent by remember { mutableStateOf(5f) }
    var isCat by remember { mutableStateOf(false) }

    val weight = weightInput.toDoubleOrNull() ?: 10.0
    val fluidPlan = ClinicalCalculatorService.calculateFluids(weight, dehydrationPercent.toDouble(), 0.0)

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Avaliação de Desidratação".localized(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { weightInput = it },
                    label = { Text("Peso do Paciente (kg)".localized()) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Grau de Desidratação: ${dehydrationPercent.roundToInt()}%", fontWeight = FontWeight.SemiBold)
                Slider(
                    value = dehydrationPercent,
                    onValueChange = { dehydrationPercent = it },
                    valueRange = 0f..12f,
                    steps = 11
                )
                Text(
                    text = when {
                        dehydrationPercent < 5f -> "Sem sinais clínicos evidentes (<5%)"
                        dehydrationPercent <= 6f -> "Perda leve de elasticidade cutânea (5-6%)"
                        dehydrationPercent <= 8f -> "Turgor retardado, mucosas secas (7-8%)"
                        dehydrationPercent <= 10f -> "Olhos enoftálmicos, tempo de recapilarização prolongado (9-10%)"
                        else -> "Choque hipovolêmico severo (>10%)"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Card(
            modifier = Modifier.weight(1.2f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocalDrink, contentDescription = null, tint = Color(0xFF3B82F6))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Protocolo de Reposição (24h)".localized(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "${fluidPlan.totalFirst24HoursMl.roundToInt()} mL / 24h",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3B82F6)
                )
                Text("Velocidade: ${fluidPlan.hourlyInfusionRateMl.roundToInt()} mL / hora", fontWeight = FontWeight.SemiBold)

                Spacer(modifier = Modifier.height(16.dp))

                BarfPortionRow("Manutenção basal:".localized(), "${fluidPlan.maintenanceMlPerDay.roundToInt()} mL")
                BarfPortionRow("Défice de desidratação:".localized(), "${fluidPlan.dehydrationDeficitMl.roundToInt()} mL")
            }
        }
    }
}

@Composable
private fun FoodLabelSection() {
    var protein by remember { mutableStateOf("26.0") }
    var fat by remember { mutableStateOf("15.0") }
    var fiber by remember { mutableStateOf("3.0") }
    var moisture by remember { mutableStateOf("10.0") }
    var ash by remember { mutableStateOf("7.0") }

    val analysis = ClinicalCalculatorService.analyzeFoodLabel(
        protein.toDoubleOrNull() ?: 26.0,
        fat.toDoubleOrNull() ?: 15.0,
        fiber.toDoubleOrNull() ?: 3.0,
        moisture.toDoubleOrNull() ?: 10.0,
        ash.toDoubleOrNull() ?: 7.0
    )

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Garantia de Análise do Rótulo (%)".localized(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(value = protein, onValueChange = { protein = it }, label = { Text("Proteína Bruta (%)".localized()) }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = fat, onValueChange = { fat = it }, label = { Text("Gordura / Extrato Etéreo (%)".localized()) }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = fiber, onValueChange = { fiber = it }, label = { Text("Fibra Bruta (%)".localized()) }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = moisture, onValueChange = { moisture = it }, label = { Text("Humidade Máxima (%)".localized()) }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = ash, onValueChange = { ash = it }, label = { Text("Matéria Mineral / Cinzas (%)".localized()) }, modifier = Modifier.fillMaxWidth())
            }
        }

        Card(
            modifier = Modifier.weight(1.2f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Composição em Matéria Seca (DM)".localized(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                BarfPortionRow("Matéria Seca Total:".localized(), "${String.format("%.1f", analysis.dryMatterPercent)}%")
                BarfPortionRow("Carboidratos Ocultos (NFE):".localized(), "${String.format("%.1f", analysis.dryMatterCarbs)}% (DM)")
                BarfPortionRow("Proteína em Base Seca:".localized(), "${String.format("%.1f", analysis.dryMatterProtein)}% (DM)")
                BarfPortionRow("Gordura em Base Seca:".localized(), "${String.format("%.1f", analysis.dryMatterFat)}% (DM)")

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AmberAccent.copy(alpha = 0.15f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Classificação Nutricional:".localized(), fontWeight = FontWeight.Bold, color = AmberAccent)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (analysis.dryMatterCarbs > 45.0) "Ração com alto teor de carboidratos (>45%). Avalie com cautela em diabéticos ou obesos."
                            else "Perfil balanceado de carboidratos.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MedicationDoseSection() {
    var weightInput by remember { mutableStateOf("10.0") }
    var doseInput by remember { mutableStateOf("5.0") }
    var concInput by remember { mutableStateOf("25.0") }
    var tabletStrengthInput by remember { mutableStateOf("50.0") }

    val weight = weightInput.toDoubleOrNull() ?: 10.0
    val dose = doseInput.toDoubleOrNull() ?: 5.0
    val conc = concInput.toDoubleOrNull()
    val tablet = tabletStrengthInput.toDoubleOrNull()

    val (totalMg, explanation) = ClinicalCalculatorService.calculateMedicationDose(
        weightKg = weight,
        doseMgPerKg = dose,
        concentrationMgPerMl = conc,
        tabletStrengthMg = tablet
    )

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Parâmetros Farmacológicos".localized(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { weightInput = it },
                    label = { Text("Peso do Animal (kg)".localized()) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = doseInput,
                    onValueChange = { doseInput = it },
                    label = { Text("Dose Prescrita (mg/kg)".localized()) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = concInput,
                    onValueChange = { concInput = it },
                    label = { Text("Concentração da Suspensão Oral (mg/mL)".localized()) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = tabletStrengthInput,
                    onValueChange = { tabletStrengthInput = it },
                    label = { Text("Dosagem por Comprimido (mg)".localized()) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Card(
            modifier = Modifier.weight(1.2f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Resultado Terapêutico".localized(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                BarfPortionRow("Dose Total Requerida:".localized(), "${String.format("%.2f", totalMg)} mg")
                if (conc != null && conc > 0) {
                    val vol = totalMg / conc
                    BarfPortionRow("Volume em Suspensão:".localized(), "${String.format("%.2f", vol)} mL")
                }
                if (tablet != null && tablet > 0) {
                    val tabs = totalMg / tablet
                    BarfPortionRow("Fração de Comprimidos:".localized(), "${String.format("%.2f", tabs)} " + "Comprimido".localized())
                }

                Spacer(modifier = Modifier.height(20.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = TealPrimary.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Resumo da Prescrição:".localized(), fontWeight = FontWeight.Bold, color = TealPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(explanation, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun AgeConversionSection() {
    var species by remember { mutableStateOf(Species.DOG) }
    var dogSize by remember { mutableStateOf(DogSizeCategory.MEDIUM) }
    var petAgeInput by remember { mutableStateOf("3") }

    val petAge = petAgeInput.toIntOrNull() ?: 3

    val ageTable = (1..15).map { petYears ->
        val humanYears = if (species == Species.CAT) {
            when (petYears) {
                1 -> 15
                2 -> 24
                else -> 24 + (petYears - 2) * 4
            }
        } else {
            when (dogSize) {
                DogSizeCategory.SMALL -> if (petYears == 1) 15 else if (petYears == 2) 24 else 24 + (petYears - 2) * 4
                DogSizeCategory.MEDIUM -> if (petYears == 1) 15 else if (petYears == 2) 24 else 24 + (petYears - 2) * 5
                DogSizeCategory.LARGE -> if (petYears == 1) 14 else if (petYears == 2) 22 else 22 + (petYears - 2) * 7
                DogSizeCategory.GIANT -> if (petYears == 1) 12 else if (petYears == 2) 22 else 22 + (petYears - 2) * 8
            }
        }
        Pair(petYears, humanYears)
    }

    val currentHumanYears = ageTable.find { it.first == petAge }?.second ?: (petAge * 6)

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Espécie e Porte Fisiológico".localized(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Species.entries.forEach { sp ->
                        Button(
                            onClick = { species = sp },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (species == sp) TealPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (species == sp) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(sp.displayName)
                        }
                    }
                }

                if (species == Species.DOG) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Porte do Cão".localized(), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        DogSizeCategory.entries.forEach { size ->
                            Button(
                                onClick = { dogSize = size },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (dogSize == size) TealPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (dogSize == size) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(size.displayName, fontSize = 12.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = petAgeInput,
                    onValueChange = { petAgeInput = it },
                    label = { Text("Idade do Pet".localized()) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = AmberAccent.copy(alpha = 0.15f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Equivalência Humana Calculada:".localized(), fontWeight = FontWeight.Bold, color = AmberAccent)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$petAge " + (if (petAge == 1) "ano".localized() else "anos".localized()) +
                                    " ≈ ~$currentHumanYears " + "anos humanos".localized(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }

        Card(
            modifier = Modifier.weight(1.2f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Tabela de Equivalência Fisiológica".localized(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                ageTable.forEach { (petY, humY) ->
                    val isCurrent = petY == petAge
                    Surface(
                        color = if (isCurrent) TealPrimary.copy(alpha = 0.12f) else Color.Transparent,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$petY " + (if (petY == 1) "ano".localized() else "anos".localized()),
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                            )
                            Text(
                                text = "~$humY " + "anos humanos".localized(),
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.SemiBold,
                                color = if (isCurrent) TealPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BCSEvaluationSection() {
    var bcsScore by remember { mutableStateOf(5f) }
    val score = bcsScore.roundToInt().coerceIn(1, 9)

    val (category, description, color) = when (score) {
        1 -> Triple("Caquético / Muito Magro".localized(), "Costelas, vértebras lombares e ossos da bacia facilmente visíveis. Nenhuma gordura palpável. Perda evidente de massa muscular.", Color(0xFFEF4444))
        2 -> Triple("Muito Abaixo do Peso".localized(), "Costelas facilmente palpáveis com mínima cobertura de gordura. Vértebras lombares evidentes. Cintura vincada acentuada.", Color(0xFFF59E0B))
        3 -> Triple("Abaixo do Peso Ideal".localized(), "Costelas palpáveis facilmente sem gordura em excesso. Cintura visível quando vista de cima. Concavidade abdominal evidente.", Color(0xFFF59E0B))
        4 -> Triple("Peso Ideal (Mínimo)".localized(), "Costelas palpáveis com leve cobertura de gordura. Cintura visível atrás das costelas. Abdómen bem delineado.", TealPrimary)
        5 -> Triple("Peso Ideal (Perfeito)".localized(), "Padrão ouro WSAVA: costelas facilmente palpáveis sem excesso de gordura. Cintura evidente e dobra abdominal visível.", TealPrimary)
        6 -> Triple("Ligeiro Sobrepeso".localized(), "Costelas palpáveis com leve excesso de gordura de cobertura. Cintura ainda discernível mas pouco evidente.", Color(0xFFF59E0B))
        7 -> Triple("Acima do Peso".localized(), "Costelas difíceis de palpar devido a capa espessa de gordura. Depósitos adiposos na zona lombar e na base da cauda.", Color(0xFFF59E0B))
        8 -> Triple("Obeso".localized(), "Costelas muito difíceis de palpar ou inacessíveis. Distensão abdominal evidente e perda total de linha de cintura.", Color(0xFFEF4444))
        else -> Triple("Obesidade Severa".localized(), "Depósitos maciços de gordura no tórax, coluna e base da cauda. Abdómen pendular e mobilidade comprometida.", Color(0xFFEF4444))
    }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Avaliação de Condição Corporal (BCS 1-9)".localized(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Arraste a barra para selecionar a condição corporal do pet:".localized(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Score: $score/9", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = color)
                    Surface(shape = RoundedCornerShape(8.dp), color = color.copy(alpha = 0.15f)) {
                        Text(category, fontWeight = FontWeight.Bold, color = color, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                    }
                }

                Slider(
                    value = bcsScore,
                    onValueChange = { bcsScore = it },
                    valueRange = 1f..9f,
                    steps = 7,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                )
            }
        }

        Card(
            modifier = Modifier.weight(1.2f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Critérios Clínicos WSAVA:".localized(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                Text(description, style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(20.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = color.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Recomendação Nutricional:".localized(), fontWeight = FontWeight.Bold, color = color)
                        Spacer(modifier = Modifier.height(4.dp))
                        val planText = when {
                            score in 4..5 -> "Manutenção padrão (100% DER). Manter plano alimentar estável e pesagens mensais."
                            score < 4 -> "Défice nutricional. Recomenda-se aumento calórico progressivo (+15% a +25% DER) e investigação clínica."
                            else -> "Sobrepeso / Obesidade. Recomenda-se redução calórica (-15% a -20% DER) e aumento gradual da atividade física."
                        }
                        Text(planText, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

