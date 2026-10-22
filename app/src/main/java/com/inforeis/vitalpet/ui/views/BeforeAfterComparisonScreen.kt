package com.inforeis.vitalpet.ui.views

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inforeis.vitalpet.models.EvolutionPhoto
import com.inforeis.vitalpet.models.Pet
import com.inforeis.vitalpet.models.PhotoCategory
import com.inforeis.vitalpet.services.LanguageManager.localized
import com.inforeis.vitalpet.services.PhotoStorageManager
import com.inforeis.vitalpet.services.PetRepository
import com.inforeis.vitalpet.ui.theme.TealPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeforeAfterComparisonScreen(
    initialPetId: String? = null,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val pets by PetRepository.pets.collectAsState()
    var selectedPetId by remember(pets, initialPetId) {
        mutableStateOf(initialPetId ?: pets.firstOrNull()?.id ?: "")
    }
    val activePet = pets.find { it.id == selectedPetId } ?: pets.firstOrNull()

    var selectedModeIndex by remember { mutableIntStateOf(0) }
    val modes = listOf("Galeria de Fotos".localized(), "Comparador Antes/Depois".localized())

    var selectedCategory by remember { mutableStateOf<PhotoCategory?>(null) }
    var showAddPhotoDialog by remember { mutableStateOf(false) }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null && activePet != null) {
            val savedUri = PhotoStorageManager.savePhotoFromUri(context, uri)
            if (savedUri != null) {
                val newPhoto = EvolutionPhoto(
                    photoUri = savedUri,
                    category = selectedCategory ?: PhotoCategory.GENERAL,
                    date = Date()
                )
                activePet.evolutionPhotos.add(newPhoto)
                PetRepository.updatePet(activePet)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fotos de Evolução & Comparador".localized(), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar".localized())
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            if (activePet != null) {
                FloatingActionButton(
                    onClick = { photoPickerLauncher.launch("image/*") },
                    containerColor = TealPrimary,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.AddAPhoto, contentDescription = "Adicionar Foto de Evolução".localized())
                }
            }
        }
    ) { padding ->
        if (pets.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Registe primeiro um pet para aceder à galeria clínica.".localized(),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Pet Selector
            if (pets.size > 1) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(pets) { pet ->
                        val isSelected = pet.id == activePet?.id
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedPetId = pet.id },
                            label = { Text(pet.name) },
                            leadingIcon = if (isSelected) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TealPrimary.copy(alpha = 0.15f),
                                selectedLabelColor = TealPrimary
                            )
                        )
                    }
                }
            }

            // Mode Selector
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                modes.forEachIndexed { index, label ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = modes.size),
                        onClick = { selectedModeIndex = index },
                        selected = index == selectedModeIndex
                    ) {
                        Text(label, fontSize = 12.sp)
                    }
                }
            }

            // Category Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("Todas".localized(), fontSize = 12.sp) }
                    )
                }
                items(PhotoCategory.values()) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat.displayName, fontSize = 12.sp) }
                    )
                }
            }

            if (activePet != null) {
                val photos = remember(activePet.evolutionPhotos, selectedCategory) {
                    val list = activePet.evolutionPhotos.sortedByDescending { it.date }
                    if (selectedCategory == null) list else list.filter { it.category == selectedCategory }
                }

                if (selectedModeIndex == 0) {
                    // Gallery Mode
                    if (photos.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Sem fotos registadas nesta categoria.\nToque no botão abaixo para adicionar a primeira foto.".localized(),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(photos) { photo ->
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .aspectRatio(1f)
                                                .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                                        ) {
                                            EvolutionPhotoItem(
                                                photoUri = photo.photoUri,
                                                contentDescription = null,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color.Black.copy(alpha = 0.6f),
                                                modifier = Modifier
                                                    .align(Alignment.BottomStart)
                                                    .padding(6.dp)
                                            ) {
                                                Text(
                                                    text = dateFormat.format(photo.date),
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Text(
                                                text = photo.category.displayName,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TealPrimary
                                            )
                                            if (!photo.notes.isNullOrEmpty()) {
                                                Text(
                                                    text = photo.notes,
                                                    fontSize = 11.sp,
                                                    maxLines = 2,
                                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Split Comparator Mode
                    if (photos.size < 2) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "São necessárias pelo menos 2 fotos para usar o comparador antes e depois.".localized(),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    } else {
                        var beforePhoto by remember(photos) { mutableStateOf(photos.lastOrNull() ?: photos[0]) }
                        var afterPhoto by remember(photos) { mutableStateOf(photos.firstOrNull() ?: photos[0]) }
                        var sliderPosition by remember { mutableFloatStateOf(0.5f) }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Split Slider View
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                            ) {
                                BoxWithConstraints(modifier = Modifier.fillMaxSize().clipToBounds()) {
                                    val totalWidth = maxWidth
                                    val splitWidth = totalWidth * sliderPosition

                                    // After Photo (Background)
                                    EvolutionPhotoItem(
                                        photoUri = afterPhoto.photoUri,
                                        contentDescription = "Depois",
                                        modifier = Modifier.fillMaxSize()
                                    )

                                    // Before Photo (Clipped overlay)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .width(splitWidth)
                                            .clipToBounds()
                                    ) {
                                        EvolutionPhotoItem(
                                            photoUri = beforePhoto.photoUri,
                                            contentDescription = "Antes",
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .width(totalWidth)
                                        )
                                    }

                                    // Divider Line & Handle
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .width(totalWidth)
                                            .pointerInput(Unit) {
                                                detectDragGestures { change, _ ->
                                                    val newPos = (change.position.x / size.width).coerceIn(0.05f, 0.95f)
                                                    sliderPosition = newPos
                                                }
                                            }
                                    ) {
                                        // Line
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .width(3.dp)
                                                .background(Color.White)
                                                .align(Alignment.CenterStart)
                                                .padding(start = splitWidth)
                                        )

                                        // Central Slider Handle Icon
                                        Surface(
                                            shape = CircleShape,
                                            color = TealPrimary,
                                            shadowElevation = 6.dp,
                                            modifier = Modifier
                                                .align(Alignment.CenterStart)
                                                .padding(start = (splitWidth.value - 16).dp)
                                                .size(32.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.UnfoldMore,
                                                contentDescription = "Arrastar",
                                                tint = Color.White,
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .padding(4.dp)
                                            )
                                        }
                                    }

                                    // Badges
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color.Black.copy(alpha = 0.6f),
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .padding(10.dp)
                                    ) {
                                        Text(
                                            text = "Antes: ".localized() + dateFormat.format(beforePhoto.date),
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color.Black.copy(alpha = 0.6f),
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(10.dp)
                                    ) {
                                        Text(
                                            text = "Depois: ".localized() + dateFormat.format(afterPhoto.date),
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            Text(
                                text = "Arraste a divisória branca lateralmente para comparar o progresso clínico.".localized(),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EvolutionPhotoItem(
    photoUri: String,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    val bitmap = remember(photoUri) {
        PhotoStorageManager.loadBitmap(photoUri)
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    } else {
        Box(
            modifier = modifier.background(Color.LightGray.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = Color.Gray)
        }
    }
}
