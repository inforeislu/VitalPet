package com.inforeis.vitalpet.models

import com.inforeis.vitalpet.services.LanguageManager.localized
import java.util.Date
import java.util.UUID

enum class AppetiteLevel(val rawValue: String) {
    ANOREXIC("Recusa Total"),
    DECREASED("Diminuído"),
    NORMAL("Normal"),
    INCREASED("Aumentado (Polifagia)");

    val displayName: String
        get() = rawValue.localized()
}

enum class EnergyLevel(val rawValue: String) {
    LETHARGIC("Letárgico / Prostrado"),
    LOW("Baixa Energia"),
    NORMAL("Normal"),
    HIGH("Muito Ativo / Agitado");

    val displayName: String
        get() = rawValue.localized()
}

enum class WaterIntakeLevel(val rawValue: String) {
    DECREASED("Diminuição (Pouca Água)"),
    NORMAL("Normal"),
    INCREASED("Aumento Excessivo (Polidipsia)");

    val displayName: String
        get() = rawValue.localized()
}

data class SymptomLog(
    val id: String = UUID.randomUUID().toString(),
    val date: Date = Date(),
    val fecalScore: Int = 2,
    val vomitingEpisodes: Int = 0,
    val appetite: AppetiteLevel = AppetiteLevel.NORMAL,
    val energyLevel: EnergyLevel = EnergyLevel.NORMAL,
    val waterIntake: WaterIntakeLevel = WaterIntakeLevel.NORMAL,
    val notes: String? = null
) {
    companion object {
        fun fecalScoreDescription(score: Int): String = when (score) {
            1 -> "Score 1: Muito dura e seca (obstipação)".localized()
            2 -> "Score 2: Firme e bem formada (ideal)".localized()
            3 -> "Score 3: Húmida com forma definida".localized()
            4 -> "Score 4: Muito húmida e pastosa".localized()
            5 -> "Score 5: Semiformada e viscosa".localized()
            6 -> "Score 6: Pastosa sem consistência".localized()
            7 -> "Score 7: Diarreia aquosa líquida".localized()
            else -> "Não avaliado".localized()
        }
    }
}
