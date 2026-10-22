package com.inforeis.vitalpet.models

import com.inforeis.vitalpet.services.LanguageManager.localized

enum class ToxicityLevel(val rawValue: String) {
    EXTREME("Extrema / Letal"),
    HIGH("Alta Gravidade"),
    MODERATE("Moderada"),
    LOW("Leve / Indigestão");

    val displayName: String
        get() = rawValue.localized()
}

typealias ToxicLevel = ToxicityLevel

enum class ToxicCategory(val rawValue: String) {
    FOODS("Alimentos"),
    PLANTS("Plantas & Flores"),
    MEDICATIONS("Medicamentos"),
    CHEMICALS("Químicos Domésticos");

    val displayName: String
        get() = rawValue.localized()
}

data class ToxicFood(
    val name: String,
    val toxicAgent: String,
    val level: ToxicityLevel,
    val category: ToxicCategory = ToxicCategory.FOODS,
    val affectedSpecies: Species? = null,
    val symptoms: String,
    val firstAidAdvice: String
)
