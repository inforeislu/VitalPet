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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inforeis.vitalpet.desktop.ui.theme.AmberAccent
import com.inforeis.vitalpet.desktop.ui.theme.TealPrimary
import com.inforeis.vitalpet.models.AppetiteLevel
import com.inforeis.vitalpet.models.EnergyLevel
import com.inforeis.vitalpet.models.Pet
import com.inforeis.vitalpet.services.DesktopPetRepository
import com.inforeis.vitalpet.services.LanguageManager.localized
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun DashboardDesktopView() {
    val pets by DesktopPetRepository.pets.collectAsState()
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    val now = Date()
    val sevenDaysAgo = remember { Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -7) }.time }
    val thirtyDaysAhead = remember { Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 30) }.time }

    // Alertas globais
    val alerts = remember(pets) { buildAlerts(pets, now, sevenDaysAgo) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Cabeçalho
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Dashboard, null, tint = TealPrimary, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Painel VitalPet".localized(), fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = TealPrimary)
                    Text("Visão Clínica Geral".localized(), fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(dateFormat.format(now), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
            }
        }

        // Alertas (se houver)
        if (alerts.isNotEmpty()) {
            item {
                Text("⚠️ ${"Alertas Clínicos".localized()}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFFC62828))
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    alerts.forEach { alert ->
                        AlertBanner(alert)
                    }
                }
            }
        }

        // Estatísticas gerais
        item {
            Text("📊 ${"Estatísticas Gerais".localized()}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                val totalSymptoms = pets.sumOf { it.symptomLogs.size }
                val totalWalks = pets.sumOf { it.walkSessions.size }
                val totalVaccines = pets.sumOf { it.vaccineLogs.size }
                val totalVisits = pets.sumOf { it.medicalVisits.size }

                DashStatCard("Pets".localized(), "${pets.size}", Icons.Default.Pets, TealPrimary, Modifier.weight(1f))
                DashStatCard("Sintomas".localized(), "$totalSymptoms", Icons.Default.MonitorHeart, AmberAccent, Modifier.weight(1f))
                DashStatCard("Passeios".localized(), "$totalWalks", Icons.Default.DirectionsWalk, Color(0xFF66BB6A), Modifier.weight(1f))
                DashStatCard("Vacinas".localized(), "$totalVaccines", Icons.Default.Vaccines, Color(0xFF9C27B0), Modifier.weight(1f))
                DashStatCard("Consultas".localized(), "$totalVisits", Icons.Default.LocalHospital, Color(0xFF1976D2), Modifier.weight(1f))
            }
        }

        // Cards por Pet
        if (pets.isNotEmpty()) {
            item { Text("🐾 ${"Resumo por Pet".localized()}", fontWeight = FontWeight.Bold, fontSize = 15.sp) }
            items(pets) { pet ->
                PetSummaryCard(pet = pet, dateFormat = dateFormat, sevenDaysAgo = sevenDaysAgo)
            }
        } else {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Pets, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Nenhum pet registado ainda.".localized(), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                        Text("Vá a 'Meus Pets' para adicionar o primeiro.".localized(), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                    }
                }
            }
        }

        // Próximos eventos
        val upcomingVaccines = pets.flatMap { pet ->
            pet.vaccineLogs.mapNotNull { vac ->
                vac.nextDueDate?.let { due ->
                    if (due >= now) Triple(pet.name, vac.vaccineName, due) else null
                }
            }
        }.sortedBy { it.third }.take(5)

        if (upcomingVaccines.isNotEmpty()) {
            item { Text("📅 ${"Próximas Vacinas / Rappels".localized()}", fontWeight = FontWeight.Bold, fontSize = 15.sp) }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    upcomingVaccines.forEach { (petName, vacName, due) ->
                        UpcomingEventCard(petName = petName, eventName = vacName, date = due, dateFormat = dateFormat, color = Color(0xFF9C27B0))
                    }
                }
            }
        }
    }
}

data class DashAlert(val petName: String, val message: String, val level: AlertLevel)
enum class AlertLevel { HIGH, MEDIUM, INFO }

