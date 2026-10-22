package com.inforeis.vitalpet.ui.views

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.inforeis.vitalpet.models.ActivityLevel
import com.inforeis.vitalpet.models.NeuterStatus
import com.inforeis.vitalpet.models.Pet
import com.inforeis.vitalpet.models.PetGender
import com.inforeis.vitalpet.models.Species
import com.inforeis.vitalpet.services.LanguageManager.localized
import com.inforeis.vitalpet.services.PetRepository
import com.inforeis.vitalpet.services.PhotoStorageManager
import com.inforeis.vitalpet.ui.components.PetAvatar
import com.inforeis.vitalpet.ui.theme.TealPrimary
import java.util.Date
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPetScreen(
    petId: String? = null,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val existingPet = petId?.let { PetRepository.getPetById(it) }

    var photoUri by remember { mutableStateOf(existingPet?.photoUri) }
    var name by remember { mutableStateOf(existingPet?.name ?: "") }
    var species by remember { mutableStateOf(existingPet?.species ?: Species.DOG) }
    var breed by remember { mutableStateOf(existingPet?.breed ?: "") }
    var weightText by remember { mutableStateOf(existingPet?.currentWeightKg?.toString() ?: "") }
    var targetWeightText by remember { mutableStateOf(existingPet?.targetWeightKg?.toString() ?: "") }
    var gender by remember { mutableStateOf(existingPet?.gender ?: PetGender.MALE) }
    var neuterStatus by remember { mutableStateOf(existingPet?.neuterStatus ?: NeuterStatus.NEUTERED) }
    var activityLevel by remember { mutableStateOf(existingPet?.activityLevel ?: ActivityLevel.NORMAL) }
    var bcs by remember { mutableFloatStateOf(existingPet?.bcs?.toFloat() ?: 5f) }
    var microchip by remember { mutableStateOf(existingPet?.microchipNumber ?: "") }
    var notes by remember { mutableStateOf(existingPet?.notes ?: "") }
    var allergiesText by remember { mutableStateOf(existingPet?.allergies?.joinToString(", ") ?: "") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val savedPath = PhotoStorageManager.savePhotoFromUri(context, uri)
            if (savedPath != null) {
                photoUri = savedPath
            }
        }
    }

    val isEditing = existingPet != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditing) "Editar Pet".localized() else "Adicionar Pet".localized(),
                        fontWeight = FontWeight.Bold
                    )
                },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Photo Selection Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        PetAvatar(
                            species = species,
                            photoUri = photoUri,
                            size = 104.dp
                        )
                        Surface(
                            shape = CircleShape,
                            color = TealPrimary,
                            contentColor = Color.White,
                            modifier = Modifier
                                .size(34.dp)
                                .clickable {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Alterar Foto".localized(),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Foto do Animal".localized(),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Toque para selecionar uma foto".localized(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                Icons.Default.PhotoLibrary,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = TealPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (photoUri != null) "Alterar Foto".localized() else "Adicionar Foto".localized(),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        if (photoUri != null) {
                            TextButton(
                                onClick = {
                                    PhotoStorageManager.deletePhoto(photoUri)
                                    photoUri = null
                                },
                                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Remover Foto".localized(), style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome do Animal".localized()) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Species selector
            Text("Espécie".localized(), fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Species.entries.forEach { sp ->
                    FilterChip(
                        selected = species == sp,
                        onClick = { species = sp },
                        label = { Text(sp.displayName) }
                    )
                }
            }

            // Gender selector
            Text("Sexo".localized(), fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PetGender.entries.forEach { g ->
                    FilterChip(
                        selected = gender == g,
                        onClick = { gender = g },
                        label = { Text("${g.displayName} (${g.symbol})") }
                    )
                }
            }

            OutlinedTextField(
                value = breed,
                onValueChange = { breed = it },
                label = { Text("Raça".localized()) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it },
                    label = { Text("Peso Atual (kg)".localized()) },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = targetWeightText,
                    onValueChange = { targetWeightText = it },
                    label = { Text("Peso Alvo (kg)".localized()) },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            // BCS Slider
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Condição Corporal (BCS)".localized(), fontWeight = FontWeight.SemiBold)
                    Text("${bcs.toInt()}/9", fontWeight = FontWeight.Bold, color = TealPrimary)
                }
                Slider(
                    value = bcs,
                    onValueChange = { bcs = it },
                    valueRange = 1f..9f,
                    steps = 7
                )
            }

            // Reproductive status
            Text("Estado Reprodutivo".localized(), fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                NeuterStatus.entries.forEach { ns ->
                    FilterChip(
                        selected = neuterStatus == ns,
                        onClick = { neuterStatus = ns },
                        label = { Text(ns.displayName) }
                    )
                }
            }

            OutlinedTextField(
                value = microchip,
                onValueChange = { microchip = it },
                label = { Text("Número do Microchip".localized()) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = allergiesText,
                onValueChange = { allergiesText = it },
                label = { Text("Alergias (separadas por vírgula)".localized()) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notas Clínicas".localized()) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val currentWeight = weightText.toDoubleOrNull() ?: 10.0
                    val targetWeight = targetWeightText.toDoubleOrNull()
                    val allergiesList = allergiesText.split(",").map { it.trim() }.filter { it.isNotEmpty() }

                    val petToSave = Pet(
                        id = existingPet?.id ?: UUID.randomUUID().toString(),
                        name = if (name.isBlank()) "Pet" else name,
                        species = species,
                        breed = if (breed.isBlank()) "SRD" else breed,
                        birthDate = existingPet?.birthDate ?: Date(),
                        currentWeightKg = currentWeight,
                        gender = gender,
                        targetWeightKg = targetWeight,
                        bcs = bcs.toInt(),
                        neuterStatus = neuterStatus,
                        activityLevel = activityLevel,
                        notes = notes,
                        microchipNumber = microchip,
                        allergies = allergiesList,
                        chronicConditions = existingPet?.chronicConditions ?: emptyList(),
                        photoUri = photoUri,
                        weightLogs = existingPet?.weightLogs ?: mutableListOf(),
                        dietPlans = existingPet?.dietPlans ?: mutableListOf(),
                        medications = existingPet?.medications ?: mutableListOf(),
                        vaccineLogs = existingPet?.vaccineLogs ?: mutableListOf(),
                        dewormingLogs = existingPet?.dewormingLogs ?: mutableListOf(),
                        medicalVisits = existingPet?.medicalVisits ?: mutableListOf(),
                        symptomLogs = existingPet?.symptomLogs ?: mutableListOf(),
                        fastingLogs = existingPet?.fastingLogs ?: mutableListOf(),
                        walkSessions = existingPet?.walkSessions ?: mutableListOf()
                    )

                    if (isEditing) {
                        PetRepository.updatePet(petToSave)
                    } else {
                        PetRepository.addPet(petToSave)
                    }
                    onBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                Text(
                    if (isEditing) "Guardar Alterações".localized() else "Registar Animal".localized(),
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
