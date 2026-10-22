package com.inforeis.vitalpet.ui.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.saveable.rememberSaveable
import com.inforeis.vitalpet.models.Pet
import com.inforeis.vitalpet.services.LanguageManager
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.WorkspacePremium
import com.inforeis.vitalpet.services.LanguageManager.localized
import com.inforeis.vitalpet.services.SubscriptionManager
import com.inforeis.vitalpet.ui.theme.AccentAmber
import com.inforeis.vitalpet.ui.theme.TealPrimary

enum class AppTab(val titleKey: String, val icon: ImageVector) {
    PETS("Meus Pets", Icons.Default.Pets),
    UTILITIES("Utilitários", Icons.Default.Build),
    EMERGENCY("Emergência", Icons.Default.MedicalServices),
    SETTINGS("Definições", Icons.Default.Settings);

    val displayName: String
        get() = titleKey.localized()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onSelectPet: (Pet) -> Unit,
    onAddPet: () -> Unit,
    onEditPet: (Pet) -> Unit,
    onNavigateUtility: (String) -> Unit,
    onOpenToxicGuide: () -> Unit,
    onOpenEmergencyMap: () -> Unit,
    onOpenPaywall: () -> Unit = {}
) {
    var currentTab by rememberSaveable { mutableStateOf(AppTab.PETS) }
    val currentLang by LanguageManager.currentLanguage.collectAsState()
    val isPremium by SubscriptionManager.isPremium.collectAsState()
    val flag = LanguageManager.SUPPORTED_LANGUAGES.find { it.code == currentLang }?.flag ?: "🇵🇹"

    Scaffold(
        topBar = {
            if (currentTab != AppTab.UTILITIES && currentTab != AppTab.EMERGENCY) {
                TopAppBar(
                    title = {
                        Text(
                            text = "VitalPet",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 0.5.sp
                        )
                    },
                    actions = {
                        // Premium PRO Badge
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isPremium) TealPrimary.copy(alpha = 0.15f) else AccentAmber.copy(alpha = 0.18f),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clickable { onOpenPaywall() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WorkspacePremium,
                                    contentDescription = null,
                                    tint = if (isPremium) TealPrimary else AccentAmber,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isPremium) "PRO" else "PREMIUM",
                                    color = if (isPremium) TealPrimary else AccentAmber,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Language selector trigger
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .clickable { currentTab = AppTab.SETTINGS }
                        ) {
                            Box(
                                modifier = Modifier.size(36.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = flag, fontSize = 18.sp)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                AppTab.entries.forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.displayName
                            )
                        },
                        label = {
                            Text(
                                text = tab.displayName,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TealPrimary,
                            selectedTextColor = TealPrimary,
                            indicatorColor = TealPrimary.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (currentTab) {
                AppTab.PETS -> PetListView(
                    onSelectPet = onSelectPet,
                    onAddPet = onAddPet,
                    onEditPet = onEditPet
                )
                AppTab.UTILITIES -> UtilitiesHubScreen(
                    onNavigate = onNavigateUtility
                )
                AppTab.EMERGENCY -> EmergencyProtocolsScreen(
                    onOpenToxicGuide = onOpenToxicGuide,
                    onOpenEmergencyMap = onOpenEmergencyMap
                )
                AppTab.SETTINGS -> SettingsScreen(
                    onOpenPaywall = onOpenPaywall
                )
            }
        }
    }
}
