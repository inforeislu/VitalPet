package com.inforeis.vitalpet.ui.views

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inforeis.vitalpet.models.BreedEnergy
import com.inforeis.vitalpet.models.BreedSize
import com.inforeis.vitalpet.models.DogSizeCategory
import com.inforeis.vitalpet.models.GroomingNeed
import com.inforeis.vitalpet.models.PetBreed
import com.inforeis.vitalpet.models.Species
import com.inforeis.vitalpet.services.BreedDatabase
import com.inforeis.vitalpet.services.LanguageManager.localized
import com.inforeis.vitalpet.ui.theme.AccentAmber
import com.inforeis.vitalpet.ui.theme.AccentBlue
import com.inforeis.vitalpet.ui.theme.TealPrimary
import kotlin.math.roundToInt

enum class QuizHomeType(val titleKey: String, val subtitleKey: String) {
    APARTMENT_SMALL("Apartamento Pequeno", "Espaço mais compacto sem área exterior privativa"),
    APARTMENT("Apartamento Médio / Grande", "Espaço confortável com varanda ou áreas amplas"),
    HOUSE_GARDEN("Moradia com Quintal / Jardim", "Espaço amplo com área exterior vedada para correr")
}

enum class QuizActivityTime(val titleKey: String, val subtitleKey: String) {
    LOW("Menos de 30 minutos / dia", "Passeios rápidos higiénicos e rotina calma em casa"),
    MODERATE("30 a 60 minutos / dia", "Caminhadas diárias ativas e brincadeiras regulares"),
    HIGH("Mais de 1 hora / dia", "Corrida, trilhos ou treino físico intenso diário")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreedMatchQuizScreen(
    onBack: () -> Unit,
    onSelectBreed: (PetBreed) -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }
    var showResults by remember { mutableStateOf(false) }

    var selectedSpecies by remember { mutableStateOf(Species.DOG) }
    var selectedHome by remember { mutableStateOf(QuizHomeType.APARTMENT) }
    var selectedActivity by remember { mutableStateOf(QuizActivityTime.MODERATE) }
    var selectedGrooming by remember { mutableStateOf(GroomingNeed.MODERATE) }

