package com.inforeis.vitalpet.ui.views

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inforeis.vitalpet.services.AppThemeMode
import com.inforeis.vitalpet.services.BackupManager
import com.inforeis.vitalpet.services.LanguageManager
import com.inforeis.vitalpet.services.LanguageManager.localized
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.OutlinedButton
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.NotificationsActive
import com.inforeis.vitalpet.services.ClinicalNotificationManager
import com.inforeis.vitalpet.services.PetRepository
import com.inforeis.vitalpet.services.SubscriptionManager
import com.inforeis.vitalpet.services.ThemeManager
import com.inforeis.vitalpet.ui.theme.AccentAmber
import com.inforeis.vitalpet.ui.theme.TealPrimary

@Composable
fun SettingsScreen(
    onOpenPaywall: () -> Unit = {}
) {
    val context = LocalContext.current
    val currentLang by LanguageManager.currentLanguage.collectAsState()
    val currentThemeMode by ThemeManager.currentThemeMode.collectAsState()
    val pets by PetRepository.pets.collectAsState()

    var showDiagnosticDialog by remember { mutableStateOf(false) }
    var showDataSafetyDialog by remember { mutableStateOf(false) }
    val integrityReport = remember(pets, currentLang) {
        BackupManager.performIntegrityCheck(context)
    }

    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val result = BackupManager.importBackup(context, uri)
            result.onSuccess { count ->
                Toast.makeText(
                    context,
                    "%d animais importados com sucesso.".localized().format(count),
                    Toast.LENGTH_LONG
                ).show()
            }.onFailure { err ->
                Toast.makeText(
                    context,
                    "Erro ao ler ficheiro de backup".localized() + ": ${err.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Definições da Aplicação".localized(),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
        }

        // Premium Subscription Section
        item {
            val isPremium by SubscriptionManager.isPremium.collectAsState()
            val currentPlan by SubscriptionManager.currentPlan.collectAsState()

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isPremium) TealPrimary.copy(alpha = 0.08f) else AccentAmber.copy(alpha = 0.1f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.5.dp,
                    color = if (isPremium) TealPrimary else AccentAmber
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenPaywall() }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (isPremium) TealPrimary.copy(alpha = 0.18f) else AccentAmber.copy(alpha = 0.2f),
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = if (isPremium) TealPrimary else AccentAmber,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isPremium) "VitalPet Premium Ativo".localized() else "VitalPet Premium".localized(),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isPremium) {
                                currentPlan?.titleKey?.localized() ?: "Plano Ativo".localized()
                            } else {
                                "Desbloqueie todo o potencial da saúde e nutrição dos seus animais.".localized()
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onOpenPaywall,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPremium) TealPrimary else AccentAmber
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (isPremium) "Gerir Assinatura".localized() else "Ver Planos".localized(),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Theme Selector Section
        item {
            Text(
                text = "Tema da Aplicação".localized(),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    val themeOptions = listOf(
                        AppThemeMode.LIGHT to ("Tema Claro".localized()),
                        AppThemeMode.DARK to ("Tema Escuro".localized()),
                        AppThemeMode.SYSTEM to ("Padrão do Sistema".localized())
                    )

                    themeOptions.forEachIndexed { index, (mode, label) ->
                        val isSelected = currentThemeMode == mode
                        val icon = when (mode) {
                            AppThemeMode.LIGHT -> Icons.Default.LightMode
                            AppThemeMode.DARK -> Icons.Default.DarkMode
                            AppThemeMode.SYSTEM -> Icons.Default.BrightnessAuto
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { ThemeManager.setThemeMode(mode) }
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) TealPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = if (isSelected) TealPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                                Text(
                                    text = when (mode) {
                                        AppThemeMode.LIGHT -> "Visual clínico claro com contraste otimizado".localized()
                                        AppThemeMode.DARK -> "Ambiente noturno confortável para os olhos".localized()
                                        AppThemeMode.SYSTEM -> "Sincroniza automaticamente com o dispositivo".localized()
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selecionado",
                                    tint = TealPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        if (index < themeOptions.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }

        // Language Selector Section
        item {
            Text(
                text = "Idioma da Aplicação (8 Idiomas Oficiais)".localized(),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    LanguageManager.SUPPORTED_LANGUAGES.forEach { lang ->
                        val isSelected = currentLang == lang.code
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { LanguageManager.setLanguage(lang.code, context) }
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = lang.flag, fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(14.dp))
                            Text(
                                text = lang.name,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selecionado",
                                    tint = TealPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Notifications & Clinical Alarms Section
        item {
            Text(
                text = "Notificações & Alarmes Clínicos".localized(),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        item {
            var fastingNotifEnabled by remember {
                mutableStateOf(ClinicalNotificationManager.isFastingNotificationEnabled(context))
            }
            var weighInNotifEnabled by remember {
                mutableStateOf(ClinicalNotificationManager.isWeighInNotificationEnabled(context))
            }

            val notificationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { _ -> }

            val checkAndRequestNotificationPermission: (() -> Unit) -> Unit = { onGranted ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        onGranted()
                    }
                } else {
                    onGranted()
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Fasting notification switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = TealPrimary.copy(alpha = 0.12f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Timer, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Alertas de Jejum Cirúrgico".localized(), fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            Text("Avisos de corte de comida sólida e retirada de água".localized(), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                        Switch(
                            checked = fastingNotifEnabled,
                            onCheckedChange = { isChecked ->
                                fastingNotifEnabled = isChecked
                                ClinicalNotificationManager.setFastingNotificationEnabled(context, isChecked)
                                if (isChecked) {
                                    checkAndRequestNotificationPermission {}
                                }
                            }
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Weigh-in reminder switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = AccentAmber.copy(alpha = 0.12f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Scale, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Lembretes de Pesagem Periódica".localized(), fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            Text("Acompanhamento quinzenal da curva de peso".localized(), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                        Switch(
                            checked = weighInNotifEnabled,
                            onCheckedChange = { isChecked ->
                                weighInNotifEnabled = isChecked
                                ClinicalNotificationManager.setWeighInNotificationEnabled(context, isChecked)
                                if (isChecked) {
                                    checkAndRequestNotificationPermission {}
                                }
                            }
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Test notification button
                    OutlinedButton(
                        onClick = {
                            checkAndRequestNotificationPermission {
                                ClinicalNotificationManager.sendTestNotification(context)
                                Toast.makeText(context, "Notificação de teste enviada!".localized(), Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Testar Notificação de Sistema".localized(), fontSize = 13.sp)
                    }
                }
            }
        }

        // Backup and Data Section
        item {
            Text(
                text = "Dados & Cópias de Segurança".localized(),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        // Diagnostic / Health Verification Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Status Badge Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = if (integrityReport.isHealthy) Color(0xFF4CAF50) else Color(0xFFFB8C00),
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Base de Dados Segura & Íntegra".localized(),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF4CAF50).copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "${integrityReport.integrityScore}%",
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                    // Metrics Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Total de Animais:".localized(),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "${integrityReport.totalPets}",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        Column {
                            Text(
                                text = "Registos Clínicos:".localized(),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "${integrityReport.totalClinicalRecords}",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        Column {
                            Text(
                                text = "Tamanho da Base de Dados:".localized(),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                text = integrityReport.databaseSizeFormatted,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Última Cópia de Segurança:".localized(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = if (integrityReport.lastBackupFormatted == "Nunca realizada") "Nunca realizada".localized() else integrityReport.lastBackupFormatted,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Action Options Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Export Backup
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val shared = BackupManager.shareBackup(context)
                                if (shared) {
                                    Toast.makeText(
                                        context,
                                        "Cópia de Segurança Gerada com Sucesso".localized(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, tint = TealPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Exportar Cópia de Segurança (JSON)".localized(), fontWeight = FontWeight.SemiBold)
                            Text("Guardar dados dos animais em formato seguro".localized(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                    // Import Backup
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                importLauncher.launch("*/*")
                            }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.FileUpload, contentDescription = null, tint = Color(0xFF007AFF))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Importar Cópia de Segurança (JSON)".localized(), fontWeight = FontWeight.SemiBold)
                            Text("Restaurar animais e registos de um ficheiro".localized(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                    // Verify Database
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDiagnosticDialog = true }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF4CAF50))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Verificar Base de Dados".localized(), fontWeight = FontWeight.SemiBold)
                            Text("Diagnóstico completo de integridade e consistência".localized(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                    // Reset / Clear All Data
                    var showClearConfirmDialog by remember { mutableStateOf(false) }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showClearConfirmDialog = true }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.DeleteForever, contentDescription = null, tint = Color.Red)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Eliminar Todos os Dados".localized(), fontWeight = FontWeight.SemiBold, color = Color.Red)
                            Text("Limpar a aplicação e remover todos os animais registados".localized(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                    }

                    if (showClearConfirmDialog) {
                        AlertDialog(
                            onDismissRequest = { showClearConfirmDialog = false },
                            title = { Text("Limpar Aplicação".localized()) },
                            text = { Text("Tem a certeza que deseja eliminar todos os animais e registos? Esta ação é irreversível.".localized()) },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        PetRepository.clearAllData()
                                        showClearConfirmDialog = false
                                        Toast.makeText(context, "Todos os dados foram eliminados".localized(), Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Text("Eliminar Tudo".localized(), color = Color.Red)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showClearConfirmDialog = false }) {
                                    Text("Cancelar".localized())
                                }
                            }
                        )
                    }
                }
            }
        }

        // App Info & Legal Section
        item {
            Text(
                text = "Sobre a Aplicação & Privacidade".localized(),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = TealPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("VitalPet Android", fontWeight = FontWeight.Bold)
                            Text("Versão 1.0.0 (Build 1) • Google Play Ready".localized(), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                    }

                    Text(
                        text = "Diretrizes oficiais WSAVA e NRC para nutrição e bem-estar animal. Compatível com as normas de segurança e privacidade da Google.".localized(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                    // Privacy Policy Link
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://inforeis.com/privacy"))
                                context.startActivity(intent)
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Política de Privacidade".localized(), fontWeight = FontWeight.SemiBold, color = TealPrimary, modifier = Modifier.weight(1f))
                        Text("↗", color = TealPrimary, fontWeight = FontWeight.Bold)
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                    // Terms of Service Link
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://inforeis.com/terms"))
                                context.startActivity(intent)
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Termos de Utilização".localized(), fontWeight = FontWeight.SemiBold, color = TealPrimary, modifier = Modifier.weight(1f))
                        Text("↗", color = TealPrimary, fontWeight = FontWeight.Bold)
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                    // Data Safety & Storage Info Dialog trigger
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDataSafetyDialog = true }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Segurança de Dados no Dispositivo".localized(), fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                        Text("ℹ️", fontSize = 14.sp)
                    }
                }
            }
        }
    }

    // Diagnostic Alert Dialog
    if (showDiagnosticDialog) {
        AlertDialog(
            onDismissRequest = { showDiagnosticDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFF4CAF50))
                    Text(
                        text = "Estado dos Dados & Integridade".localized(),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Todos os registos foram inspecionados com sucesso:".localized(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• " + "Total de Animais:".localized() + " ${integrityReport.totalPets}", style = MaterialTheme.typography.bodySmall)
                    Text("• " + "Registos de Peso:".localized() + " ${integrityReport.totalWeightLogs}", style = MaterialTheme.typography.bodySmall)
                    Text("• " + "Vacinas & Desparasitações:".localized() + " ${integrityReport.totalVaccineLogs + integrityReport.totalDewormingLogs}", style = MaterialTheme.typography.bodySmall)
                    Text("• " + "Consultas & Sintomas:".localized() + " ${integrityReport.totalMedicalVisits + integrityReport.totalSymptomLogs}", style = MaterialTheme.typography.bodySmall)
                    Text("• " + "Passeios & Jejum:".localized() + " ${integrityReport.totalWalkSessions + integrityReport.totalFastingLogs}", style = MaterialTheme.typography.bodySmall)
                    Text("• " + "Tamanho Total:".localized() + " ${integrityReport.databaseSizeFormatted}", style = MaterialTheme.typography.bodySmall)

                    Spacer(modifier = Modifier.height(6.dp))
                    if (integrityReport.issuesFound.isEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                            Text(
                                text = "Nenhum erro encontrado".localized(),
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    } else {
                        integrityReport.issuesFound.forEach { issue ->
                            Text("⚠️ $issue", color = Color(0xFFE53935), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDiagnosticDialog = false }) {
                    Text("Fechar".localized(), fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showDataSafetyDialog) {
        AlertDialog(
            onDismissRequest = { showDataSafetyDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = TealPrimary)
                    Text("Segurança & Proteção de Dados".localized(), fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "O VitalPet armazena todos os registos clínicos, dados demográficos, notas e fotografias diretamente no armazenamento protegido do seu dispositivo Android.".localized(),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        "• Não vendemos nem partilhamos os seus dados pessoais com terceiros.".localized(),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "• Acesso à localização é usado exclusivamente em primeiro plano para encontrar hospitais veterinários 24h próximos.".localized(),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        "• As cópias de segurança em ficheiro JSON permanecem sob o seu controlo absoluto.".localized(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showDataSafetyDialog = false }) {
                    Text("Compreendi".localized())
                }
            }
        )
    }
}
