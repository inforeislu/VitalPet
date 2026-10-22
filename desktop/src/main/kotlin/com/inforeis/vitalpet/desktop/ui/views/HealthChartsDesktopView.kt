package com.inforeis.vitalpet.desktop.ui.views

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.inforeis.vitalpet.desktop.ui.theme.AmberAccent
import com.inforeis.vitalpet.desktop.ui.theme.TealPrimary
import com.inforeis.vitalpet.models.WeightLog
import com.inforeis.vitalpet.services.DesktopPetRepository
import com.inforeis.vitalpet.services.LanguageManager.localized
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Composable
fun HealthChartsDesktopView() {
    val pets by DesktopPetRepository.pets.collectAsState()
    var selectedPetId by remember(pets) { mutableStateOf(pets.firstOrNull()?.id ?: "") }
    val activePet = pets.find { it.id == selectedPetId } ?: pets.firstOrNull()
    var selectedTab by remember { mutableStateOf(0) }
    var showAddWeightDialog by remember { mutableStateOf(false) }
    var tooltipPoint by remember { mutableStateOf<Pair<Float, Float>?>(null) }

    val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())

    Row(modifier = Modifier.fillMaxSize()) {
        // Painel Esquerdo
        Surface(
            modifier = Modifier.width(260.dp).fillMaxHeight(),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Evolução de Saúde".localized(), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TealPrimary)
                }
                Divider()
                if (pets.isNotEmpty()) {
                    Text("Seleccionar Pet".localized(), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    pets.forEach { pet ->
                        val isSelected = pet.id == activePet?.id
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedPetId = pet.id },
                            label = { Text(pet.name, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            leadingIcon = if (isSelected) { { Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp)) } } else null,
                            modifier = Modifier.fillMaxWidth(),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TealPrimary.copy(alpha = 0.15f),
                                selectedLabelColor = TealPrimary
                            )
                        )
                    }
                    Divider()
                }
                // Resumo estatístico
                if (activePet != null) {
                    val weightLogs = activePet.weightLogs.sortedBy { it.date }
                    if (weightLogs.size >= 2) {
                        val first = weightLogs.first().weightKg
                        val last = weightLogs.last().weightKg
                        val delta = last - first
                        val trend = when {
                            delta > 0.1 -> "📈 +${String.format("%.2f", delta)} kg"
                            delta < -0.1 -> "📉 -${String.format("%.2f", -delta)} kg"
                            else -> "➡️ ${"Estável".localized()}"
                        }
                        HealthStatCard("Peso Atual".localized(), "${String.format("%.2f", last)} kg", TealPrimary)
                        HealthStatCard("Tendência".localized(), trend, if (delta > 0.1) AmberAccent else TealPrimary)
                        HealthStatCard("Total Registos".localized(), "${weightLogs.size}", Color(0xFF9C27B0))
                    } else if (weightLogs.size == 1) {
                        HealthStatCard("Peso Atual".localized(), "${String.format("%.2f", weightLogs.first().weightKg)} kg", TealPrimary)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { showAddWeightDialog = true },
                    enabled = activePet != null,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Registar Peso".localized(), fontWeight = FontWeight.Bold)
                }
            }
        }

        // Painel Direito: Gráficos
        Column(modifier = Modifier.weight(1f).fillMaxHeight().padding(16.dp)) {
            if (activePet == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Pets, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Seleccione um pet para ver os gráficos.".localized(), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                    }
                }
            } else {
                Text(
                    "${activePet.name} — ${"Gráficos de Evolução".localized()}",
                    fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TealPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))

                val tabs = listOf("Peso & IMC".localized(), "Histórico de Registos".localized())
                TabRow(selectedTabIndex = selectedTab) {
                    tabs.forEachIndexed { i, title ->
                        Tab(selected = selectedTab == i, onClick = { selectedTab = i }, text = { Text(title, fontSize = 13.sp) })
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                when (selectedTab) {
                    0 -> {
                        val weightLogs = activePet.weightLogs.sortedBy { it.date }
                        if (weightLogs.size < 2) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Mínimo 2 registos de peso para visualizar gráfico.".localized(), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(onClick = { showAddWeightDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)) {
                                        Text("Registar Peso".localized())
                                    }
                                }
                            }
                        } else {
                            WeightLineChart(logs = weightLogs, dateFormat = dateFormat)
                        }
                    }
                    1 -> {
                        val weightLogs = activePet.weightLogs.sortedByDescending { it.date }
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(weightLogs) { log ->
                                WeightLogCard(log = log, dateFormat = dateFormat, onDelete = {
                                    val updated = activePet.copy(weightLogs = activePet.weightLogs.filterNot { it.id == log.id }.toMutableList())
                                    DesktopPetRepository.updatePet(updated)
                                })
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddWeightDialog && activePet != null) {
        AddWeightDesktopDialog(
            petName = activePet.name,
            onDismiss = { showAddWeightDialog = false },
            onSave = { log ->
                val updated = activePet.copy(
                    currentWeightKg = log.weightKg,
                    weightLogs = (activePet.weightLogs + log).toMutableList()
                )
                DesktopPetRepository.updatePet(updated)
                showAddWeightDialog = false
            }
        )
    }
}

