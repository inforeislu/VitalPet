package com.inforeis.vitalpet.desktop.ui.views

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.window.Dialog
import com.inforeis.vitalpet.desktop.ui.theme.AmberAccent
import com.inforeis.vitalpet.desktop.ui.theme.TealPrimary
import com.inforeis.vitalpet.models.AppetiteLevel
import com.inforeis.vitalpet.models.EnergyLevel
import com.inforeis.vitalpet.models.SymptomLog
import com.inforeis.vitalpet.models.WaterIntakeLevel
import com.inforeis.vitalpet.services.DesktopPetRepository
import com.inforeis.vitalpet.services.LanguageManager.localized
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.math.roundToInt

@Composable
fun SymptomJournalDesktopView() {
    val pets by DesktopPetRepository.pets.collectAsState()
    var selectedPetId by remember(pets) { mutableStateOf(pets.firstOrNull()?.id ?: "") }
    val activePet = pets.find { it.id == selectedPetId } ?: pets.firstOrNull()
    var showAddDialog by remember { mutableStateOf(false) }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val sevenDaysAgo = remember { Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -7) }.time }
    val threeDaysAgo = remember { Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -3) }.time }

    val recentLogs = activePet?.symptomLogs?.filter { it.date >= sevenDaysAgo } ?: emptyList()
    val recentVomitingCount = recentLogs.sumOf { it.vomitingEpisodes }
    val recentDiarrheaAlert = activePet?.symptomLogs
        ?.filter { it.date >= threeDaysAgo }
        ?.any { it.fecalScore >= 6 } ?: false

    Row(modifier = Modifier.fillMaxSize()) {
        // Painel Esquerdo
        Surface(
            modifier = Modifier.width(280.dp).fillMaxHeight(),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MonitorHeart, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Diário de Sintomas".localized(),
                        fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TealPrimary
                    )
                }
                Divider()
                if (pets.isEmpty()) {
                    Text("Registe primeiro um pet.".localized(), fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                } else {
                    Text("Selecionar Pet".localized(), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    pets.forEach { pet ->
                        val isSelected = pet.id == activePet?.id
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedPetId = pet.id },
                            label = { Text(pet.name, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            leadingIcon = if (isSelected) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null,
                            modifier = Modifier.fillMaxWidth(),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TealPrimary.copy(alpha = 0.15f),
                                selectedLabelColor = TealPrimary
                            )
                        )
                    }
                }
                Divider()
                if (activePet != null) {
                    Text("Resumo 7 Dias".localized(), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    SideStatCard("Registos Totais".localized(), "${activePet.symptomLogs.size}", TealPrimary)
                    SideStatCard("Últimos 7 Dias".localized(), "${recentLogs.size}", AmberAccent)
                    SideStatCard(
                        "Episódios Vómito (7d)".localized(), "$recentVomitingCount",
                        if (recentVomitingCount > 0) Color(0xFFE57373) else Color(0xFF66BB6A)
                    )
                    if (recentDiarrheaAlert) {
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFCDD2))
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFC62828), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Alerta: fezes moles (3d)".localized(), fontSize = 12.sp, color = Color(0xFFC62828), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { showAddDialog = true },
                    enabled = activePet != null,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Registar Sintoma".localized(), fontWeight = FontWeight.Bold)
                }
            }
        }

        // Painel Direito: Timeline
        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
            when {
                activePet == null -> EmptyPetState("Registe primeiro um pet.".localized())
                activePet.symptomLogs.isEmpty() -> Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Sem registos".localized(), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { showAddDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)) {
                        Text("Primeiro Registo".localized())
                    }
                }
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text("${activePet.name} — ${"Histórico Clínico".localized()}", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TealPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    val sorted = activePet.symptomLogs.sortedByDescending { it.date }
                    items(sorted) { log ->
                        SymptomLogCard(log = log, dateFormat = dateFormat, onDelete = {
                            val updated = activePet.copy(symptomLogs = activePet.symptomLogs.filterNot { it.id == log.id }.toMutableList())
                            DesktopPetRepository.updatePet(updated)
                        })
                    }
                }
            }
        }
    }

    if (showAddDialog && activePet != null) {
        AddSymptomDesktopDialog(
            petName = activePet.name,
            onDismiss = { showAddDialog = false },
            onSave = { log ->
                val updated = activePet.copy(symptomLogs = (activePet.symptomLogs + log).toMutableList())
                DesktopPetRepository.updatePet(updated)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun EmptyPetState(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Pets, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(12.dp))
            Text(message, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
        }
    }
}

@Composable
private fun SideStatCard(label: String, value: String, color: Color) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            Text(value, fontWeight = FontWeight.Bold, color = color, fontSize = 16.sp)
        }
    }
}

