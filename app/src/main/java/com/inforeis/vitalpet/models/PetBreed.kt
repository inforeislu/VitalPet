package com.inforeis.vitalpet.models

import com.inforeis.vitalpet.services.LanguageManager.localized

enum class BreedSize(val key: String) {
    TOY("Toy / Mini"),
    SMALL("Pequeno"),
    MEDIUM("Médio"),
    LARGE("Grande"),
    GIANT("Gigante");

    val displayName: String
        get() = key.localized()
}

enum class BreedEnergy(val key: String) {
    LOW("Baixa"),
    MODERATE("Moderada"),
    HIGH("Alta"),
    VERY_HIGH("Muito Alta");

    val displayName: String
        get() = key.localized()
}

enum class GroomingNeed(val key: String) {
    LOW("Pouca manutenção"),
    MODERATE("Manutenção moderada"),
    HIGH("Dedicação alta / Tosquias");

    val displayName: String
        get() = key.localized()
}

data class PetBreed(
    val id: String,
    val name: String,
    val species: Species,
    val sizeCategory: DogSizeCategory,
    val minWeightKg: Double,
    val maxWeightKg: Double,
    val description: String,
    val healthRisks: List<String> = emptyList(),
    val expectedLifeYearsMin: Int = 10,
    val expectedLifeYearsMax: Int = 15,
    val energyLevel: BreedEnergy = BreedEnergy.MODERATE,
    val groomingNeeds: GroomingNeed = GroomingNeed.LOW
) {
    val commonSensitivities: List<String>
        get() = healthRisks

    val averageWeightString: String
        get() = "%.1f - %.1f kg".format(minWeightKg, maxWeightKg)
}
