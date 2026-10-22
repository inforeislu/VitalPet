package com.inforeis.vitalpet.ui.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inforeis.vitalpet.services.AppThemeMode
import com.inforeis.vitalpet.services.LanguageManager.localized
import com.inforeis.vitalpet.services.ThemeManager
import com.inforeis.vitalpet.ui.theme.TealLight

data class UtilityItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconColor: Color,
    val route: String
)

data class UtilitySection(
    val title: String,
    val items: List<UtilityItem>
)

@Composable
fun UtilitiesHubScreen(
    onNavigate: (String) -> Unit
) {
    val themeMode by ThemeManager.currentThemeMode.collectAsState()
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemDark
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    val sections = listOf(
        UtilitySection(
            title = "Nutrição & Culinária".localized(),
            items = listOf(
                UtilityItem(
                    title = "Receitas Culinárias".localized(),
                    subtitle = "Dietas brandas, caldos renais, refeições cozidas e snacks".localized(),
                    icon = Icons.Default.Restaurant,
                    iconColor = Color(0xFFF59E0B),
                    route = "recipes"
                ),
                UtilityItem(
                    title = "Transição Alimentar".localized(),
                    subtitle = "Plano gradual de 7 dias para troca segura de ração".localized(),
                    icon = Icons.Default.Sync,
                    iconColor = Color(0xFF10B981),
                    route = "food_transition"
                ),
                UtilityItem(
                    title = "Calculadora de Fluidos".localized(),
                    subtitle = "Cálculo de reposição de desidratação e manutenção".localized(),
                    icon = Icons.Default.Opacity,
                    iconColor = Color(0xFF0284C7),
                    route = "hydration"
                ),
                UtilityItem(
                    title = "Dieta Crua (BARF)".localized(),
                    subtitle = "Proporções biológicas de ossos carnudos, carne, vísceras e vegetais".localized(),
                    icon = Icons.Default.Spa,
                    iconColor = Color(0xFF22C55E),
                    route = "barf"
                ),
                UtilityItem(
                    title = "Analisador de Rótulos".localized(),
                    subtitle = "Cálculo de Carboidratos Ocultos (NFE), Matéria Seca e Alergénios".localized(),
                    icon = Icons.Default.PieChart,
                    iconColor = Color(0xFFA855F7),
                    route = "label_analyzer"
                )
            )
        ),
        UtilitySection(
            title = "Calculadoras Clínicas & Fisiologia".localized(),
            items = listOf(
                UtilityItem(
                    title = "Calculadora Farmacológica".localized(),
                    subtitle = "Cálculo de dosagem por kg de peso e concentração".localized(),
                    icon = Icons.Default.Medication,
                    iconColor = Color(0xFFD946EF),
                    route = "med_calculator"
                ),
                UtilityItem(
                    title = "Temporizador de Jejum".localized(),
                    subtitle = "Controlo pré-operatório e exames com cortes de água/comida".localized(),
                    icon = Icons.Default.Timer,
                    iconColor = Color(0xFFEF4444),
                    route = "fasting_timer"
                ),
                UtilityItem(
                    title = "Conversão de Idade".localized(),
                    subtitle = "Tabela de equivalência e fases biológicas da vida (AAHA)".localized(),
                    icon = Icons.Default.People,
                    iconColor = Color(0xFF6366F1),
                    route = "age_conversion"
                ),
                UtilityItem(
                    title = "Avaliador Corporal (BCS)".localized(),
                    subtitle = "Escala WSAVA de 1 a 9 com silhuetas e recomendações".localized(),
                    icon = Icons.Default.FitnessCenter,
                    iconColor = Color(0xFFF59E0B),
                    route = "bcs_evaluator"
                )
            )
        ),
        UtilitySection(
            title = "Acompanhamento Clínico & Evolução".localized(),
            items = listOf(
                UtilityItem(
                    title = "Evolução de Peso & Gráficos".localized(),
                    subtitle = "Curva ponderal interativa com linha de peso alvo e metas".localized(),
                    icon = Icons.Default.AutoGraph,
                    iconColor = Color(0xFF0D9488),
                    route = "health_charts"
                ),
                UtilityItem(
                    title = "Fotos & Comparador Antes/Depois".localized(),
                    subtitle = "Galeria clínica com slider de cicatrização e perda de peso".localized(),
                    icon = Icons.Default.CameraAlt,
                    iconColor = Color(0xFFF59E0B),
                    route = "evolution_photos"
                ),
                UtilityItem(
                    title = "Diário Clínico de Sintomas".localized(),
                    subtitle = "Registo digestivo com escala Bristol/Purina e alertas".localized(),
                    icon = Icons.Default.Healing,
                    iconColor = Color(0xFFE11D48),
                    route = "symptom_journal"
                )
            )
        ),
        UtilitySection(
            title = "Rotina & Enciclopédia de Raças".localized(),
            items = listOf(
                UtilityItem(
                    title = "Passeio & Atividade".localized(),
                    subtitle = "Cálculo de calorias gastas (MET) e tempo em tempo real".localized(),
                    icon = Icons.Default.DirectionsWalk,
                    iconColor = Color(0xFF06B6D4),
                    route = "walk_tracker"
                ),
                UtilityItem(
                    title = "Enciclopédia de Raças".localized(),
                    subtitle = "Catálogo completo com filtros de porte, energia e sensibilidades".localized(),
                    icon = Icons.Default.MenuBook,
                    iconColor = Color(0xFF3B82F6),
                    route = "breed_encyclopedia"
                ),
                UtilityItem(
                    title = "Quiz de Raça Ideal".localized(),
                    subtitle = "Descubra a raça ideal para o seu estilo de vida e habitação".localized(),
                    icon = Icons.Default.Psychology,
                    iconColor = Color(0xFF8B5CF6),
                    route = "breed_quiz"
                ),
                UtilityItem(
                    title = "Comparador de Raças".localized(),
                    subtitle = "Comparação lado a lado de parâmetros de duas raças".localized(),
                    icon = Icons.Default.Compare,
                    iconColor = Color(0xFF10B981),
                    route = "breed_comparison"
                )
            )
        ),
        UtilitySection(
            title = "Conectividade & Multiplataforma".localized(),
            items = listOf(
                UtilityItem(
                    title = "Sincronizar com Computador".localized(),
                    subtitle = "Transfira e sincronize os seus pets com a versão Windows pela rede Wi-Fi.".localized(),
                    icon = Icons.Default.Computer,
                    iconColor = Color(0xFF0D9488),
                    route = "desktop_sync"
                )
            )
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Large iOS Style Title
        item {
            Text(
                text = "Utilitários".localized(),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 34.sp
                ),
                modifier = Modifier.padding(start = 4.dp, top = 12.dp, bottom = 14.dp)
            )
        }

        // Hero Card: Central de Utilitários Veterinários
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = null,
                            tint = TealLight,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Central de Utilitários Veterinários".localized(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Calculadoras clínicas, nutrição, temporizadores e ferramentas essenciais para o acompanhamento diário do seu pet.".localized(),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                            lineHeight = 20.sp
                        )
                    )
                }
            }
        }

        // Sections
        sections.forEach { section ->
            item {
                Text(
                    text = section.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        fontSize = 17.sp
                    ),
                    modifier = Modifier.padding(start = 6.dp, top = 22.dp, bottom = 8.dp)
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        section.items.forEachIndexed { index, item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigate(item.route) }
                                    .padding(horizontal = 16.dp, vertical = 13.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Squircle icon container (darker rounded box like iOS)
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isDark) Color(0xFF262626) else item.iconColor.copy(alpha = 0.12f),
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = null,
                                            tint = item.iconColor,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = item.subtitle,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                            lineHeight = 16.sp,
                                            fontSize = 13.sp
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            if (index < section.items.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(start = 74.dp, end = 16.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom spacing so items aren't obscured by the bottom navigation bar
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
