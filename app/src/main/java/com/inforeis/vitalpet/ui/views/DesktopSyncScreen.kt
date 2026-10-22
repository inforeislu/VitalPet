package com.inforeis.vitalpet.ui.views

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inforeis.vitalpet.services.DiscoveredServer
import com.inforeis.vitalpet.services.LanguageManager.localized
import com.inforeis.vitalpet.services.SyncResult
import com.inforeis.vitalpet.services.VitalPetSyncClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesktopSyncScreen(onBack: () -> Unit) {
    var ipInput by remember { mutableStateOf("") }
    var pinInput by remember { mutableStateOf("") }
    var isSyncing by remember { mutableStateOf(false) }
    var isDiscovering by remember { mutableStateOf(false) }
    var discoveredServers by remember { mutableStateOf<List<DiscoveredServer>>(emptyList()) }
    var syncResult by remember { mutableStateOf<SyncResult?>(null) }
    val scope = rememberCoroutineScope()

    fun triggerDiscovery() {
        isDiscovering = true
        scope.launch {
            discoveredServers = VitalPetSyncClient.discoverLocalServers(2000)
            isDiscovering = false
        }
    }

    LaunchedEffect(Unit) {
        triggerDiscovery()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sincronizar com Computador".localized(), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Computer, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "VitalPet Windows Sync",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Transfira e sincronize os seus pets com a versão Windows pela rede Wi-Fi.".localized(),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Instruções
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Wifi, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Como funciona a sincronização?".localized(),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("1. Abra o VitalPet no seu computador Windows.".localized(), fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("2. Verifique se o telemóvel e o PC estão no mesmo Wi-Fi.".localized(), fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("3. Introduza o IP ou leia o QR Code apresentado no ecrã do PC.".localized(), fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Descoberta Automática de Computador na Rede
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lan, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Computadores Encontrados na Rede:".localized(),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        if (isDiscovering) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        } else {
                            IconButton(onClick = { triggerDiscovery() }) {
                                Icon(Icons.Default.Refresh, contentDescription = "Procurar Computador na Rede".localized())
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (isDiscovering && discoveredServers.isEmpty()) {
                        Text(
                            text = "Procurando computadores na rede...".localized(),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else if (discoveredServers.isEmpty()) {
                        Text(
                            text = "Nenhum computador encontrado na rede.".localized(),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        discoveredServers.forEach { server ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(server.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("${server.host}:${server.port}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("Disponível na rede local".localized(), fontSize = 11.sp, color = Color(0xFF10B981))
                                    }
                                    Button(
                                        onClick = {
                                            ipInput = "${server.host}:${server.port}"
                                            pinInput = server.token
                                            isSyncing = true
                                            syncResult = null
                                            scope.launch {
                                                val result = VitalPetSyncClient.syncWithDesktop(server.host, server.port, server.token)
                                                syncResult = result
                                                isSyncing = false
                                            }
                                        },
                                        enabled = !isSyncing,
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                    ) {
                                        Text("Conectar e Sincronizar".localized(), fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Formulário de Conexão
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Dados do Computador".localized(),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = ipInput,
                        onValueChange = { input ->
                            ipInput = input
                            // Suporte a colar o link do QR Code diretamente
                            val parsed = VitalPetSyncClient.parseQrCode(input)
                            if (parsed != null) {
                                ipInput = "${parsed.first}:${parsed.second}"
                                if (parsed.third.isNotBlank()) {
                                    pinInput = parsed.third
                                }
                            }
                        },
                        label = { Text("Endereço IP do Computador".localized()) },
                        placeholder = { Text("Ex: 192.168.1.15 ou código QR".localized()) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = { pinInput = it },
                        label = { Text("PIN de Autorização (Opcional)".localized()) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (ipInput.isNotBlank()) {
                                isSyncing = true
                                syncResult = null
                                scope.launch {
                                    val parsed = VitalPetSyncClient.parseQrCode(ipInput)
                                    val host = parsed?.first ?: ipInput.substringBefore(":")
                                    val port = parsed?.second ?: ipInput.substringAfter(":", "8920").toIntOrNull() ?: 8920
                                    val token = if (pinInput.isNotBlank()) pinInput else (parsed?.third ?: "")

                                    val result = VitalPetSyncClient.syncWithDesktop(host, port, token)
                                    syncResult = result
                                    isSyncing = false
                                }
                            }
                        },
                        enabled = !isSyncing && ipInput.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Sincronizando dados com o PC...".localized())
                        } else {
                            Icon(Icons.Default.Sync, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sincronizar Agora".localized(), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Resultado da Sincronização
            syncResult?.let { res ->
                val isSuccess = res.success
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSuccess) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFFEF4444).copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSuccess) Color(0xFF10B981) else Color(0xFFEF4444)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                            contentDescription = null,
                            tint = if (isSuccess) Color(0xFF10B981) else Color(0xFFEF4444),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isSuccess) "Sincronização Concluída com Sucesso!".localized() else "Falha na sincronização. Verifique a rede Wi-Fi e o IP do PC.".localized(),
                                fontWeight = FontWeight.Bold,
                                color = if (isSuccess) Color(0xFF10B981) else Color(0xFFEF4444)
                            )
                            if (isSuccess) {
                                Text(
                                    text = "Animais sincronizados:".localized() + " ${res.totalPetsSynced}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
