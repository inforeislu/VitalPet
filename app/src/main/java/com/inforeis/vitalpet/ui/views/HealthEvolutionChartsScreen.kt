package com.inforeis.vitalpet.ui.views

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inforeis.vitalpet.models.Pet
import com.inforeis.vitalpet.models.WeightLog
import com.inforeis.vitalpet.services.LanguageManager.localized
import com.inforeis.vitalpet.services.PetRepository
import com.inforeis.vitalpet.ui.theme.AccentAmber
import com.inforeis.vitalpet.ui.theme.AccentBlue
import com.inforeis.vitalpet.ui.theme.AccentRose
import com.inforeis.vitalpet.ui.theme.TealPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthEvolutionChartsScreen(
    initialPetId: String? = null,
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val pets by PetRepository.pets.collectAsState()
    var selectedPetId by remember(pets, initialPetId) {
        mutableStateOf(initialPetId ?: pets.firstOrNull()?.id ?: "")
    }
    val activePet = pets.find { it.id == selectedPetId } ?: pets.firstOrNull()
    var showAddWeightDialog by remember { mutableStateOf(false) }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    val weightLogs = remember(activePet?.weightLogs, activePet?.currentWeightKg) {
        if (activePet == null) emptyList()
        else {
            val logs = activePet.weightLogs.toMutableList()
            if (logs.none { it.weightKg == activePet.currentWeightKg }) {
                logs.add(WeightLog(weightKg = activePet.currentWeightKg, date = Date()))
            }
            logs.sortedBy { it.date }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Evolução de Peso & Saúde".localized(), fontWeight = FontWeight.Bold) },
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
                    onClick = { showAddWeightDialog = true },
                    containerColor = TealPrimary,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Registar Peso".localized())
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
                    text = "Registe primeiro um pet para acompanhar a sua evolução clínica.".localized(),
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Pet selector chips
            if (pets.size > 1) {
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(pets) { pet ->
                            val isSelected = pet.id == activePet?.id
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedPetId = pet.id },
                                label = { Text(pet.name, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
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
                // Summary Stats Cards
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        EvolutionMetricCard(
                            modifier = Modifier.weight(1f),
                            title = "Peso Atual".localized(),
                            value = "%.1f kg".format(activePet.currentWeightKg),
                            color = TealPrimary,
                            icon = Icons.Default.FitnessCenter
                        )
                        activePet.targetWeightKg?.let { target ->
                            val diff = activePet.currentWeightKg - target
                            EvolutionMetricCard(
                                modifier = Modifier.weight(1f),
                                title = "Peso Alvo".localized(),
                                value = "%.1f kg".format(target),
                                subtitle = if (diff > 0) "+%.1f kg".format(diff) else "%.1f kg".format(diff),
                                color = AccentAmber,
                                icon = if (diff > 0) Icons.Default.TrendingDown else Icons.Default.TrendingUp
                            )
                        }
                        EvolutionMetricCard(
                            modifier = Modifier.weight(1f),
                            title = "Score BCS".localized(),
                            value = "${activePet.bcs}/9",
                            subtitle = activePet.bcsCategory,
                            color = AccentBlue,
                            icon = Icons.Default.FitnessCenter
                        )
                    }
                }

                // Interactive Chart Card
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Curva de Evolução Ponderal".localized(),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                activePet.targetWeightKg?.let { target ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(AccentAmber, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Alvo: %.1f kg".format(target),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = AccentAmber
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            InteractiveWeightChart(
                                logs = weightLogs,
                                targetWeight = activePet.targetWeightKg,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                            )
                        }
                    }
                }

                // Weight Logs List
                item {
                    Text(
                        text = "Histórico de Pesagens".localized(),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                items(weightLogs.sortedByDescending { it.date }) { log ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = dateFormat.format(log.date),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                                if (!log.notes.isNullOrEmpty()) {
                                    Text(
                                        text = log.notes,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = TealPrimary.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "%.2f kg".format(log.weightKg),
                                    color = TealPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddWeightDialog && activePet != null) {
        var weightInput by remember { mutableStateOf("") }
        var notesInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddWeightDialog = false },
            title = { Text("Novo Registo de Peso".localized(), fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = { weightInput = it },
                        label = { Text("Peso (kg)".localized()) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = notesInput,
                        onValueChange = { notesInput = it },
                        label = { Text("Observações (opcional)".localized()) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsedWeight = weightInput.replace(',', '.').toDoubleOrNull()
                        if (parsedWeight != null && parsedWeight > 0) {
                            val newLog = WeightLog(weightKg = parsedWeight, date = Date(), notes = notesInput.ifEmpty { null })
                            activePet.weightLogs.add(newLog)
                            val updatedPet = activePet.copy(currentWeightKg = parsedWeight)
                            PetRepository.updatePet(updatedPet)
                            com.inforeis.vitalpet.services.ClinicalNotificationManager.schedulePeriodicWeighInReminder(context, activePet.name, 14)
                            showAddWeightDialog = false
                        }
                    }
                ) {
                    Text("Guardar".localized())
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddWeightDialog = false }) {
                    Text("Cancelar".localized())
                }
            }
        )
    }
}

@Composable
fun EvolutionMetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String? = null,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = color)
            Text(title, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            if (subtitle != null) {
                Text(subtitle, fontSize = 9.sp, fontWeight = FontWeight.Medium, color = color.copy(alpha = 0.8f))
            }
        }
    }
}

