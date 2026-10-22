package com.inforeis.vitalpet.ui.views

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inforeis.vitalpet.services.EmergencyProtocolsDatabase
import com.inforeis.vitalpet.services.LanguageManager.localized
import com.inforeis.vitalpet.ui.theme.AccentRose

enum class EmergencySection(val shortTitleKey: String, val fullTitleKey: String) {
    HOSPITALS("Veterinários", "Veterinários & Urgências"),
    TOXIC_GUIDE("Tóxicos", "Toxidez & Envenenamento"),
    PROTOCOLS("Socorros", "Primeiros Socorros");

    val shortTitle: String
        get() = shortTitleKey.localized()

    val fullTitle: String
        get() = fullTitleKey.localized()
}

@Composable
fun EmergencyProtocolsScreen(
    initialSection: EmergencySection = EmergencySection.TOXIC_GUIDE,
    onOpenToxicGuide: (() -> Unit)? = null,
    onOpenEmergencyMap: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var selectedSection by remember { mutableStateOf(initialSection) }
    var toxicSearchQuery by remember { mutableStateOf("") }
    var protocolSearchQuery by remember { mutableStateOf("") }
    val sosPhone = "+352 112"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // iOS Style Header: Title + Emergency Dial Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = selectedSection.fullTitle,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(10.dp))

            // SOS Dial Pill Button (e.g. +352 112)
            Surface(
                shape = CircleShape,
                color = Color(0xFFE53935),
                modifier = Modifier.clickable {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${sosPhone.replace(" ", "")}"))
                    context.startActivity(intent)
                }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = sosPhone,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Search Bar (Shown when in Toxic Guide or Protocols)
        if (selectedSection == EmergencySection.TOXIC_GUIDE || selectedSection == EmergencySection.PROTOCOLS) {
            val query = if (selectedSection == EmergencySection.TOXIC_GUIDE) toxicSearchQuery else protocolSearchQuery
            val placeholder = if (selectedSection == EmergencySection.TOXIC_GUIDE) {
                "Pesquisar alimento, planta ou medicamento...".localized()
            } else {
                "Pesquisar protocolos de primeiros socorros...".localized()
            }

            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = {
                        if (selectedSection == EmergencySection.TOXIC_GUIDE) {
                            toxicSearchQuery = it
                        } else {
                            protocolSearchQuery = it
                        }
                    },
                    placeholder = {
                        Text(
                            text = placeholder,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = {
                                if (selectedSection == EmergencySection.TOXIC_GUIDE) {
                                    toxicSearchQuery = ""
                                } else {
                                    protocolSearchQuery = ""
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Limpar",
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF007AFF),
                        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Top 3-Way Segmented Control: [ Veterinários | Tóxicos | Socorros ]
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(3.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                EmergencySection.entries.forEach { section ->
                    val isSelected = selectedSection == section
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(9.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.surface
                                else Color.Transparent
                            )
                            .clickable { selectedSection = section }
                            .padding(vertical = 7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = section.shortTitle,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.onSurface
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // Dynamic Content Area
        Box(modifier = Modifier.weight(1f)) {
            when (selectedSection) {
                EmergencySection.HOSPITALS -> {
                    VeterinaryEmergencyMapScreen(onBack = null)
                }
                EmergencySection.TOXIC_GUIDE -> {
                    ToxicFoodGuideContent(
                        showHeader = false,
                        sosPhone = sosPhone,
                        searchQuery = toxicSearchQuery,
                        onSearchQueryChange = { toxicSearchQuery = it }
                    )
                }
                EmergencySection.PROTOCOLS -> {
                    FirstAidProtocolsContent(searchQuery = protocolSearchQuery)
                }
            }
        }
    }
}

@Composable
fun FirstAidProtocolsContent(searchQuery: String = "") {
    var expandedProtocolId by remember { mutableStateOf<String?>(null) }

    val filteredProtocols = remember(searchQuery) {
        val q = searchQuery.trim().lowercase()
        EmergencyProtocolsDatabase.protocols.filter { protocol ->
            q.isEmpty() ||
                    protocol.title.lowercase().contains(q) ||
                    protocol.title.localized().lowercase().contains(q) ||
                    protocol.symptoms.any { it.lowercase().contains(q) || it.localized().lowercase().contains(q) }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Protocolos de Primeiros Socorros".localized(),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            )
        }

        items(filteredProtocols, key = { it.id }) { protocol ->
            val isExpanded = expandedProtocolId == protocol.id
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedProtocolId = if (isExpanded) null else protocol.id }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = protocol.title.localized(),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = CircleShape,
                            color = AccentRose.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = protocol.urgency.label.localized(),
                                color = AccentRose,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    if (isExpanded) {
                        Spacer(modifier = Modifier.height(14.dp))

                        Text("Sintomas Críticos:".localized(), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        protocol.symptoms.forEach { sym ->
                            Text("• ${sym.localized()}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f))
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            "O que fazer de imediato:".localized(),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        protocol.steps.forEachIndexed { idx, stp ->
                            Text("${idx + 1}. ${stp.localized()}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f))
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("NÃO FAZER:".localized(), fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = AccentRose)
                        Spacer(modifier = Modifier.height(4.dp))
                        protocol.doNotDo.forEach { dnd ->
                            Text("❌ ${dnd.localized()}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f))
                        }
                    } else {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Toque para expandir passos de socorro...".localized(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}
