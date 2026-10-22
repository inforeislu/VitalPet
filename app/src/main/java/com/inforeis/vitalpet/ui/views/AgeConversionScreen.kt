package com.inforeis.vitalpet.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.inforeis.vitalpet.models.DogSizeCategory
import com.inforeis.vitalpet.models.Species
import com.inforeis.vitalpet.services.LanguageManager.localized
import com.inforeis.vitalpet.ui.theme.TealPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgeConversionScreen(onBack: () -> Unit) {
    var species by remember { mutableStateOf(Species.DOG) }
    var dogSize by remember { mutableStateOf(DogSizeCategory.MEDIUM) }

    val ageTable = (1..15).map { petYears ->
        val humanYears = if (species == Species.CAT) {
            when (petYears) {
                1 -> 15
                2 -> 24
                else -> 24 + (petYears - 2) * 4
            }
        } else {
            when (dogSize) {
                DogSizeCategory.SMALL -> if (petYears == 1) 15 else if (petYears == 2) 24 else 24 + (petYears - 2) * 4
                DogSizeCategory.MEDIUM -> if (petYears == 1) 15 else if (petYears == 2) 24 else 24 + (petYears - 2) * 5
                DogSizeCategory.LARGE -> if (petYears == 1) 14 else if (petYears == 2) 22 else 22 + (petYears - 2) * 7
                DogSizeCategory.GIANT -> if (petYears == 1) 12 else if (petYears == 2) 22 else 22 + (petYears - 2) * 8
            }
        }
        Pair(petYears, humanYears)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Conversão de Idade".localized(), fontWeight = FontWeight.Bold) },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Species.entries.forEach { sp ->
                    FilterChip(
                        selected = species == sp,
                        onClick = { species = sp },
                        label = { Text(sp.displayName) }
                    )
                }
            }

            if (species == Species.DOG) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DogSizeCategory.entries.forEach { size ->
                        FilterChip(
                            selected = dogSize == size,
                            onClick = { dogSize = size },
                            label = { Text(size.displayName) }
                        )
                    }
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                LazyColumn(modifier = Modifier.padding(14.dp)) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Idade do Pet".localized(), fontWeight = FontWeight.Bold)
                            Text("Equivalência Humana".localized(), fontWeight = FontWeight.Bold, color = TealPrimary)
                        }
                    }
                    items(ageTable) { (petY, humY) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("$petY " + (if (petY == 1) "ano".localized() else "anos".localized()))
                            Text("~$humY " + "anos humanos".localized(), fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}
