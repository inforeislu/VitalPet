package com.inforeis.vitalpet.ui.views

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inforeis.vitalpet.services.LanguageManager.localized
import com.inforeis.vitalpet.services.SubscriptionManager
import com.inforeis.vitalpet.services.SubscriptionPlan
import com.inforeis.vitalpet.ui.theme.AccentAmber
import com.inforeis.vitalpet.ui.theme.TealDark
import com.inforeis.vitalpet.ui.theme.TealPrimary

@Composable
fun PremiumPaywallScreen(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isPremium by SubscriptionManager.isPremium.collectAsState()
    val currentPlan by SubscriptionManager.currentPlan.collectAsState()

    var selectedPlan by remember { mutableStateOf(SubscriptionPlan.YEARLY) }
    var isProcessing by remember { mutableStateOf(false) }

    val goldGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFFD700),
            AccentAmber,
            Color(0xFFF59E0B)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar with Close button
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Hero Premium Header
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = AccentAmber.copy(alpha = 0.15f),
                        modifier = Modifier.size(76.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = AccentAmber,
                                modifier = Modifier.size(46.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "VitalPet Premium".localized(),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Desbloqueie todo o potencial da saúde e nutrição dos seus animais.".localized(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Premium Features list
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        FeatureRow(
                            icon = Icons.Default.Pets,
                            title = "Animais Ilimitados".localized(),
                            subtitle = "Registe e monitorize quantos animais e pacientes desejar sem restrições.".localized()
                        )
                        FeatureRow(
                            icon = Icons.Default.HealthAndSafety,
                            title = "Calculadoras Clínicas Avançadas".localized(),
                            subtitle = "Acesso a dosagens farmacológicas em mg/kg e fluidoterapia de emergência.".localized()
                        )
                        FeatureRow(
                            icon = Icons.Default.Restaurant,
                            title = "Nutrição Especializada & BARF".localized(),
                            subtitle = "Cálculo preciso de dietas naturais BARF e análise de matéria seca e NFE.".localized()
                        )
                        FeatureRow(
                            icon = Icons.Default.Timer,
                            title = "Temporizador Clínico Pré-Operatório".localized(),
                            subtitle = "Monitorização rigorosa das fases de jejum cirúrgico.".localized()
                        )
                        FeatureRow(
                            icon = Icons.Default.PictureAsPdf,
                            title = "Relatórios em PDF".localized(),
                            subtitle = "Exporte relatórios clínicos e nutricionais para o seu veterinário.".localized()
                        )
                        FeatureRow(
                            icon = Icons.Default.CloudDone,
                            title = "Cópias de Segurança & Nuvem".localized(),
                            subtitle = "Proteção contínua e restauro fácil de todos os dados clínicos.".localized()
                        )
                    }
                }
            }

            // Plan Options Section
            item {
                Text(
                    text = "Escolha o seu plano".localized(),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }

            // Yearly Plan (Highlighted)
            item {
                PlanCard(
                    plan = SubscriptionPlan.YEARLY,
                    isSelected = selectedPlan == SubscriptionPlan.YEARLY,
                    badgeText = "${"Mais Popular".localized()} • ${"Poupe 58%".localized()}",
                    badgeColor = AccentAmber,
                    onSelect = { selectedPlan = SubscriptionPlan.YEARLY }
                )
            }

            // Monthly Plan
            item {
                PlanCard(
                    plan = SubscriptionPlan.MONTHLY,
                    isSelected = selectedPlan == SubscriptionPlan.MONTHLY,
                    badgeText = null,
                    badgeColor = TealPrimary,
                    onSelect = { selectedPlan = SubscriptionPlan.MONTHLY }
                )
            }

            // Lifetime Plan
            item {
                PlanCard(
                    plan = SubscriptionPlan.LIFETIME,
                    isSelected = selectedPlan == SubscriptionPlan.LIFETIME,
                    badgeText = "Melhor Investimento".localized(),
                    badgeColor = TealPrimary,
                    onSelect = { selectedPlan = SubscriptionPlan.LIFETIME }
                )
            }

            // Legal & Terms
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = "A assinatura renova-se automaticamente a menos que seja cancelada até 24 horas antes do termo do período atual.".localized(),
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Termos de Utilização".localized(),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TealPrimary
                            ),
                            modifier = Modifier.clickable {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://inforeis.com/terms")
                                )
                                context.startActivity(intent)
                            }
                        )
                        Text(
                            text = " • ",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                        Text(
                            text = "Política de Privacidade".localized(),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TealPrimary
                            ),
                            modifier = Modifier.clickable {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://inforeis.com/privacy")
                                )
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        }

        // Bottom CTA Sticky Bar
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        isProcessing = true
                        SubscriptionManager.purchase(selectedPlan, context as? Activity) { success, errorMsg ->
                            isProcessing = false
                            if (success) {
                                Toast.makeText(
                                    context,
                                    "Compra concluída com sucesso! Bem-vindo ao VitalPet Premium.".localized(),
                                    Toast.LENGTH_LONG
                                ).show()
                                onDismiss()
                            } else if (!errorMsg.isNullOrBlank()) {
                                Toast.makeText(
                                    context,
                                    errorMsg,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedPlan == SubscriptionPlan.YEARLY) AccentAmber else TealPrimary
                    ),
                    enabled = !isProcessing
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        val ctaText = when (selectedPlan) {
                            SubscriptionPlan.YEARLY -> "Começar Teste Grátis de 7 Dias".localized()
                            SubscriptionPlan.MONTHLY -> "Subscrever Plano Mensal".localized()
                            SubscriptionPlan.LIFETIME -> "Desbloquear Acesso Vitalício".localized()
                        }
                        Text(
                            text = ctaText,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                TextButton(
                    onClick = {
                        SubscriptionManager.restorePurchases { success, found ->
                            if (success && found) {
                                Toast.makeText(
                                    context,
                                    "Compras restauradas com sucesso!".localized(),
                                    Toast.LENGTH_SHORT
                                ).show()
                                onDismiss()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Nenhuma compra anterior encontrada para restaurar.".localized(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                ) {
                    Text(
                        text = "Restaurar Compras".localized(),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun FeatureRow(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = CircleShape,
            color = TealPrimary.copy(alpha = 0.12f),
            modifier = Modifier.size(34.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TealPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
            )
        }
    }
}

@Composable
private fun PlanCard(
    plan: SubscriptionPlan,
    isSelected: Boolean,
    badgeText: String?,
    badgeColor: Color,
    onSelect: () -> Unit
) {
    val borderColor = if (isSelected) {
        if (plan == SubscriptionPlan.YEARLY) AccentAmber else TealPrimary
    } else {
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    }

    val backgroundColor = if (isSelected) {
        if (plan == SubscriptionPlan.YEARLY) AccentAmber.copy(alpha = 0.08f) else TealPrimary.copy(alpha = 0.08f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(18.dp)
                )
                .clickable { onSelect() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (isSelected) {
                        if (plan == SubscriptionPlan.YEARLY) AccentAmber else TealPrimary
                    } else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = plan.titleKey.localized(),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = plan.durationKey.localized(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = plan.priceKey.localized(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isSelected && plan == SubscriptionPlan.YEARLY) AccentAmber else TealPrimary
                        )
                    )
                    if (plan.isTrialAvailable) {
                        Text(
                            text = "7 Dias Grátis".localized(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = AccentAmber
                            )
                        )
                    }
                }
            }
        }

        // Top-right pill badge
        if (badgeText != null) {
            Surface(
                shape = RoundedCornerShape(topStart = 0.dp, bottomStart = 8.dp, topEnd = 16.dp, bottomEnd = 0.dp),
                color = badgeColor,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Text(
                    text = badgeText,
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}
