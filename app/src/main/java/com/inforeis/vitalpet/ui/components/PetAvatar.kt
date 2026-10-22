package com.inforeis.vitalpet.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.inforeis.vitalpet.models.Species
import com.inforeis.vitalpet.services.PhotoStorageManager
import com.inforeis.vitalpet.ui.theme.AccentAmber
import com.inforeis.vitalpet.ui.theme.TealLight
import com.inforeis.vitalpet.ui.theme.TealPrimary

@Composable
fun PetAvatar(
    species: Species,
    photoUri: String? = null,
    size: Dp = 48.dp,
    modifier: Modifier = Modifier
) {
    val bitmap = remember(photoUri) {
        PhotoStorageManager.loadBitmap(photoUri)
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap,
            contentDescription = species.name,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
        )
    } else {
        val gradient = Brush.linearGradient(
            colors = if (species == Species.DOG) {
                listOf(TealPrimary, TealLight)
            } else {
                listOf(AccentAmber, Color(0xFFFB923C))
            }
        )

        Box(
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(gradient)
                .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Pets,
                contentDescription = species.name,
                tint = Color.White,
                modifier = Modifier.size(size * 0.55f)
            )
        }
    }
}