@Composable
private fun HealthStatCard(label: String, value: String, color: Color) {
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
            Text(value, fontWeight = FontWeight.Bold, color = color, fontSize = 14.sp)
        }
    }
}

@Composable
private fun WeightLineChart(logs: List<WeightLog>, dateFormat: SimpleDateFormat) {
    val primaryColor = TealPrimary
    val gridColor = Color.Gray.copy(alpha = 0.2f)
    val minWeight = (logs.minOf { it.weightKg } - 0.5).coerceAtLeast(0.0)
    val maxWeight = logs.maxOf { it.weightKg } + 0.5

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().height(320.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Curva de Peso".localized(), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Canvas(modifier = Modifier.fillMaxWidth().weight(1f)) {
                val w = size.width
                val h = size.height
                val padL = 48f; val padB = 36f; val padT = 16f; val padR = 16f
                val chartW = w - padL - padR
                val chartH = h - padT - padB

                // Grid linhas
                val ySteps = 5
                for (i in 0..ySteps) {
                    val y = padT + chartH * (1f - i.toFloat() / ySteps)
                    drawLine(gridColor, Offset(padL, y), Offset(w - padR, y), strokeWidth = 1f)
                }

                // Path
                val path = Path()
                val fillPath = Path()
                logs.forEachIndexed { i, log ->
                    val x = padL + chartW * i.toFloat() / (logs.size - 1)
                    val y = padT + chartH * (1f - ((log.weightKg - minWeight) / (maxWeight - minWeight)).toFloat())
                    if (i == 0) { path.moveTo(x, y); fillPath.moveTo(x, y) } else { path.lineTo(x, y); fillPath.lineTo(x, y) }
                }
                // Fill gradient
                fillPath.lineTo(padL + chartW, padT + chartH)
                fillPath.lineTo(padL, padT + chartH)
                fillPath.close()
                drawPath(fillPath, Brush.verticalGradient(listOf(primaryColor.copy(alpha = 0.3f), primaryColor.copy(alpha = 0.0f))))
                drawPath(path, primaryColor, style = Stroke(width = 3f))

                // Points
                logs.forEachIndexed { i, log ->
                    val x = padL + chartW * i.toFloat() / (logs.size - 1)
                    val y = padT + chartH * (1f - ((log.weightKg - minWeight) / (maxWeight - minWeight)).toFloat())
                    drawCircle(Color.White, radius = 6f, center = Offset(x, y))
                    drawCircle(primaryColor, radius = 4f, center = Offset(x, y))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            // Labels X
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                val step = if (logs.size <= 7) 1 else logs.size / 6
                logs.filterIndexed { i, _ -> i % step == 0 || i == logs.size - 1 }.forEach { log ->
                    Text(dateFormat.format(log.date), fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
            }
        }
    }
}

@Composable
private fun WeightLogCard(log: WeightLog, dateFormat: SimpleDateFormat, onDelete: () -> Unit) {
    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("${String.format("%.2f", log.weightKg)} kg", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TealPrimary)
                Text(dateFormat.format(log.date), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (!log.notes.isNullOrBlank()) {
                Text(log.notes, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), modifier = Modifier.weight(1f).padding(horizontal = 12.dp))
            }
            TextButton(onClick = onDelete) { Text("Apagar".localized(), color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }
        }
    }
}

@Composable
private fun AddWeightDesktopDialog(petName: String, onDismiss: () -> Unit, onSave: (WeightLog) -> Unit) {
    var weight by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(20.dp), modifier = Modifier.width(400.dp), tonalElevation = 8.dp) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FitnessCenter, null, tint = TealPrimary, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("${"Novo Registo de Peso".localized()} — $petName", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = weight, onValueChange = { weight = it; errorMsg = "" },
                    label = { Text("Peso (kg)".localized()) },
                    isError = errorMsg.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                )
                if (errorMsg.isNotBlank()) Text(errorMsg, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = note, onValueChange = { note = it },
                    label = { Text("Nota (opcional)".localized()) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onDismiss) { Text("Cancelar".localized()) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val w = weight.replace(",", ".").toDoubleOrNull()
                            if (w == null || w <= 0) { errorMsg = "Peso inválido.".localized(); return@Button }
                            onSave(WeightLog(id = UUID.randomUUID().toString(), date = Date(), weightKg = w, notes = note.ifBlank { null }))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                    ) { Text("Guardar".localized(), fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}
