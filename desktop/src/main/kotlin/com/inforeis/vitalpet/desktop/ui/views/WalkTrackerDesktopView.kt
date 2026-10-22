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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.inforeis.vitalpet.desktop.ui.theme.AmberAccent
import com.inforeis.vitalpet.desktop.ui.theme.TealPrimary
import com.inforeis.vitalpet.models.WalkIntensity
import com.inforeis.vitalpet.models.WalkSession
import com.inforeis.vitalpet.services.DesktopPetRepository
import com.inforeis.vitalpet.services.LanguageManager.localized
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

@Composable
fun WalkTrackerDesktopView() {
    val pets by DesktopPetRepository.pets.collectAsState()
    var selectedPetId by remember(pets) { mutableStateOf(pets.firstOrNull()?.id) }
    val selectedPet = pets.find { it.id == selectedPetId } ?: pets.firstOrNull()

    var isTracking by remember { mutableStateOf(false) }
    var secondsElapsed by remember { mutableIntStateOf(0) }
    var intensity by remember { mutableStateOf(WalkIntensity.MODERATE) }
    var showIntensityMenu by remember { mutableStateOf(false) }

    LaunchedEffect(isTracking) {
        if (isTracking) {
            while (isTracking) {
                delay(1000)
                secondsElapsed++
            }
        }
    }

    val sevenDaysAgo = remember { Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -7) }.time }
    val weekSessions = selectedPet?.walkSessions?.filter { it.date >= sevenDaysAgo } ?: emptyList()
    val totalMinutesWeek = weekSessions.sumOf { it.durationSeconds } / 60
    val totalMetersWeek = weekSessions.sumOf { it.distanceMeters ?: 0.0 }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    Row(modifier = Modifier.fillMaxSize()) {
        // Painel Esquerdo: Controlo do Passeio
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
                    Icon(Icons.Default.DirectionsWalk, null, tint = TealPrimary, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Rastreador de Passeios".localized(), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TealPrimary)
                }
                Divider()

                if (pets.isNotEmpty()) {
                    Text("Seleccionar Pet".localized(), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    pets.forEach { pet ->
                        val isSelected = pet.id == selectedPet?.id
                        FilterChip(
                            selected = isSelected,
                            onClick = { if (!isTracking) selectedPetId = pet.id },
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

                // Intensidade
                Text("Intensidade".localized(), fontWeight = FontWeight.Medium, fontSize = 13.sp)
                Box {
                    OutlinedButton(onClick = { showIntensityMenu = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(intensity.displayName)
                    }
                    DropdownMenu(expanded = showIntensityMenu, onDismissRequest = { showIntensityMenu = false }) {
                        WalkIntensity.values().forEach { wi ->
                            DropdownMenuItem(
                                text = { Text(wi.displayName) },
                                onClick = { intensity = wi; showIntensityMenu = false }
                            )
                        }
                    }
                }

                // Cronómetro
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isTracking) TealPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            formatWalkDuration(secondsElapsed),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isTracking) TealPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Text(
                            if (isTracking) "Em curso…".localized() else "Pronto para iniciar".localized(),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }

                // Botões
                if (!isTracking) {
                    Button(
                        onClick = { if (selectedPet != null) { secondsElapsed = 0; isTracking = true } },
                        enabled = selectedPet != null,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Iniciar Passeio".localized(), fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = {
                            isTracking = false
                            if (selectedPet != null && secondsElapsed >= 30) {
                                val session = WalkSession(
                                    id = UUID.randomUUID().toString(),
                                    date = Date(System.currentTimeMillis() - secondsElapsed * 1000L),
                                    durationSeconds = secondsElapsed,
                                    intensity = intensity,
                                    distanceMeters = null,
                                    caloriesBurned = WalkSession.calculateCalories(selectedPet.currentWeightKg, secondsElapsed, intensity)
                                )
                                val updatedSessions = selectedPet.walkSessions.toMutableList().also { it.add(session) }
                                val updated = selectedPet.copy(walkSessions = updatedSessions)
                                DesktopPetRepository.updatePet(updated)
                                secondsElapsed = 0
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                    ) {
                        Icon(Icons.Default.Stop, null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Terminar e Guardar".localized(), fontWeight = FontWeight.Bold)
                    }
                }

                Divider()
                Text("Esta Semana".localized(), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                WalkStatCard("Sessões".localized(), "${weekSessions.size}", TealPrimary)
                WalkStatCard("Tempo Total".localized(), "${totalMinutesWeek} min", AmberAccent)
                if (totalMetersWeek > 0) {
                    val km = totalMetersWeek / 1000.0
                    WalkStatCard("Distância".localized(), "${String.format("%.1f", km)} km", Color(0xFF9C27B0))
                }
            }
        }

        // Painel Direito: Histórico
        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
            when {
                selectedPet == null -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Pets, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Seleccione um pet.".localized(), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                    }
                }
                selectedPet.walkSessions.isEmpty() -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.DirectionsWalk, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Sem passeios para ${selectedPet.name}.".localized(), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                    }
                }
                else -> {
                    val allSessions = selectedPet.walkSessions.sortedByDescending { it.date }
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Text("${selectedPet.name} — ${"Histórico de Passeios".localized()}", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TealPrimary)
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        items(allSessions) { session ->
                            WalkSessionCard(session = session, dateFormat = dateFormat, onDelete = {
                                val updatedSessions = selectedPet.walkSessions.toMutableList().also { it.removeAll { s -> s.id == session.id } }
                                val updated = selectedPet.copy(walkSessions = updatedSessions)
                                DesktopPetRepository.updatePet(updated)
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WalkStatCard(label: String, value: String, color: Color) {
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
private fun WalkSessionCard(session: WalkSession, dateFormat: SimpleDateFormat, onDelete: () -> Unit) {
    val intensityColor = when (session.intensity) {
        WalkIntensity.GENTLE -> Color(0xFF66BB6A)
        WalkIntensity.MODERATE -> AmberAccent
        WalkIntensity.BRISK -> Color(0xFFE53935)
    }
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(intensityColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.DirectionsWalk, null, tint = intensityColor, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(dateFormat.format(session.date), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("⏱ ${session.formattedDuration}", fontSize = 12.sp, color = TealPrimary)
                        session.distanceMeters?.let {
                            Text("📍 ${String.format("%.0f", it)} m", fontSize = 12.sp, color = AmberAccent)
                        }
                        if (session.caloriesBurned > 0) {
                            Text("🔥 ${String.format("%.0f", session.caloriesBurned)} kcal", fontSize = 12.sp, color = Color(0xFFE53935))
                        }
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(20.dp), color = intensityColor.copy(alpha = 0.12f)) {
                    Text(
                        session.intensity.displayName,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontSize = 11.sp, color = intensityColor, fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onDelete) { Text("✕", color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }
            }
        }
    }
}

private fun formatWalkDuration(seconds: Int): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) String.format("%d:%02d:%02d", h, m, s) else String.format("%02d:%02d", m, s)
}
