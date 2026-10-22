package com.inforeis.vitalpet.desktop.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.inforeis.vitalpet.desktop.services.DesktopReportService
import com.inforeis.vitalpet.desktop.ui.components.DesktopWeightEvolutionChart
import com.inforeis.vitalpet.desktop.ui.theme.AmberAccent
import com.inforeis.vitalpet.desktop.ui.theme.TealPrimary
import com.inforeis.vitalpet.models.ActivityLevel
import com.inforeis.vitalpet.models.MedicalVisit
import com.inforeis.vitalpet.models.NeuterStatus
import com.inforeis.vitalpet.models.Pet
import com.inforeis.vitalpet.models.PetGender
import com.inforeis.vitalpet.models.Species
import com.inforeis.vitalpet.models.VaccineLog
import com.inforeis.vitalpet.models.WeightLog
import com.inforeis.vitalpet.services.DesktopPetRepository
import com.inforeis.vitalpet.services.LanguageManager.localized
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PetsMasterDetailView() {
    val pets by DesktopPetRepository.pets.collectAsState()
    var selectedPetId by remember { mutableStateOf<String?>(pets.firstOrNull()?.id) }
    var showAddDialog by remember { mutableStateOf(false) }

    val selectedPet = pets.find { it.id == selectedPetId } ?: pets.firstOrNull()

    Row(modifier = Modifier.fillMaxSize()) {
        // Coluna Esquerda: Lista de Animais (Master)
        Surface(
            modifier = Modifier
                .width(320.dp)
                .fillMaxHeight(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Meus Pets".localized(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Button(
                        onClick = { showAddDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Adicionar".localized(), fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (pets.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Pets, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Nenhum animal registado.".localized(), color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { showAddDialog = true }) {
                                Text("Criar Primeiro Pet".localized())
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(pets) { pet ->
                            val isSelected = pet.id == selectedPet?.id
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedPetId = pet.id },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) TealPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                ),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, TealPrimary) else null
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(if (pet.species == Species.DOG) Color(0xFF3B82F6) else Color(0xFFEC4899)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (pet.species == Species.DOG) "🐶" else "🐱",
                                            fontSize = 20.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = pet.name,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Text(
                                            text = "${pet.breed} • ${pet.currentWeightKg} kg",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        // Coluna Direita: Detalhes do Animal Selecionado (Detail)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (selectedPet != null) {
                PetDetailView(
                    pet = selectedPet,
                    onDelete = {
                        DesktopPetRepository.deletePet(selectedPet.id)
                        selectedPetId = pets.firstOrNull { it.id != selectedPet.id }?.id
                    }
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Selecione um animal para visualizar o prontuário clínico.".localized())
                }
            }
        }
    }

    if (showAddDialog) {
        AddPetDesktopDialog(
            onDismiss = { showAddDialog = false },
            onSave = { newPet ->
                DesktopPetRepository.addPet(newPet)
                selectedPetId = newPet.id
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun PetDetailView(pet: Pet, onDelete: () -> Unit) {
    var newWeightInput by remember { mutableStateOf("") }
    var showAddVaccineDialog by remember { mutableStateOf(false) }
    var showAddVisitDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(TealPrimary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = if (pet.species == Species.DOG) "🐶" else "🐱", fontSize = 32.sp)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = pet.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${pet.species.displayName} • ${pet.breed} • ${pet.ageInYears} anos • BCS ${pet.bcs}/9",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = { DesktopReportService.generateAndOpenDigitalPassport(pet) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Badge, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Passaporte Veterinário".localized(), color = MaterialTheme.colorScheme.onPrimaryContainer)
                }

                Button(
                    onClick = { DesktopReportService.generateAndOpenClinicalReport(pet) },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Imprimir Prontuário".localized())
                }

                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFEF4444))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Métricas Clínicas WSAVA em Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MetricCard(
                modifier = Modifier.weight(1f),
                title = "RER (Repouso)".localized(),
                value = "${pet.rer.toInt()} kcal",
                subtitle = "Fórmula WSAVA 70*(kg^0.75)"
            )
            MetricCard(
                modifier = Modifier.weight(1f),
                title = "DER (Manutenção)".localized(),
                value = "${pet.der.toInt()} kcal",
                subtitle = "Necessidade Diária Total"
            )
            MetricCard(
                modifier = Modifier.weight(1f),
                title = "BCS Corporal".localized(),
                value = "${pet.bcs} / 9",
                subtitle = pet.bcsCategory
            )
            MetricCard(
                modifier = Modifier.weight(1f),
                title = "Peso Atual".localized(),
                value = "${pet.currentWeightKg} kg",
                subtitle = if (pet.targetWeightKg != null) "Meta: ${pet.targetWeightKg} kg" else "Sem meta"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Gráfico de Evolução Ponderal
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = TealPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Curva de Evolução Ponderal".localized(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Registar peso rápido
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newWeightInput,
                            onValueChange = { newWeightInput = it },
                            placeholder = { Text("Ex: 15.4") },
                            modifier = Modifier.width(110.dp),
                            singleLine = true
                        )

                        Button(
                            onClick = {
                                val w = newWeightInput.toDoubleOrNull()
                                if (w != null && w > 0) {
                                    val updatedWeights = pet.weightLogs.toMutableList()
                                    updatedWeights.add(WeightLog(date = Date(), weightKg = w))
                                    val updatedPet = pet.copy(
                                        currentWeightKg = w,
                                        weightLogs = updatedWeights
                                    )
                                    DesktopPetRepository.updatePet(updatedPet)
                                    newWeightInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Pesar".localized())
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                DesktopWeightEvolutionChart(
                    weightLogs = pet.weightLogs,
                    targetWeightKg = pet.targetWeightKg
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Seção Vacinas e Consultas Médicas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Vacinas
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Vaccines, contentDescription = null, tint = TealPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Registo Vacinal".localized(), fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { showAddVaccineDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary.copy(alpha = 0.15f)),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Vacina".localized(), color = TealPrimary, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (pet.vaccineLogs.isEmpty()) {
                        Text("Nenhuma vacina registada.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                    } else {
                        pet.vaccineLogs.take(6).forEach { v ->
                            Text("💉 ${v.vaccineName} (${SimpleDateFormat("dd/MM/yyyy", Locale.US).format(v.applicationDate)})", fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }

            // Consultas
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalHospital, contentDescription = null, tint = AmberAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Histórico Clínico".localized(), fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { showAddVisitDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = AmberAccent.copy(alpha = 0.15f)),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = AmberAccent, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Consulta".localized(), color = AmberAccent, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (pet.medicalVisits.isEmpty()) {
                        Text("Nenhuma consulta médica registada.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                    } else {
                        pet.medicalVisits.take(6).forEach { m ->
                            Text("🩺 ${m.reason} (${SimpleDateFormat("dd/MM/yyyy", Locale.US).format(m.date)})", fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }
    }

    // Diálogos de Adição
    if (showAddVaccineDialog) {
        AddVaccineDesktopDialog(
            onDismiss = { showAddVaccineDialog = false },
            onSave = { vaccine ->
                val updatedList = pet.vaccineLogs.toMutableList()
                updatedList.add(vaccine)
                DesktopPetRepository.updatePet(pet.copy(vaccineLogs = updatedList))
                showAddVaccineDialog = false
            }
        )
    }

    if (showAddVisitDialog) {
        AddVisitDesktopDialog(
            onDismiss = { showAddVisitDialog = false },
            onSave = { visit ->
                val updatedList = pet.medicalVisits.toMutableList()
                updatedList.add(visit)
                DesktopPetRepository.updatePet(pet.copy(medicalVisits = updatedList))
                showAddVisitDialog = false
            }
        )
    }
}

@Composable
private fun MetricCard(
    modifier: Modifier,
    title: String,
    value: String,
    subtitle: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = TealPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
        }
    }
}

@Composable
private fun AddPetDesktopDialog(onDismiss: () -> Unit, onSave: (Pet) -> Unit) {
    var name by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var weightText by remember { mutableStateOf("") }
    var isDog by remember { mutableStateOf(true) }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.width(420.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Adicionar Novo Pet".localized(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { isDog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isDog) TealPrimary else MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text("🐶 Cão".localized())
                    }
                    Button(
                        onClick = { isDog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = if (!isDog) TealPrimary else MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text("🐱 Gato".localized())
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome do Animal".localized()) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = breed,
                    onValueChange = { breed = it },
                    label = { Text("Raça".localized()) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it },
                    label = { Text("Peso Atual (kg)".localized()) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Cancelar".localized())
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            val w = weightText.toDoubleOrNull() ?: 5.0
                            val p = Pet(
                                name = name.ifBlank { "Sem Nome" },
                                species = if (isDog) Species.DOG else Species.CAT,
                                breed = breed.ifBlank { "Indefinida" },
                                birthDate = Date(),
                                currentWeightKg = w,
                                gender = PetGender.MALE,
                                neuterStatus = NeuterStatus.NEUTERED,
                                activityLevel = ActivityLevel.NORMAL,
                                weightLogs = mutableListOf(WeightLog(date = Date(), weightKg = w))
                            )
                            onSave(p)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                    ) {
                        Text("Guardar".localized())
                    }
                }
            }
        }
    }
}

@Composable
private fun AddVaccineDesktopDialog(onDismiss: () -> Unit, onSave: (VaccineLog) -> Unit) {
    var vaccineName by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.width(420.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Adicionar Vacina".localized(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = vaccineName,
                    onValueChange = { vaccineName = it },
                    label = { Text("Nome da Vacina".localized()) },
                    placeholder = { Text("Ex: Raiva, Nobivac DHPPi, Felocell") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Lote / Observações (Opcional)".localized()) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onDismiss) { Text("Cancelar".localized()) }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            if (vaccineName.isNotBlank()) {
                                onSave(VaccineLog(vaccineName = vaccineName, applicationDate = Date()))
                            }
                        },
                        enabled = vaccineName.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                    ) {
                        Text("Guardar".localized())
                    }
                }
            }
        }
    }
}

@Composable
private fun AddVisitDesktopDialog(onDismiss: () -> Unit, onSave: (MedicalVisit) -> Unit) {
    var reason by remember { mutableStateOf("") }
    var clinicName by remember { mutableStateOf("") }
    var diagnosis by remember { mutableStateOf("") }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.width(420.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Registar Consulta Médica".localized(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Motivo da Consulta".localized()) },
                    placeholder = { Text("Ex: Check-up anual, Otite, Coceira") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = clinicName,
                    onValueChange = { clinicName = it },
                    label = { Text("Clínica ou Veterinário".localized()) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = diagnosis,
                    onValueChange = { diagnosis = it },
                    label = { Text("Diagnóstico".localized()) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onDismiss) { Text("Cancelar".localized()) }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            if (reason.isNotBlank()) {
                                onSave(MedicalVisit(
                                    reason = reason,
                                    clinicName = clinicName.ifBlank { "Clínica Geral" },
                                    diagnosis = diagnosis.ifBlank { null }
                                ))
                            }
                        },
                        enabled = reason.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                    ) {
                        Text("Guardar".localized())
                    }
                }
            }
        }
    }
}
