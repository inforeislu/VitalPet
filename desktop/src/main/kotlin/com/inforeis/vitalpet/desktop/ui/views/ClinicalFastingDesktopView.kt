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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import com.inforeis.vitalpet.desktop.ui.theme.AmberAccent
import com.inforeis.vitalpet.desktop.ui.theme.TealPrimary
import com.inforeis.vitalpet.models.FastingProcedure
import com.inforeis.vitalpet.services.DesktopPetRepository
import com.inforeis.vitalpet.services.LanguageManager.localized
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun ClinicalFastingDesktopView() {
    val pets by DesktopPetRepository.pets.collectAsState()
    var selectedProcedure by remember { mutableStateOf(FastingProcedure.GENERAL_SURGERY) }
    var procedureHour by remember { mutableStateOf("09:00") }

    val foodHours = selectedProcedure.defaultFoodHours
    val waterHours = selectedProcedure.defaultWaterHours

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Temporizador de Jejum Pré-Operatório".localized(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Planeamento fisiológico de corte de alimentos sólidos e água para anestesia e exames.".localized(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            // Parâmetros do Procedimento
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Tipo de Procedimento Clínico".localized(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(14.dp))

                    FastingProcedure.values().filter { it != FastingProcedure.CUSTOM }.forEach { proc ->
                        val isSelected = selectedProcedure == proc
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) TealPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, TealPrimary) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(proc.displayName, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                    Text("Sólidos: ${proc.defaultFoodHours}h • Água: ${proc.defaultWaterHours}h", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Button(
                                    onClick = { selectedProcedure = proc },
                                    colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) TealPrimary else MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Text(if (isSelected) "Selecionado" else "Escolher", fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = procedureHour,
                        onValueChange = { procedureHour = it },
                        label = { Text("Hora Prevista do Procedimento (HH:mm)".localized()) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Cronograma Fisiológico Calculado
            Card(
                modifier = Modifier.weight(1.2f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Timer, contentDescription = null, tint = TealPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Linha do Tempo de Jejum".localized(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    FastingTimelineStage(
                        step = "1",
                        icon = Icons.Default.Restaurant,
                        title = "Corte de Comida Sólida ($foodHours horas antes)".localized(),
                        description = "Retirar toda a ração, petiscos e ossos para esvaziamento gástrico seguro.".localized(),
                        tint = Color(0xFFF97316)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    FastingTimelineStage(
                        step = "2",
                        icon = Icons.Default.WaterDrop,
                        title = "Corte de Água ($waterHours horas antes)".localized(),
                        description = "Retirar o bebedouro. Água após este período aumenta risco de broncoaspiração anestésica.".localized(),
                        tint = Color(0xFF3B82F6)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    FastingTimelineStage(
                        step = "3",
                        icon = Icons.Default.CheckCircle,
                        title = "Hora do Procedimento / Anestesia".localized(),
                        description = "Paciente pronto e seguro para indução anestésica ou exames diagnósticos.".localized(),
                        tint = Color(0xFF10B981)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = AmberAccent.copy(alpha = 0.12f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Aviso Clínico Obrigatório:".localized(), fontWeight = FontWeight.Bold, color = AmberAccent)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Filhotes (<4 meses) e animais diabéticos possuem protocolos de jejum reduzidos (máx 2-4h) para evitar hipoglicemia severa. Consulte sempre o veterinário responsável.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FastingTimelineStage(
    step: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    tint: Color
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