private fun buildAlerts(pets: List<Pet>, now: Date, sevenDaysAgo: Date): List<DashAlert> {
    val alerts = mutableListOf<DashAlert>()
    val threeDaysAgo = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -3) }.time

    pets.forEach { pet ->
        val recentLogs = pet.symptomLogs.filter { it.date >= sevenDaysAgo }
        val vomiting = recentLogs.sumOf { it.vomitingEpisodes }
        if (vomiting >= 3) alerts.add(DashAlert(pet.name, "Vómitos recorrentes (${vomiting}x) nos últimos 7 dias.".localized(), AlertLevel.HIGH))

        val diarrheaAlert = pet.symptomLogs.filter { it.date >= threeDaysAgo }.any { it.fecalScore >= 6 }
        if (diarrheaAlert) alerts.add(DashAlert(pet.name, "Score fecal elevado nos últimos 3 dias.".localized(), AlertLevel.HIGH))

        val highConcern = recentLogs.any { it.energyLevel == EnergyLevel.LETHARGIC || it.appetite == AppetiteLevel.ANOREXIC }
        if (highConcern) alerts.add(DashAlert(pet.name, "Nível de dor ≥7 registado recentemente.".localized(), AlertLevel.MEDIUM))

        val overdueVaccines = pet.vaccineLogs.filter { vac ->
            vac.nextDueDate != null && vac.nextDueDate!! < now
        }
        if (overdueVaccines.isNotEmpty()) {
            val names = overdueVaccines.joinToString(", ") { it.vaccineName }
            alerts.add(DashAlert(pet.name, "${overdueVaccines.size} ${"Vacina".localized()} (${"Em atraso".localized()}): $names", AlertLevel.MEDIUM))
        }
    }
    return alerts
}

@Composable
private fun AlertBanner(alert: DashAlert) {
    val (bgColor, iconColor) = when (alert.level) {
        AlertLevel.HIGH -> Color(0xFFFFCDD2) to Color(0xFFC62828)
        AlertLevel.MEDIUM -> Color(0xFFFFF9C4) to Color(0xFFF57F17)
        AlertLevel.INFO -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
    }
    Card(shape = RoundedCornerShape(10.dp), colors = CardDefaults.cardColors(containerColor = bgColor)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Warning, null, tint = iconColor, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(alert.petName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = iconColor)
                Text(alert.message, fontSize = 12.sp, color = iconColor.copy(alpha = 0.85f))
            }
        }
    }
}

@Composable
private fun DashStatCard(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(14.dp),
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = color)
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
    }
}

@Composable
private fun PetSummaryCard(pet: Pet, dateFormat: SimpleDateFormat, sevenDaysAgo: Date) {
    val recentLogs = pet.symptomLogs.filter { it.date >= sevenDaysAgo }
    val lastWeight = pet.weightLogs.sortedByDescending { it.date }.firstOrNull()
    val lastWalk = pet.walkSessions.sortedByDescending { it.date }.firstOrNull()
    val lastVisit = pet.medicalVisits.sortedByDescending { it.date }.firstOrNull()

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(TealPrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(pet.name.take(1).uppercase(), fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = TealPrimary)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(pet.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("${pet.species.name.lowercase().replaceFirstChar { it.uppercase() }} • ${pet.breed ?: "—"}".localized(), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.weight(1f))
                Surface(shape = RoundedCornerShape(20.dp), color = TealPrimary.copy(alpha = 0.12f)) {
                    Text(
                        "${String.format("%.2f", pet.currentWeightKg)} kg",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TealPrimary
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Divider()
            Spacer(modifier = Modifier.height(10.dp))

            // Quick Stats Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PetMiniStat("Sintomas (7d)".localized(), "${recentLogs.size}", AmberAccent, Modifier.weight(1f))
                PetMiniStat("Passeios".localized(), "${pet.walkSessions.size}", Color(0xFF66BB6A), Modifier.weight(1f))
                PetMiniStat("Vacinas".localized(), "${pet.vaccineLogs.size}", Color(0xFF9C27B0), Modifier.weight(1f))
                PetMiniStat("Consultas".localized(), "${pet.medicalVisits.size}", Color(0xFF1976D2), Modifier.weight(1f))
            }

            // Last events
            if (lastVisit != null || lastWalk != null || lastWeight != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    lastWeight?.let {
                        Text("⚖️ ${"Peso".localized()}: ${String.format("%.2f", it.weightKg)} kg (${dateFormat.format(it.date)})", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f))
                    }
                    lastWalk?.let {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("🦮 ${"Passeio".localized()}: ${dateFormat.format(it.date)}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f))
                    }
                }
                lastVisit?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("🏥 ${"Última consulta".localized()}: ${it.reason} (${dateFormat.format(it.date)})", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f))
                }
            }
        }
    }
}

@Composable
private fun PetMiniStat(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = color)
            Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
    }
}

@Composable
private fun UpcomingEventCard(petName: String, eventName: String, date: Date, dateFormat: SimpleDateFormat, color: Color) {
    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarMonth, null, tint = color, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(petName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = color)
                    Text(eventName, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                }
            }
            Surface(shape = RoundedCornerShape(20.dp), color = color.copy(alpha = 0.15f)) {
                Text(dateFormat.format(date), modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
            }
        }
    }
}