    val scoredBreeds: List<Pair<PetBreed, Int>> = remember(showResults, selectedSpecies, selectedHome, selectedActivity, selectedGrooming) {
        if (!showResults) emptyList()
        else {
            val pool = BreedDatabase.allBreeds.filter { it.species == selectedSpecies }
            pool.map { breed ->
                var score = 100.0

                // Housing vs Size & Energy
                when (selectedHome) {
                    QuizHomeType.APARTMENT_SMALL -> {
                        if (breed.sizeCategory == DogSizeCategory.GIANT || breed.sizeCategory == DogSizeCategory.LARGE) score -= 35
                        if (breed.energyLevel == BreedEnergy.VERY_HIGH) score -= 25
                        if (breed.sizeCategory == DogSizeCategory.SMALL) score += 10
                    }
                    QuizHomeType.APARTMENT -> {
                        if (breed.sizeCategory == DogSizeCategory.GIANT) score -= 20
                        if (breed.energyLevel == BreedEnergy.VERY_HIGH) score -= 15
                    }
                    QuizHomeType.HOUSE_GARDEN -> {
                        if (breed.sizeCategory == DogSizeCategory.LARGE || breed.sizeCategory == DogSizeCategory.GIANT) score += 10
                        if (breed.energyLevel == BreedEnergy.VERY_HIGH || breed.energyLevel == BreedEnergy.HIGH) score += 10
                    }
                }

                // Daily Activity
                when (selectedActivity) {
                    QuizActivityTime.LOW -> {
                        if (breed.energyLevel == BreedEnergy.VERY_HIGH) score -= 40
                        else if (breed.energyLevel == BreedEnergy.HIGH) score -= 25
                        else if (breed.energyLevel == BreedEnergy.LOW) score += 15
                    }
                    QuizActivityTime.MODERATE -> {
                        if (breed.energyLevel == BreedEnergy.MODERATE) score += 15
                        else if (breed.energyLevel == BreedEnergy.VERY_HIGH) score -= 15
                    }
                    QuizActivityTime.HIGH -> {
                        if (breed.energyLevel == BreedEnergy.VERY_HIGH || breed.energyLevel == BreedEnergy.HIGH) score += 20
                        else if (breed.energyLevel == BreedEnergy.LOW) score -= 20
                    }
                }

                // Grooming
                when (selectedGrooming) {
                    GroomingNeed.LOW -> {
                        if (breed.groomingNeeds == GroomingNeed.HIGH) score -= 30
                        else if (breed.groomingNeeds == GroomingNeed.LOW) score += 15
                    }
                    GroomingNeed.MODERATE -> {
                        if (breed.groomingNeeds == GroomingNeed.MODERATE) score += 10
                    }
                    GroomingNeed.HIGH -> {
                        if (breed.groomingNeeds == GroomingNeed.HIGH) score += 15
                    }
                }

                val finalPercentage = score.coerceIn(40.0, 99.0).roundToInt()
                Pair(breed, finalPercentage)
            }.sortedByDescending { it.second }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz de Raça Ideal".localized(), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar".localized())
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        AnimatedContent(
            targetState = showResults,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) { isResults ->
            if (isResults) {
                // Results Screen
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = TealPrimary.copy(alpha = 0.15f),
                                modifier = Modifier.size(64.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(36.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Raças Mais Compatíveis!".localized(),
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Com base no seu perfil de espaço, tempo e rotina diária:".localized(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    items(scoredBreeds.take(6)) { item ->
                        val breed = item.first
                        val affinity = item.second
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectBreed(breed) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (breed.species == Species.DOG) TealPrimary.copy(alpha = 0.15f) else AccentAmber.copy(alpha = 0.15f),
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.Pets,
                                            contentDescription = null,
                                            tint = if (breed.species == Species.DOG) TealPrimary else AccentAmber,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = breed.name.localized(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "${breed.sizeCategory.displayName} • ${breed.averageWeightString}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "$affinity%",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp,
                                        color = if (affinity >= 85) Color(0xFF10B981) else AccentAmber
                                    )
                                    Text(
                                        text = "Afinidade".localized(),
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        OutlinedButton(
                            onClick = {
                                currentStep = 0
                                showResults = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Refazer Quiz".localized())
                        }
                    }
                }
            } else {
                // Quiz Questions
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header progress
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = TealPrimary.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "PERGUNTA ${currentStep + 1} DE 4".localized(),
                            color = TealPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    when (currentStep) {
                        0 -> {
                            // Step 1: Species
                            Text(
                                text = "Qual a espécie de companhia desejada?".localized(),
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "O perfil ideal de atividade e cuidados varia consoante a espécie.".localized(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )

                            QuizOptionCard(
                                title = "Cão (Canino)".localized(),
                                subtitle = "Companheiro para passeios diários e treinos ao ar livre".localized(),
                                isSelected = selectedSpecies == Species.DOG,
                                onClick = { selectedSpecies = Species.DOG }
                            )

                            QuizOptionCard(
                                title = "Gato (Felino)".localized(),
                                subtitle = "Companheiro independente e adaptável para viver no interior".localized(),
                                isSelected = selectedSpecies == Species.CAT,
                                onClick = { selectedSpecies = Species.CAT }
                            )
                        }
                        1 -> {
                            // Step 2: Home Type
                            Text(
                                text = "Como é o seu espaço habitacional?".localized(),
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "O porte e nível de energia do pet devem harmonizar com o seu lar.".localized(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )

                            QuizHomeType.values().forEach { home ->
                                QuizOptionCard(
                                    title = home.titleKey.localized(),
                                    subtitle = home.subtitleKey.localized(),
                                    isSelected = selectedHome == home,
                                    onClick = { selectedHome = home }
                                )
                            }
                        }
                        2 -> {
                            // Step 3: Daily Activity
                            Text(
                                text = "Quanto tempo diário tem para passeios e brincadeiras?".localized(),
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Pets com alta energia necessitam de estímulo motor para evitar ansiedade.".localized(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )

                            QuizActivityTime.values().forEach { activity ->
                                QuizOptionCard(
                                    title = activity.titleKey.localized(),
                                    subtitle = activity.subtitleKey.localized(),
                                    isSelected = selectedActivity == activity,
                                    onClick = { selectedActivity = activity }
                                )
                            }
                        }
                        3 -> {
                            // Step 4: Grooming
                            Text(
                                text = "Qual a sua tolerância a escovagem e cuidados com o pelo?".localized(),
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Algumas raças exigem escovagens diárias e tosquias regulares.".localized(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )

                            QuizOptionCard(
                                title = "Pouca manutenção".localized(),
                                subtitle = "Prefiro pelo curto e escovagens rápidas e raras".localized(),
                                isSelected = selectedGrooming == GroomingNeed.LOW,
                                onClick = { selectedGrooming = GroomingNeed.LOW }
                            )
                            QuizOptionCard(
                                title = "Manutenção moderada".localized(),
                                subtitle = "Posso escovar 1 a 2 vezes por semana sem problema".localized(),
                                isSelected = selectedGrooming == GroomingNeed.MODERATE,
                                onClick = { selectedGrooming = GroomingNeed.MODERATE }
                            )
                            QuizOptionCard(
                                title = "Dedicação alta / Tosquias".localized(),
                                subtitle = "Gosto de cuidar do pelo e ir ao pet shop regularmente".localized(),
                                isSelected = selectedGrooming == GroomingNeed.HIGH,
                                onClick = { selectedGrooming = GroomingNeed.HIGH }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (currentStep > 0) {
                            OutlinedButton(
                                onClick = { currentStep-- },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Anterior".localized())
                            }
                        } else {
                            Spacer(modifier = Modifier.width(1.dp))
                        }

                        Button(
                            onClick = {
                                if (currentStep < 3) {
                                    currentStep++
                                } else {
                                    showResults = true
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                        ) {
                            Text(if (currentStep < 3) "Seguinte".localized() else "Ver Resultados".localized())
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuizOptionCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) TealPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (isSelected) TealPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = if (isSelected) TealPrimary else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                )
            }
        }
    }
}
