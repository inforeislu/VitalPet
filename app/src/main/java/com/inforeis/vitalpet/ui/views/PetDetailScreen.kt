package com.inforeis.vitalpet.ui.views

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inforeis.vitalpet.models.Pet
import com.inforeis.vitalpet.models.WeightLog
import com.inforeis.vitalpet.services.LanguageManager.localized
import com.inforeis.vitalpet.services.PetRepository
import com.inforeis.vitalpet.ui.components.PetAvatar
import com.inforeis.vitalpet.ui.theme.AccentAmber
import com.inforeis.vitalpet.ui.theme.AccentBlue
import com.inforeis.vitalpet.ui.theme.AccentRose
import com.inforeis.vitalpet.ui.theme.TealPrimary
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.PictureAsPdf
import com.inforeis.vitalpet.services.PDFReportGenerator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDetailScreen(
    petId: String,
    onBack: () -> Unit,
    onOpenDigitalId: (Pet) -> Unit,
    onOpenHealthCharts: (String) -> Unit = {},
    onOpenEvolutionPhotos: (String) -> Unit = {},
    onOpenSymptomJournal: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val pets by PetRepository.pets.collectAsState()
    val pet = pets.find { it.id == petId }

    if (pet == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Pet não encontrado".localized())
        }
        return
    }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(pet.name, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar".localized())
                    }
                },
                actions = {
                    IconButton(onClick = { PDFReportGenerator.shareReport(context, pet) }) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "Partilhar Relatório Clínico (PDF)".localized())
                    }
                    IconButton(onClick = {
                        val report = pet.getFormattedClinicalReport()
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, report)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Partilhar Relatório Clínico".localized()))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Partilhar Texto".localized())
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            PetAvatar(species = pet.species, photoUri = pet.photoUri, size = 68.dp)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = pet.name,
                                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = pet.gender.symbol,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = TealPrimary
                                    )
                                }
                                Text(
                                    text = "${pet.breed} • ${pet.ageInYears} " + "anos".localized() + " (~${pet.humanEquivalentAge} " + "anos humanos".localized() + ")",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = TealPrimary.copy(alpha = 0.12f)
                                    ) {
                                        Text(
                                            text = pet.neuterStatus.displayName,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TealPrimary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = AccentAmber.copy(alpha = 0.12f)
                                    ) {
                                        Text(
                                            text = pet.activityLevel.displayName,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = AccentAmber,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Biometrics WSAVA Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            BiometricBox(
                                title = "Peso Atual".localized(),
                                value = "%.1f kg".format(pet.currentWeightKg),
                                color = TealPrimary
                            )
                            BiometricBox(
                                title = "Score (BCS)".localized(),
                                value = "${pet.bcs}/9",
                                color = AccentAmber
                            )
                            BiometricBox(
                                title = "RER (Basal)".localized(),
                                value = "%.0f kcal".format(pet.rer),
                                color = AccentBlue
                            )
                            BiometricBox(
                                title = "DER (Diário)".localized(),
                                value = "%.0f kcal".format(pet.der),
                                color = AccentRose
                            )
                        }

                        if (pet.allergies.isNotEmpty() || pet.chronicConditions.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(AccentAmber.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Alergias / Cuidados: ".localized() + (pet.allergies + pet.chronicConditions).joinToString(", "),
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = { onOpenDigitalId(pet) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Ver Cartão Digital SOS".localized())
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onOpenHealthCharts(pet.id) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.AutoGraph, contentDescription = null, modifier = Modifier.size(16.dp), tint = TealPrimary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Gráficos".localized(), fontSize = 11.sp)
                            }
                            OutlinedButton(
                                onClick = { onOpenEvolutionPhotos(pet.id) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp), tint = AccentAmber)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Fotos".localized(), fontSize = 11.sp)
                            }
                            OutlinedButton(
                                onClick = { onOpenSymptomJournal(pet.id) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Healing, contentDescription = null, modifier = Modifier.size(16.dp), tint = AccentRose)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Sintomas".localized(), fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Diet Plans Section
            item {
                SectionHeader(title = "Plano Nutricional Ativo".localized(), icon = Icons.Default.Restaurant)
            }

            if (pet.dietPlans.isEmpty()) {
                item {
                    Text(
                        "Sem planos nutricionais ativos.".localized(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            } else {
                items(pet.dietPlans) { plan ->
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
                                Text(plan.foodName, fontWeight = FontWeight.Bold)
                                Text(
                                    "%.0f kcal/100g • Alvo: %.0f kcal/dia".format(plan.kcalPer100g, plan.targetDailyKcal),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = TealPrimary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    "%.0f g/dia".format(plan.dailyPortionGrams),
                                    fontWeight = FontWeight.Bold,
                                    color = TealPrimary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Weight History Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionHeader(title = "Evolução de Peso".localized(), icon = Icons.Default.FitnessCenter)
                    androidx.compose.material3.TextButton(onClick = { onOpenHealthCharts(pet.id) }) {
                        Text("Ver Gráfico".localized(), color = TealPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            items(pet.weightLogs.sortedByDescending { it.date }) { log ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(dateFormat.format(log.date), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                            log.notes?.let {
                                Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            }
                        }
                        Text(
                            "%.2f kg".format(log.weightKg),
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary
                        )
                    }
                }
            }

            // Vaccines & Medical
            item {
                SectionHeader(title = "Vacinação & Saúde Clínica".localized(), icon = Icons.Default.LocalHospital)
            }

            if (pet.vaccineLogs.isEmpty()) {
                item {
                    Text(
                        "Sem vacinas registadas.".localized(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            } else {
                items(pet.vaccineLogs) { vac ->
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(vac.vaccineName, fontWeight = FontWeight.Bold)
                                Text(
                                    "Aplicada em: ".localized() + dateFormat.format(vac.applicationDate),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            if (vac.isOverdue) {
                                Surface(shape = RoundedCornerShape(6.dp), color = AccentRose.copy(alpha = 0.15f)) {
                                    Text("Em atraso".localized(), color = AccentRose, modifier = Modifier.padding(6.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                vac.nextDueDate?.let { due ->
                                    Text("Próx: ".localized() + dateFormat.format(due), style = MaterialTheme.typography.bodySmall, color = TealPrimary)
                                }
                            }
                        }
                    }
                }
            }

            // Symptoms & Digestive Health Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionHeader(title = "Diário Clínico de Sintomas".localized(), icon = Icons.Default.Healing)
                    androidx.compose.material3.TextButton(onClick = { onOpenSymptomJournal(pet.id) }) {
                        Text("Ver Diário".localized(), color = TealPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (pet.symptomLogs.isEmpty()) {
                item {
                    Text(
                        "Sem sintomas clínicos registados recentemente.".localized(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            } else {
                items(pet.symptomLogs.take(3)) { log ->
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(dateFormat.format(log.date), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                if (!log.notes.isNullOrEmpty()) {
                                    Text(log.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                }
                            }
                            log.fecalScore?.let { score ->
                                Surface(shape = RoundedCornerShape(6.dp), color = TealPrimary.copy(alpha = 0.12f)) {
                                    Text("Score $score/7".localized(), color = TealPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(6.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BiometricBox(title: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = color)
        Text(title, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
    }
}

@Composable
fun SectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(icon, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
    }
}
