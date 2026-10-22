package com.inforeis.vitalpet.desktop

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.inforeis.vitalpet.desktop.ui.theme.AppThemeMode
import com.inforeis.vitalpet.desktop.ui.theme.ThemeManager
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.inforeis.vitalpet.desktop.ui.theme.TealPrimary
import com.inforeis.vitalpet.desktop.ui.theme.VitalPetDesktopTheme
import com.inforeis.vitalpet.desktop.ui.views.BreedEncyclopediaDesktopView
import com.inforeis.vitalpet.desktop.ui.views.ClinicalCalculatorsDesktopView
import com.inforeis.vitalpet.desktop.ui.views.ClinicalFastingDesktopView
import com.inforeis.vitalpet.desktop.ui.views.DashboardDesktopView
import com.inforeis.vitalpet.desktop.ui.views.EmergencyProtocolsDesktopView
import com.inforeis.vitalpet.desktop.ui.views.HealthChartsDesktopView
import com.inforeis.vitalpet.desktop.ui.views.PetsMasterDetailView
import com.inforeis.vitalpet.desktop.ui.views.SettingsDesktopView
import com.inforeis.vitalpet.desktop.ui.views.SymptomJournalDesktopView
import com.inforeis.vitalpet.desktop.ui.views.SyncHubView
import com.inforeis.vitalpet.desktop.ui.views.ToxicFoodDesktopView
import com.inforeis.vitalpet.desktop.ui.views.WalkTrackerDesktopView
import com.inforeis.vitalpet.services.DesktopPetRepository
import com.inforeis.vitalpet.services.LanguageManager
import com.inforeis.vitalpet.services.LanguageManager.localized
import com.inforeis.vitalpet.sync.VitalPetSyncServer
import java.awt.Dimension

enum class DesktopNavSection(val label: String, val icon: ImageVector) {
    DASHBOARD("Painel", Icons.Default.Dashboard),
    PETS("Meus Pets", Icons.Default.Pets),
    SYMPTOMS("Sintomas", Icons.Default.MonitorHeart),
    HEALTH_CHARTS("Evolução", Icons.Default.FitnessCenter),
    WALK_TRACKER("Passeios", Icons.Default.DirectionsWalk),
    CALCULATORS("Calculadoras", Icons.Default.Calculate),
    BREEDS("Raças", Icons.Default.MenuBook),
    TOXIC_FOOD("Toxidez", Icons.Default.Warning),
    FASTING("Jejum", Icons.Default.Timer),
    EMERGENCY("Primeiros Socorros", Icons.Default.Emergency),
    SYNC("Sincronização", Icons.Default.PhoneAndroid),
    SETTINGS("Definições", Icons.Default.Settings)
}

fun main() = application {
    // 1. Inicializar Idiomas e Dados
    LanguageManager.initialize()
    DesktopPetRepository.initialize()

    // 2. Iniciar Servidor de Sincronização em segundo plano
    VitalPetSyncServer.start()

    val windowState = rememberWindowState(
        width = 1260.dp,
        height = 820.dp,
        position = WindowPosition(Alignment.Center)
    )

    Window(
        onCloseRequest = {
            VitalPetSyncServer.stop()
            exitApplication()
        },
        state = windowState,
        title = "VitalPet - Gestão Clínica e Nutricional Veterinária (Windows)",
        icon = painterResource("icon.png")
    ) {
        window.minimumSize = Dimension(1000, 680)

        VitalPetDesktopTheme {
            VitalPetDesktopApp()
        }
    }
}

@Composable
fun VitalPetDesktopApp() {
    var currentSection by remember { mutableStateOf(DesktopNavSection.DASHBOARD) }

    Row(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // Sidebar / Navigation Rail
        NavigationRail(
            modifier = Modifier.width(96.dp).fillMaxHeight(),
            containerColor = MaterialTheme.colorScheme.surface,
            header = {
                Column(
                    modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(TealPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🐾", fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "VitalPet",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TealPrimary
                    )
                }
            }
        ) {
            DesktopNavSection.values().forEach { section ->
                NavigationRailItem(
                    selected = currentSection == section,
                    onClick = { currentSection = section },
                    icon = { Icon(section.icon, contentDescription = section.label) },
                    label = {
                        Text(
                            text = section.label.localized(),
                            fontSize = 11.sp,
                            fontWeight = if (currentSection == section) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationRailItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = TealPrimary,
                        indicatorColor = TealPrimary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            val themeMode by ThemeManager.themeMode.collectAsState()
            IconButton(
                onClick = {
                    val nextMode = if (themeMode == AppThemeMode.DARK) AppThemeMode.LIGHT else AppThemeMode.DARK
                    ThemeManager.setThemeMode(nextMode)
                },
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = if (themeMode == AppThemeMode.DARK) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Alternar Tema",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Divider(
            modifier = Modifier.fillMaxHeight().width(1.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        // Conteúdo Principal
        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
            when (currentSection) {
                DesktopNavSection.DASHBOARD -> DashboardDesktopView()
                DesktopNavSection.PETS -> PetsMasterDetailView()
                DesktopNavSection.SYMPTOMS -> SymptomJournalDesktopView()
                DesktopNavSection.HEALTH_CHARTS -> HealthChartsDesktopView()
                DesktopNavSection.WALK_TRACKER -> WalkTrackerDesktopView()
                DesktopNavSection.CALCULATORS -> ClinicalCalculatorsDesktopView()
                DesktopNavSection.BREEDS -> BreedEncyclopediaDesktopView()
                DesktopNavSection.TOXIC_FOOD -> ToxicFoodDesktopView()
                DesktopNavSection.FASTING -> ClinicalFastingDesktopView()
                DesktopNavSection.EMERGENCY -> EmergencyProtocolsDesktopView()
                DesktopNavSection.SYNC -> SyncHubView()
                DesktopNavSection.SETTINGS -> SettingsDesktopView()
            }
        }
    }
}
