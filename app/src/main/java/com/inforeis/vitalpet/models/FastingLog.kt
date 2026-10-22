package com.inforeis.vitalpet.models

import com.inforeis.vitalpet.services.LanguageManager.localized
import java.util.Calendar
import java.util.Date
import java.util.UUID

enum class FastingProcedure(val rawValue: String) {
    GENERAL_SURGERY("Cirurgia Geral / Anestesia"),
    ABDOMINAL_ULTRASOUND("Ecografia Abdominal"),
    BLOOD_TESTS("Análises de Sangue"),
    DENTAL_CLEANING("Destartarização / Procedimento Dentário"),
    ENDOSCOPY("Endoscopia Digestiva"),
    CUSTOM("Personalizado");

    val displayName: String
        get() = rawValue.localized()

    val defaultFoodHours: Int
        get() = when (this) {
            GENERAL_SURGERY, DENTAL_CLEANING -> 8
            ABDOMINAL_ULTRASOUND -> 8
            BLOOD_TESTS -> 8
            ENDOSCOPY -> 12
            CUSTOM -> 8
        }

    val defaultWaterHours: Int
        get() = when (this) {
            GENERAL_SURGERY, DENTAL_CLEANING -> 2
            ABDOMINAL_ULTRASOUND -> 1
            BLOOD_TESTS -> 0
            ENDOSCOPY -> 4
            CUSTOM -> 2
        }
}

enum class FastingStage(val rawValue: String) {
    BEFORE_FASTING("Alimentação Normal"),
    WATER_ONLY("Jejum Sólido (Água Permitida)"),
    TOTAL_FASTING("Jejum Total Obrigatório"),
    READY("Pronto para o Procedimento"),
    COMPLETED("Procedimento Realizado");

    val displayName: String
        get() = rawValue.localized()
}

data class FastingLog(
    val id: String = UUID.randomUUID().toString(),
    val procedure: FastingProcedure = FastingProcedure.GENERAL_SURGERY,
    val customProcedureName: String? = null,
    val procedureDate: Date,
    val foodFastingHours: Int = 8,
    val waterFastingHours: Int = 2,
    val isCompleted: Boolean = false,
    val notes: String? = null
) {
    val procedureDisplayName: String
        get() = if (procedure == FastingProcedure.CUSTOM && !customProcedureName.isNullOrEmpty()) {
            customProcedureName
        } else {
            procedure.displayName
        }

    val foodCutoffDate: Date
        get() {
            val cal = Calendar.getInstance()
            cal.time = procedureDate
            cal.add(Calendar.HOUR_OF_DAY, -foodFastingHours)
            return cal.time
        }

    val waterCutoffDate: Date
        get() {
            val cal = Calendar.getInstance()
            cal.time = procedureDate
            cal.add(Calendar.HOUR_OF_DAY, -waterFastingHours)
            return cal.time
        }

    fun currentStage(now: Date = Date()): FastingStage {
        if (isCompleted) return FastingStage.COMPLETED
        if (now.after(procedureDate) || now == procedureDate) return FastingStage.READY
        if (now.after(waterCutoffDate) || now == waterCutoffDate) return FastingStage.TOTAL_FASTING
        if (now.after(foodCutoffDate) || now == foodCutoffDate) return FastingStage.WATER_ONLY
        return FastingStage.BEFORE_FASTING
    }
}
