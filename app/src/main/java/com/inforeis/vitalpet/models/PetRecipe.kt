package com.inforeis.vitalpet.models

import com.inforeis.vitalpet.services.LanguageManager.localized
import java.util.UUID

enum class RecipeCategory(val rawValue: String) {
    BLAND_DIET("Dieta Branda (Gastroenterite)"),
    DAILY_COOKED("Alimentação Cozida Completa"),
    HEALTHY_TREAT("Snacks & Recompensas Saudáveis"),
    RENAL_SUPPORT("Suporte Renal & Caldos");

    val displayName: String
        get() = rawValue.localized()
}

data class PetRecipe(
    val id: String,
    val title: String,
    val category: RecipeCategory,
    val species: Species,
    val prepTimeMinutes: Int,
    val portions: String,
    val ingredients: List<String>,
    val instructions: List<String>,
    val nutritionalNotes: String,
    val warning: String? = null
)

data class CustomPetRecipe(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: RecipeCategory = RecipeCategory.DAILY_COOKED,
    val species: Species = Species.DOG,
    val prepTimeMinutes: Int = 30,
    val portions: String = "2 refeições",
    val ingredients: List<String> = emptyList(),
    val instructions: List<String> = emptyList(),
    val nutritionalNotes: String = ""
)
