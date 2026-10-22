package com.inforeis.vitalpet.ui.views

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.inforeis.vitalpet.models.FastingLog
import com.inforeis.vitalpet.models.FastingProcedure
import com.inforeis.vitalpet.models.FastingStage
import com.inforeis.vitalpet.models.Pet
import com.inforeis.vitalpet.services.ClinicalNotificationManager
import com.inforeis.vitalpet.services.LanguageManager.localized
import com.inforeis.vitalpet.services.PetRepository
import com.inforeis.vitalpet.ui.components.PetAvatar
import com.inforeis.vitalpet.ui.theme.AccentAmber
import com.inforeis.vitalpet.ui.theme.AccentRose
import com.inforeis.vitalpet.ui.theme.TealPrimary
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClinicalFastingTimerScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val pets by PetRepository.pets.collectAsState()

    var selectedPetId by remember(pets) {
        mutableStateOf(pets.firstOrNull()?.id ?: "")
    }
    val currentPet = pets.firstOrNull { it.id == selectedPetId } ?: pets.firstOrNull()

    // Find active fasting log for this pet
    val activeFasting = currentPet?.fastingLogs?.firstOrNull { !it.isCompleted }

    // Live ticker
    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(activeFasting?.id) {
        while (true) {
            currentTime = System.currentTimeMillis()
            delay(1000L)
        }
    }

    // Permission launcher for Android 13+
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // Setup state
    var selectedProcedure by remember { mutableStateOf(FastingProcedure.GENERAL_SURGERY) }
    var customProcedureName by remember { mutableStateOf("") }
    var foodHours by remember { mutableStateOf(selectedProcedure.defaultFoodHours) }
    var waterHours by remember { mutableStateOf(selectedProcedure.defaultWaterHours) }
    var hoursUntilProcedure by remember { mutableStateOf(10f) }

    // Synchronize hours when procedure changes
    LaunchedEffect(selectedProcedure) {
        foodHours = selectedProcedure.defaultFoodHours
        waterHours = selectedProcedure.defaultWaterHours
        if (hoursUntilProcedure < foodHours) {
            hoursUntilProcedure = (foodHours + 2).toFloat()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Temporizador de Jejum".localized(), fontWeight = FontWeight.Bold) },
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
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // 1. Pet Selector Carousel
            if (pets.isNotEmpty()) {
                Text(
                    text = "Selecionar Animal:".localized(),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(pets) { pet ->
                        val isSelected = pet.id == currentPet?.id
                        val petHasActiveFasting = pet.fastingLogs.any { !it.isCompleted }

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) TealPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, TealPrimary) else null,
                            modifier = Modifier.clickable {
                                selectedPetId = pet.id
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PetAvatar(species = pet.species, photoUri = pet.photoUri, size = 36.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = pet.name,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 14.sp
                                    )
                                    if (petHasActiveFasting) {
                                        Text(
                                            text = "Jejum Ativo".localized(),
                                            fontSize = 10.sp,
                                            color = AccentAmber,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (currentPet != null && activeFasting != null) {
                // 2. ACTIVE FASTING VIEW
                ActiveFastingTracker(
                    pet = currentPet,
                    fastingLog = activeFasting,
                    currentTime = currentTime,
                    onFinishProcedure = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        ClinicalNotificationManager.cancelFastingAlerts(context, activeFasting.id)
                        val updatedFasting = activeFasting.copy(isCompleted = true)
                        val updatedLogs = currentPet.fastingLogs.map {
                            if (it.id == activeFasting.id) updatedFasting else it
                        }.toMutableList()
                        PetRepository.updatePet(currentPet.copy(fastingLogs = updatedLogs))
                    },
                    onCancelFasting = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        ClinicalNotificationManager.cancelFastingAlerts(context, activeFasting.id)
                        val updatedLogs = currentPet.fastingLogs.filter { it.id != activeFasting.id }.toMutableList()
                        PetRepository.updatePet(currentPet.copy(fastingLogs = updatedLogs))
                    }
                )
            } else if (currentPet != null) {
                // 3. NEW FASTING SETUP VIEW
                NewFastingSetupView(
                    pet = currentPet,
                    selectedProcedure = selectedProcedure,
                    onSelectProcedure = { selectedProcedure = it },
                    customProcedureName = customProcedureName,
                    onCustomProcedureNameChange = { customProcedureName = it },
                    foodHours = foodHours,
                    onFoodHoursChange = { foodHours = it },
                    waterHours = waterHours,
                    onWaterHoursChange = { waterHours = it },
                    hoursUntilProcedure = hoursUntilProcedure,
                    onHoursUntilProcedureChange = { hoursUntilProcedure = it },
                    onStartFasting = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        val cal = Calendar.getInstance()
                        cal.timeInMillis = System.currentTimeMillis() + (hoursUntilProcedure * 3600 * 1000).toLong()

                        val newLog = FastingLog(
                            procedure = selectedProcedure,
                            customProcedureName = if (selectedProcedure == FastingProcedure.CUSTOM) customProcedureName.ifBlank { null } else null,
                            procedureDate = cal.time,
                            foodFastingHours = foodHours,
                            waterFastingHours = waterHours,
                            isCompleted = false
                        )

                        // Schedule system notifications
                        ClinicalNotificationManager.scheduleFastingAlerts(context, currentPet.name, newLog)

                        // Save in repository
                        val updatedLogs = currentPet.fastingLogs.toMutableList().apply { add(0, newLog) }
                        PetRepository.updatePet(currentPet.copy(fastingLogs = updatedLogs))
                    }
                )
            } else {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Nenhum animal registado para iniciar o jejum.".localized(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ActiveFastingTracker(
    pet: Pet,
    fastingLog: FastingLog,
    currentTime: Long,
    onFinishProcedure: () -> Unit,
    onCancelFasting: () -> Unit
) {
    val stage = fastingLog.currentStage(Date(currentTime))
    val timeFormat = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())

    val (stageColor, stageTitle, stageDesc) = when (stage) {
        FastingStage.BEFORE_FASTING -> Triple(
            Color(0xFF4CAF50),
            "Alimentação Permitida".localized(),
            "O animal pode alimentar-se normalmente até ao início da janela de jejum.".localized()
        )
        FastingStage.WATER_ONLY -> Triple(
            AccentAmber,
            "Jejum de Sólidos (Água Permitida)".localized(),
            "Retire toda a ração, petiscos e sólidos. Mantenha apenas água fresca disponível.".localized()
        )
        FastingStage.TOTAL_FASTING -> Triple(
            AccentRose,
            "Jejum Total Obrigatório".localized(),
            "Retire imediatamente a taça da água. O animal não pode ingerir nada até à cirurgia.".localized()
        )
        FastingStage.READY -> Triple(
            TealPrimary,
            "Pronto para Cirurgia / Sedação".localized(),
            "O tempo de esvaziamento gástrico foi atingido com segurança para indução anestésica.".localized()
        )
        FastingStage.COMPLETED -> Triple(
            TealPrimary,
            "Procedimento Concluído".localized(),
            "O protocolo foi concluído.".localized()
        )
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Stage Indicator Header
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = stageColor.copy(alpha = 0.15f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(stageColor, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stageTitle,
                        fontWeight = FontWeight.Bold,
                        color = stageColor,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Large Circular Timer
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(stageColor.copy(alpha = 0.08f))
                    .border(3.dp, stageColor.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val remainingMillis = fastingLog.procedureDate.time - currentTime
                    if (remainingMillis > 0) {
                        val hours = (remainingMillis / (1000 * 60 * 60))
                        val minutes = (remainingMillis / (1000 * 60)) % 60
                        val seconds = (remainingMillis / 1000) % 60

                        Text(
                            text = String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = stageColor
                        )
                        Text(
                            text = "Para o Procedimento".localized(),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = TealPrimary,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Pronto!".localized(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${pet.name} • ${fastingLog.procedureDisplayName}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stageDesc,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Timeline Steps
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(14.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Etapas Fisiológicas do Jejum:".localized(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                // 1. Food Cutoff
                val foodCutPassed = currentTime >= fastingLog.foodCutoffDate.time
                TimelineRow(
                    title = "Corte de Sólidos (Sem Ração/Comida)".localized(),
                    time = timeFormat.format(fastingLog.foodCutoffDate),
                    isPassed = foodCutPassed,
                    icon = Icons.Default.Restaurant,
                    color = AccentAmber
                )

                // 2. Water Cutoff
                val waterCutPassed = currentTime >= fastingLog.waterCutoffDate.time
                TimelineRow(
                    title = "Corte de Líquidos (Retirar Água)".localized(),
                    time = timeFormat.format(fastingLog.waterCutoffDate),
                    isPassed = waterCutPassed,
                    icon = Icons.Default.WaterDrop,
                    color = AccentRose
                )

                // 3. Procedure Start
                val procPassed = currentTime >= fastingLog.procedureDate.time
                TimelineRow(
                    title = "Hora do Procedimento Veterinário".localized(),
                    time = timeFormat.format(fastingLog.procedureDate),
                    isPassed = procPassed,
                    icon = Icons.Default.Alarm,
                    color = TealPrimary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCancelFasting,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Cancelar".localized(), fontSize = 13.sp)
                }

                Button(
                    onClick = onFinishProcedure,
                    modifier = Modifier.weight(1.3f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Concluir".localized(), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TimelineRow(
    title: String,
    time: String,
    isPassed: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = if (isPassed) color else color.copy(alpha = 0.15f),
            modifier = Modifier.size(28.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isPassed) Color.White else color,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = if (isPassed) FontWeight.Bold else FontWeight.Medium
            )
            Text(
                text = time,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        if (isPassed) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = TealPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun NewFastingSetupView(
    pet: Pet,
    selectedProcedure: FastingProcedure,
    onSelectProcedure: (FastingProcedure) -> Unit,
    customProcedureName: String,
    onCustomProcedureNameChange: (String) -> Unit,
    foodHours: Int,
    onFoodHoursChange: (Int) -> Unit,
    waterHours: Int,
    onWaterHoursChange: (Int) -> Unit,
    hoursUntilProcedure: Float,
    onHoursUntilProcedureChange: (Float) -> Unit,
    onStartFasting: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Novo Protocolo de Jejum para %s".localized().format(pet.name),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            // Procedure Selection Chips
            Text(
                text = "Procedimento Clínico:".localized(),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FastingProcedure.entries.forEach { proc ->
                    FilterChip(
                        selected = selectedProcedure == proc,
                        onClick = { onSelectProcedure(proc) },
                        label = { Text(proc.displayName) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TealPrimary.copy(alpha = 0.15f),
                            selectedLabelColor = TealPrimary
                        )
                    )
                }
            }

            AnimatedVisibility(visible = selectedProcedure == FastingProcedure.CUSTOM) {
                OutlinedTextField(
                    value = customProcedureName,
                    onValueChange = onCustomProcedureNameChange,
                    label = { Text("Nome do Procedimento".localized()) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Procedure Scheduling Slider (Hours until procedure)
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Início do Procedimento:".localized(), fontWeight = FontWeight.SemiBold)
                    Text("Daqui a %.0f horas".localized().format(hoursUntilProcedure), color = TealPrimary, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = hoursUntilProcedure,
                    onValueChange = onHoursUntilProcedureChange,
                    valueRange = maxOf(4f, (foodHours + 1).toFloat())..24f,
                    steps = 19
                )
            }

            // Food and Water Fasting Adjusters
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(AccentRose.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Jejum Sólidos".localized(), fontSize = 12.sp, color = AccentRose, fontWeight = FontWeight.SemiBold)
                    Text("${foodHours}h", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = AccentRose)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(onClick = { if (foodHours > 2) onFoodHoursChange(foodHours - 1) }, modifier = Modifier.size(32.dp)) {
                            Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = { if (foodHours < 24) onFoodHoursChange(foodHours + 1) }, modifier = Modifier.size(32.dp)) {
                            Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(TealPrimary.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Jejum Líquidos".localized(), fontSize = 12.sp, color = TealPrimary, fontWeight = FontWeight.SemiBold)
                    Text("${waterHours}h", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TealPrimary)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(onClick = { if (waterHours > 0) onWaterHoursChange(waterHours - 1) }, modifier = Modifier.size(32.dp)) {
                            Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = { if (waterHours < foodHours) onWaterHoursChange(waterHours + 1) }, modifier = Modifier.size(32.dp)) {
                            Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Start Button with Alarm Icon
            Button(
                onClick = onStartFasting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                Icon(Icons.Default.NotificationsActive, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Iniciar Jejum & Ativar Alarmes".localized(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            // Clinical Safety Guidelines
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        "Protocolo de Segurança WSAVA / AAHA:".localized(),
                        fontWeight = FontWeight.Bold,
                        color = TealPrimary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "O cumprimento rigoroso dos tempos de jejum evita aspiração pulmonar durante a anestesia e garante a precisão de ecografias e análises bioquímicas.".localized(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
