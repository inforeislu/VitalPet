package com.inforeis.vitalpet.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inforeis.vitalpet.models.AppetiteLevel
import com.inforeis.vitalpet.models.EnergyLevel
import com.inforeis.vitalpet.models.Pet
import com.inforeis.vitalpet.models.SymptomLog
import com.inforeis.vitalpet.services.LanguageManager.localized
import com.inforeis.vitalpet.services.PetRepository
import com.inforeis.vitalpet.ui.theme.AccentAmber
import com.inforeis.vitalpet.ui.theme.AccentBlue
import com.inforeis.vitalpet.ui.theme.AccentRose
import com.inforeis.vitalpet.ui.theme.TealPrimary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymptomJournalScreen(
    initialPetId: String? = null,
    onBack: () -> Unit
) {
    val pets by PetRepository.pets.collectAsState()
    var selectedPetId by remember(pets, initialPetId) {
        mutableStateOf(initialPetId ?: pets.firstOrNull()?.id ?: "")
    }
    val activePet = pets.find { it.id == selectedPetId } ?: pets.firstOrNull()
    var showAddDialog by remember { mutableStateOf(false) }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    // 7-day analytics
    val now = Date()
    val sevenDaysAgo = remember {
        Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -7) }.time
    }
    val threeDaysAgo = remember {
        Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -3) }.time
    }

    val recentLogs = activePet?.symptomLogs?.filter { it.date >= sevenDaysAgo } ?: emptyList()
    val recentVomitingCount = recentLogs.sumOf { it.vomitingEpisodes }
    val recentDiarrheaAlert = activePet?.symptomLogs?.filter { it.date >= threeDaysAgo }?.any { (it.fecalScore ?: 2) >= 6 } ?: false

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Diário Clínico de Sintomas".localized(), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar".localized())
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            if (activePet != null) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = TealPrimary,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Registar Sintoma".localized())
                }
            }
        }
    ) { padding ->
        if (pets.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Registe primeiro um pet para aceder ao diário de sintomas.".localized(),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Pet Selector Chips
            if (pets.size > 1) {
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(pets) { pet ->
                            val isSelected = pet.id == activePet?.id
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedPetId = pet.id },
                                label = { Text(pet.name) },
                                leadingIcon = if (isSelected) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = TealPrimary.copy(alpha = 0.15f),
                                    selectedLabelColor = TealPrimary
                                )
                            )
                        }
                    }
                }
            }

            if (activePet != null) {
                // Alert Banner if recent diarrhea or vomiting
                if (recentDiarrheaAlert || recentVomitingCount > 0) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = AccentRose.copy(alpha = 0.1f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = AccentRose, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Alerta Digestivo dos Últimos 7 Dias".localized(),
                                        fontWeight = FontWeight.Bold,
                                        color = AccentRose,
                                        fontSize = 14.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                if (recentDiarrheaAlert) {
                                    Text(
                                        text = "• Fezes muito moles ou líquidas (Score 6-7) nos últimos 3 dias.".localized(),
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                if (recentVomitingCount > 0) {
                                    Text(
                                        text = "• $recentVomitingCount " + "episódio(s) de vómito registados recentemente.".localized(),
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                // Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Histórico de Registos".localized(),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${activePet.symptomLogs.size} " + "registos".localized(),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                if (activePet.symptomLogs.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Nenhum sintoma digestivo ou clínico registado.\nToque no botão + para adicionar um novo registo.".localized(),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                } else {
                    items(activePet.symptomLogs.sortedByDescending { it.date }) { log ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = dateFormat.format(log.date),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    log.fecalScore?.let { score ->
                                        val scoreColor = when (score) {
                                            in 1..3 -> TealPrimary
                                            in 4..5 -> AccentAmber
                                            else -> AccentRose
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = scoreColor.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = "Score Fecal $score/7".localized(),
                                                color = scoreColor,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (log.vomitingEpisodes > 0) {
                                        Surface(shape = RoundedCornerShape(6.dp), color = AccentRose.copy(alpha = 0.12f)) {
                                            Text(
                                                text = "🤮 ${log.vomitingEpisodes} vómito(s)".localized(),
                                                fontSize = 11.sp,
                                                color = AccentRose,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                    Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)) {
                                        Text(
                                            text = "Apetite: ${log.appetite.displayName}".localized(),
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                    Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)) {
                                        Text(
                                            text = "Energia: ${log.energyLevel.displayName}".localized(),
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                if (!log.notes.isNullOrEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = log.notes,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog && activePet != null) {
        var fecalScoreFloat by remember { mutableFloatStateOf(2f) }
        var vomitingEpisodes by remember { mutableIntStateOf(0) }
        var selectedAppetite by remember { mutableStateOf(AppetiteLevel.NORMAL) }
        var selectedEnergy by remember { mutableStateOf(EnergyLevel.NORMAL) }
        var coughOrSneezing by remember { mutableStateOf(false) }
        var notesInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Novo Registo de Sintomas".localized(), fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Fecal Score Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Escala Fecal (Purina/Bristol)".localized(), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("${fecalScoreFloat.roundToInt()}/7", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TealPrimary)
                        }
                        Slider(
                            value = fecalScoreFloat,
                            onValueChange = { fecalScoreFloat = it },
                            valueRange = 1f..7f,
                            steps = 5
                        )
                        val fecalDescription = when (fecalScoreFloat.roundToInt()) {
                            1 -> "Muito duro e seco".localized()
                            2 -> "Ideal, firme e moldado".localized()
                            3 -> "Húmido, mas retém forma".localized()
                            4 -> "Muito mole com forma perdida".localized()
                            5 -> "Viscoso e pastoso".localized()
                            6 -> "Textura líquida com pedaços".localized()
                            else -> "Diarreia totalmente líquida".localized()
                        }
                        Text(fecalDescription, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }

                    // Vomiting Counter
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Episódios de Vómito".localized(), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { if (vomitingEpisodes > 0) vomitingEpisodes-- }) {
                                Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                            Text("$vomitingEpisodes", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            IconButton(onClick = { vomitingEpisodes++ }) {
                                Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Notes
                    OutlinedTextField(
                        value = notesInput,
                        onValueChange = { notesInput = it },
                        label = { Text("Observações Clínicas (opcional)".localized()) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newLog = SymptomLog(
                            date = Date(),
                            fecalScore = fecalScoreFloat.roundToInt(),
                            vomitingEpisodes = vomitingEpisodes,
                            appetite = selectedAppetite,
                            energyLevel = selectedEnergy,
                            notes = notesInput.ifEmpty { null }
                        )
                        activePet.symptomLogs.add(newLog)
                        PetRepository.updatePet(activePet)
                        showAddDialog = false
                    }
                ) {
                    Text("Guardar".localized())
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancelar".localized())
                }
            }
        )
    }
}
