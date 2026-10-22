package com.inforeis.vitalpet.models

import com.inforeis.vitalpet.services.LanguageManager.localized

enum class EmergencyCategory(val rawValue: String) {
    CPR("RCP & Asfixia"),
    TOXICITY("Intoxicação & Envenenamento"),
    TRAUMA("Trauma & Hemorragias"),
    ENVIRONMENT("Golpe de Calor & Hipotermia");

    val displayName: String
        get() = rawValue.localized()
}

enum class UrgencyLevel(val rawValue: String) {
    CRITICAL("CRÍTICO (Imediato)"),
    HIGH("ALTO (Urgente)"),
    MODERATE("MODERADO");

    val label: String
        get() = rawValue.localized()
}

data class EmergencyProtocol(
    val id: String,
    val title: String,
    val category: EmergencyCategory,
    val urgency: UrgencyLevel,
    val symptoms: List<String>,
    val steps: List<String>,
    val doNotDo: List<String>
)