@Composable
private fun SymptomLogCard(log: SymptomLog, dateFormat: SimpleDateFormat, onDelete: () -> Unit) {
    val severityColor = when {
        log.fecalScore >= 6 || log.vomitingEpisodes >= 2 || log.energyLevel == EnergyLevel.LETHARGIC || log.appetite == AppetiteLevel.ANOREXIC -> Color(0xFFE53935)
        log.fecalScore >= 4 || log.vomitingEpisodes == 1 || log.energyLevel == EnergyLevel.LOW || log.appetite == AppetiteLevel.DECREASED -> Color(0xFFFFA726)
        else -> Color(0xFF66BB6A)
    }
    Card(
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(severityColor))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(dateFormat.format(log.date), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Eliminar".localized(), tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                SymptomInfoChip("Score Fecal WSAVA".localized(), "${log.fecalScore}/7", if (log.fecalScore >= 6) Color(0xFFE53935) else if (log.fecalScore >= 4) AmberAccent else Color(0xFF66BB6A))
                SymptomInfoChip("Apetite".localized(), log.appetite.displayName, AmberAccent)
                SymptomInfoChip("Energia".localized(), log.energyLevel.displayName, TealPrimary)
                SymptomInfoChip("Consumo de Água".localized(), log.waterIntake.displayName, Color(0xFF29B6F6))
                if (log.vomitingEpisodes > 0) {
                    SymptomInfoChip("Vómito".localized(), "${log.vomitingEpisodes}x", Color(0xFFE53935))
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = SymptomLog.fecalScoreDescription(log.fecalScore),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            if (!log.notes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text("📝 ${log.notes}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
            }
        }
    }
}

@Composable
private fun SymptomInfoChip(label: String, value: String, color: Color) {
    Surface(shape = RoundedCornerShape(10.dp), color = color.copy(alpha = 0.12f)) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 9.sp, color = color.copy(alpha = 0.85f), maxLines = 1)
            Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color, maxLines = 1)
        }
    }
}

@Composable
private fun AddSymptomDesktopDialog(petName: String, onDismiss: () -> Unit, onSave: (SymptomLog) -> Unit) {
    var appetiteLevel by remember { mutableStateOf(AppetiteLevel.NORMAL) }
    var energyLevel by remember { mutableStateOf(EnergyLevel.NORMAL) }
    var waterIntake by remember { mutableStateOf(WaterIntakeLevel.NORMAL) }
    var vomitingEpisodes by remember { mutableIntStateOf(0) }
    var fecalScore by remember { mutableFloatStateOf(2f) }
    var notes by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(20.dp), modifier = Modifier.width(520.dp), tonalElevation = 8.dp) {
            Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocalHospital, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("${"Registar Sintoma".localized()} — $petName", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text("Apetite".localized(), fontWeight = FontWeight.Medium, fontSize = 13.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    AppetiteLevel.values().forEach { level ->
                        FilterChip(
                            selected = appetiteLevel == level,
                            onClick = { appetiteLevel = level },
                            label = { Text(level.displayName, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TealPrimary.copy(alpha = 0.15f), selectedLabelColor = TealPrimary)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                Text("Energia".localized(), fontWeight = FontWeight.Medium, fontSize = 13.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    EnergyLevel.values().forEach { level ->
                        FilterChip(
                            selected = energyLevel == level,
                            onClick = { energyLevel = level },
                            label = { Text(level.displayName, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TealPrimary.copy(alpha = 0.15f), selectedLabelColor = TealPrimary)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                Text("Consumo de Água".localized(), fontWeight = FontWeight.Medium, fontSize = 13.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    WaterIntakeLevel.values().forEach { level ->
                        FilterChip(
                            selected = waterIntake == level,
                            onClick = { waterIntake = level },
                            label = { Text(level.displayName, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TealPrimary.copy(alpha = 0.15f), selectedLabelColor = TealPrimary)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                Text("Episódios de Vómito".localized(), fontWeight = FontWeight.Medium, fontSize = 13.sp)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { if (vomitingEpisodes > 0) vomitingEpisodes-- }) { Text("-") }
                    Text("$vomitingEpisodes", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    OutlinedButton(onClick = { vomitingEpisodes++ }) { Text("+") }
                }
                Spacer(modifier = Modifier.height(10.dp))

                Text("${"Score Fecal WSAVA".localized()}: ${fecalScore.roundToInt()}/7", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Slider(value = fecalScore, onValueChange = { fecalScore = it }, valueRange = 1f..7f, steps = 5)
                Text(
                    text = SymptomLog.fecalScoreDescription(fecalScore.roundToInt()),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notas Clínicas".localized()) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onDismiss) { Text("Cancelar".localized()) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onSave(
                                SymptomLog(
                                    id = UUID.randomUUID().toString(),
                                    date = Date(),
                                    fecalScore = fecalScore.roundToInt(),
                                    vomitingEpisodes = vomitingEpisodes,
                                    appetite = appetiteLevel,
                                    energyLevel = energyLevel,
                                    waterIntake = waterIntake,
                                    notes = notes.ifBlank { null }
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                    ) { Text("Guardar".localized(), fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}
