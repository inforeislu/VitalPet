package com.inforeis.vitalpet.models

import com.inforeis.vitalpet.services.LanguageManager.localized
import java.util.Date
import java.util.UUID

enum class PhotoCategory(val key: String) {
    WEIGHT_PROGRESS("Evolução de Peso"),
    WOUND_HEALING("Cicatrização"),
    DERMATOLOGY("Dermatologia / Pele"),
    GENERAL("Geral");

    val displayName: String
        get() = key.localized()
}

data class EvolutionPhoto(
    val id: String = UUID.randomUUID().toString(),
    val date: Date = Date(),
    val photoUri: String,
    val category: PhotoCategory = PhotoCategory.GENERAL,
    val notes: String? = null
)
