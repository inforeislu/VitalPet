package com.inforeis.vitalpet.models

import com.inforeis.vitalpet.services.LanguageManager.localized
import java.util.Date
import java.util.UUID

enum class DewormingType(val id: String) {
    INTERNAL("internalParasite"),
    EXTERNAL("externalParasite"),
    COMBINED("combined");

    val displayName: String
        get() = when (this) {
            INTERNAL -> "Interno (Vermífugo)".localized()
            EXTERNAL -> "Externo (Pulgas/Carraças)".localized()
            COMBINED -> "Combinado (Interno + Externo)".localized()
        }

    fun displayName(lang: String): String = when (this) {
        INTERNAL -> "Interno (Vermífugo)".localized(lang)
        EXTERNAL -> "Externo (Pulgas/Carraças)".localized(lang)
        COMBINED -> "Combinado (Interno + Externo)".localized(lang)
    }

    companion object {
        fun fromString(str: String): DewormingType =
            entries.find { it.name.equals(str, ignoreCase = true) || it.id.equals(str, ignoreCase = true) } ?: INTERNAL
    }
}

data class DewormingLog(
    val id: String = UUID.randomUUID().toString(),
    val productName: String,
    val type: DewormingType = DewormingType.INTERNAL,
    val applicationDate: Date = Date(),
    val nextDueDate: Date? = null,
    val dosage: String? = null,
    val notes: String? = null
) {
    val isOverdue: Boolean
        get() = nextDueDate != null && nextDueDate.before(Date())
}