@Composable
fun InteractiveWeightChart(
    logs: List<WeightLog>,
    targetWeight: Double?,
    modifier: Modifier = Modifier
) {
    if (logs.isEmpty()) return

    val weights = logs.map { it.weightKg }
    val allValues = weights + listOfNotNull(targetWeight)
    val minWeight = (allValues.minOrNull() ?: 10.0) * 0.95
    val maxWeight = (allValues.maxOrNull() ?: 20.0) * 1.05
    val weightRange = if (maxWeight - minWeight > 0.01) maxWeight - minWeight else 1.0

    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(logs) {
                    detectDragGestures(
                        onDrag = { change, _ ->
                            val width = size.width
                            val stepX = width / maxOf(1, logs.size - 1)
                            val touchX = change.position.x
                            val rawIndex = (touchX / stepX).roundToInt()
                            selectedIndex = rawIndex.coerceIn(0, logs.size - 1)
                        },
                        onDragEnd = { selectedIndex = null }
                    )
                }
                .pointerInput(logs) {
                    detectTapGestures(
                        onTap = { offset ->
                            val width = size.width
                            val stepX = width / maxOf(1, logs.size - 1)
                            val rawIndex = (offset.x / stepX).roundToInt()
                            selectedIndex = rawIndex.coerceIn(0, logs.size - 1)
                        }
                    )
                }
        ) {
            val width = size.width
            val height = size.height
            val paddingBottom = 20f
            val chartHeight = height - paddingBottom

            // Draw horizontal grid lines
            val gridCount = 4
            for (i in 0..gridCount) {
                val y = chartHeight * (i.toFloat() / gridCount)
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.25f),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1f
                )
            }

            // Draw target weight dashed line if present
            targetWeight?.let { target ->
                val targetY = (chartHeight - ((target - minWeight) / weightRange * chartHeight)).toFloat()
                drawLine(
                    color = AccentAmber.copy(alpha = 0.8f),
                    start = Offset(0f, targetY),
                    end = Offset(width, targetY),
                    strokeWidth = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )
            }

            if (logs.size == 1) {
                val y = (chartHeight - ((logs[0].weightKg - minWeight) / weightRange * chartHeight)).toFloat()
                drawCircle(color = TealPrimary, radius = 6.dp.toPx(), center = Offset(width / 2f, y))
                return@Canvas
            }

            // Calculate points
            val stepX = width / (logs.size - 1)
            val points = logs.mapIndexed { i, log ->
                val x = i * stepX
                val y = (chartHeight - ((log.weightKg - minWeight) / weightRange * chartHeight)).toFloat()
                Offset(x, y)
            }

            // Fill area gradient
            val fillPath = Path().apply {
                moveTo(points.first().x, chartHeight)
                points.forEach { lineTo(it.x, it.y) }
                lineTo(points.last().x, chartHeight)
                close()
            }
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(TealPrimary.copy(alpha = 0.35f), TealPrimary.copy(alpha = 0.02f)),
                    startY = 0f,
                    endY = chartHeight
                )
            )

            // Line path
            val strokePath = Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 1 until points.size) {
                    lineTo(points[i].x, points[i].y)
                }
            }
            drawPath(
                path = strokePath,
                color = TealPrimary,
                style = Stroke(width = 3.dp.toPx())
            )

            // Draw point dots
            points.forEachIndexed { index, point ->
                val isSelected = index == selectedIndex
                drawCircle(
                    color = if (isSelected) Color.White else TealPrimary,
                    radius = if (isSelected) 7.dp.toPx() else 4.dp.toPx(),
                    center = point
                )
                if (isSelected) {
                    drawCircle(
                        color = TealPrimary,
                        radius = 4.dp.toPx(),
                        center = point
                    )
                }
            }
        }

        // Active Tooltip overlay
        selectedIndex?.let { index ->
            val log = logs.getOrNull(index)
            if (log != null) {
                val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF1E293B),
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 4.dp)
                ) {
                    Text(
                        text = "${dateFormat.format(log.date)} • %.2f kg".format(log.weightKg),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
