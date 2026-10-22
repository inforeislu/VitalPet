package com.inforeis.vitalpet.models

import com.inforeis.vitalpet.services.LanguageManager.localized

enum class Species(val id: String) {
    DOG("dog"),
    CAT("cat");

    val displayName: String
        get() = when (this) {
            DOG -> "Cão".localized()
            CAT -> "Gato".localized()
        }

    fun displayName(lang: String): String = when (this) {
        DOG -> "Cão".localized(lang)
        CAT -> "Gato".localized(lang)
    }

    companion object {
        fun fromString(value: String): Species =
            entries.find { it.name.equals(value, ignoreCase = true) || it.id.equals(value, ignoreCase = true) } ?: DOG
    }
}

enum class NeuterStatus(val id: String) {
    NEUTERED("neutered"),
    INTACT("intact");

    val displayName: String
        get() = when (this) {
            NEUTERED -> "Esterilizado / Castrado".localized()
            INTACT -> "Inteiro".localized()
        }

    fun displayName(lang: String): String = when (this) {
        NEUTERED -> "Esterilizado / Castrado".localized(lang)
        INTACT -> "Inteiro".localized(lang)
    }

    companion object {
        fun fromString(value: String): NeuterStatus =
            entries.find { it.name.equals(value, ignoreCase = true) || it.id.equals(value, ignoreCase = true) } ?: NEUTERED
    }
}

enum class ActivityLevel(val id: String) {
    WEIGHT_LOSS("weightLoss"),
    SEDENTARY("sedentary"),
    NORMAL("normal"),
    ACTIVE("active"),
    WEIGHT_GAIN("weightGain");

    val displayName: String
        get() = when (this) {
            WEIGHT_LOSS -> "Perda de Peso (Restrição)".localized()
            SEDENTARY -> "Sedentário / Baixa Atividade".localized()
            NORMAL -> "Atividade Normal (Manutenção)".localized()
            ACTIVE -> "Muito Ativo / Trabalho".localized()
            WEIGHT_GAIN -> "Ganho de Peso (Recuperação)".localized()
        }

    fun displayName(lang: String): String = when (this) {
        WEIGHT_LOSS -> "Perda de Peso (Restrição)".localized(lang)
        SEDENTARY -> "Sedentário / Baixa Atividade".localized(lang)
        NORMAL -> "Atividade Normal (Manutenção)".localized(lang)
        ACTIVE -> "Muito Ativo / Trabalho".localized(lang)
        WEIGHT_GAIN -> "Ganho de Peso (Recuperação)".localized(lang)
    }

    /**
     * Fator DER (Daily Energy Requirement) multiplicador do RER segundo diretrizes WSAVA/NRC
     */
    fun factor(species: Species, neuterStatus: NeuterStatus): Double = when (species) {
        Species.DOG -> when (this) {
            NORMAL -> if (neuterStatus == NeuterStatus.NEUTERED) 1.6 else 1.8
            SEDENTARY -> 1.2
            ACTIVE -> 2.0
            WEIGHT_LOSS -> 1.0
            WEIGHT_GAIN -> 1.4
        }
        Species.CAT -> when (this) {
            NORMAL -> if (neuterStatus == NeuterStatus.NEUTERED) 1.2 else 1.4
            SEDENTARY -> 1.0
            ACTIVE -> 1.6
            WEIGHT_LOSS -> 0.8
            WEIGHT_GAIN -> 1.3
        }
    }

    companion object {
        fun fromString(value: String): ActivityLevel =
            entries.find { it.name.equals(value, ignoreCase = true) || it.id.equals(value, ignoreCase = true) } ?: NORMAL
    }
}

enum class PetGender(val id: String, val symbol: String) {
    MALE("male", "♂"),
    FEMALE("female", "♀");

    val displayName: String
        get() = when (this) {
            MALE -> "Macho".localized()
            FEMALE -> "Fêmea".localized()
        }

    fun displayName(lang: String): String = when (this) {
        MALE -> "Macho".localized(lang)
        FEMALE -> "Fêmea".localized(lang)
    }

    companion object {
        fun fromString(value: String): PetGender =
            entries.find { it.name.equals(value, ignoreCase = true) || it.id.equals(value, ignoreCase = true) } ?: MALE
    }
}

enum class LifeStage(val rawValue: String) {
    PUPPY_KITTEN("Filhote"),
    YOUNG_ADULT("Jovem Adulto"),
    MATURE_ADULT("Adulto Maduro"),
    SENIOR("Sénior"),
    GERIATRIC("Geriátrico"),
    SUPER_GERIATRIC("Super Geriátrico");

    val displayName: String
        get() = rawValue.localized()
}

enum class DogSizeCategory(val rawValue: String) {
    SMALL("Pequeno (<10 kg)"),
    MEDIUM("Médio (10-25 kg)"),
    LARGE("Grande (25-45 kg)"),
    GIANT("Gigante (>45 kg)");

    val displayName: String
        get() = rawValue.localized()

    companion object {
        fun from(weightKg: Double): DogSizeCategory = when {
            weightKg < 10.0 -> SMALL
            weightKg <= 25.0 -> MEDIUM
            weightKg <= 45.0 -> LARGE
            else -> GIANT
        }
    }
}
