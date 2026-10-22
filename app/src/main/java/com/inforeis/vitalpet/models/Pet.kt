package com.inforeis.vitalpet.models

import com.inforeis.vitalpet.services.LanguageManager.localized
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.math.pow

data class Pet(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val species: Species,
    val breed: String,
    val birthDate: Date,
    val currentWeightKg: Double,
    val gender: PetGender = PetGender.MALE,
    val targetWeightKg: Double? = null,
    val bcs: Int = 5, // Escala 1 a 9
    val neuterStatus: NeuterStatus = NeuterStatus.NEUTERED,
    val activityLevel: ActivityLevel = ActivityLevel.NORMAL,
    val notes: String? = null,
    val microchipNumber: String? = null,
    val passportNumber: String? = null,
    val insurancePolicy: String? = null,
    val clinicContact: String? = null,
    val allergies: List<String> = emptyList(),
    val chronicConditions: List<String> = emptyList(),
    val photoUri: String? = null,
    val weightLogs: MutableList<WeightLog> = mutableListOf(),
    val dietPlans: MutableList<DietPlan> = mutableListOf(),
    val medications: MutableList<Medication> = mutableListOf(),
    val vaccineLogs: MutableList<VaccineLog> = mutableListOf(),
    val dewormingLogs: MutableList<DewormingLog> = mutableListOf(),
    val medicalVisits: MutableList<MedicalVisit> = mutableListOf(),
    val symptomLogs: MutableList<SymptomLog> = mutableListOf(),
    val fastingLogs: MutableList<FastingLog> = mutableListOf(),
    val walkSessions: MutableList<WalkSession> = mutableListOf(),
    val evolutionPhotos: MutableList<EvolutionPhoto> = mutableListOf()
) {
    /**
     * Idade em anos cronológicos
     */
    val ageInYears: Int
        get() {
            val now = Calendar.getInstance()
            val dob = Calendar.getInstance()
            dob.time = birthDate
            var years = now.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
            if (now.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                years--
            }
            return maxOf(0, years)
        }

    /**
     * Categoria de porte para cães
     */
    val dogSizeCategory: DogSizeCategory
        get() = DogSizeCategory.from(currentWeightKg)

    /**
     * RER (Resting Energy Requirement) segundo fórmula padrão WSAVA:
     * RER = 70 * (peso em kg ^ 0.75)
     */
    val rer: Double
        get() = 70.0 * currentWeightKg.pow(0.75)

    /**
     * DER (Daily Energy Requirement) segundo fórmula padrão WSAVA:
     * DER = RER * Fator de Atividade
     */
    val der: Double
        get() = rer * activityLevel.factor(species, neuterStatus)

    /**
     * Idade equivalente em anos humanos segundo diretrizes AAHA / WSAVA
     */
    val humanEquivalentAge: Int
        get() {
            val years = ageInYears
            if (years == 0) return 1
            if (species == Species.CAT) {
                return when (years) {
                    1 -> 15
                    2 -> 24
                    else -> 24 + (years - 2) * 4
                }
            } else {
                return when (dogSizeCategory) {
                    DogSizeCategory.SMALL -> when (years) {
                        1 -> 15
                        2 -> 24
                        else -> 24 + (years - 2) * 4
                    }
                    DogSizeCategory.MEDIUM -> when (years) {
                        1 -> 15
                        2 -> 24
                        else -> 24 + (years - 2) * 5
                    }
                    DogSizeCategory.LARGE -> when (years) {
                        1 -> 14
                        2 -> 22
                        else -> 22 + (years - 2) * 7
                    }
                    DogSizeCategory.GIANT -> when (years) {
                        1 -> 12
                        2 -> 22
                        else -> 22 + (years - 2) * 8
                    }
                }
            }
        }

    /**
     * Etapa de vida biológica segundo diretrizes AAHA / WSAVA
     */
    val lifeStage: LifeStage
        get() {
            val years = ageInYears
            if (species == Species.CAT) {
                return when {
                    years < 1 -> LifeStage.PUPPY_KITTEN
                    years <= 2 -> LifeStage.YOUNG_ADULT
                    years <= 6 -> LifeStage.MATURE_ADULT
                    years <= 10 -> LifeStage.SENIOR
                    years <= 14 -> LifeStage.GERIATRIC
                    else -> LifeStage.SUPER_GERIATRIC
                }
            } else {
                return when (dogSizeCategory) {
                    DogSizeCategory.SMALL, DogSizeCategory.MEDIUM -> when {
                        years < 1 -> LifeStage.PUPPY_KITTEN
                        years <= 2 -> LifeStage.YOUNG_ADULT
                        years <= 6 -> LifeStage.MATURE_ADULT
                        years <= 10 -> LifeStage.SENIOR
                        years <= 14 -> LifeStage.GERIATRIC
                        else -> LifeStage.SUPER_GERIATRIC
                    }
                    DogSizeCategory.LARGE -> when {
                        years < 1 -> LifeStage.PUPPY_KITTEN
                        years <= 2 -> LifeStage.YOUNG_ADULT
                        years <= 5 -> LifeStage.MATURE_ADULT
                        years <= 8 -> LifeStage.SENIOR
                        else -> LifeStage.GERIATRIC
                    }
                    DogSizeCategory.GIANT -> when {
                        years < 1 -> LifeStage.PUPPY_KITTEN
                        years <= 2 -> LifeStage.YOUNG_ADULT
                        years <= 4 -> LifeStage.MATURE_ADULT
                        years <= 7 -> LifeStage.SENIOR
                        else -> LifeStage.GERIATRIC
                    }
                }
            }
        }

    /**
     * Classificação da condição corporal segundo escala BCS (1-9)
     */
    val bcsCategory: String
        get() = when (bcs) {
            in 1..3 -> "Abaixo do Peso Ideal".localized()
            in 4..5 -> "Peso Ideal".localized()
            in 6..7 -> "Acima do Peso".localized()
            in 8..9 -> "Obesidade".localized()
            else -> "Desconhecido".localized()
        }

    /**
     * Relatório Clínico Formatado
     */
    fun getFormattedClinicalReport(lang: String = "pt"): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val sb = StringBuilder()
        sb.append("🏥 RELATÓRIO CLÍNICO & NUTRICIONAL VITALPET\n")
        sb.append("-------------------------------------------------\n")
        sb.append("Nome: $name\n")
        sb.append("Espécie: ${species.displayName(lang)} | Sexo: ${gender.displayName(lang)} (${gender.symbol}) | Raça: $breed\n")
        sb.append("Idade: $ageInYears ano(s) (${dateFormat.format(birthDate)})\n")
        sb.append("Estado Reprodutivo: ${neuterStatus.displayName(lang)}\n")
        sb.append("Nível de Atividade: ${activityLevel.displayName(lang)}\n\n")

        sb.append("📊 BIOMETRIA & METABOLISMO (WSAVA / NRC)\n")
        sb.append("• Peso Atual: %.2f kg\n".format(currentWeightKg))
        if (targetWeightKg != null) {
            sb.append("• Peso Alvo: %.2f kg\n".format(targetWeightKg))
        }
        sb.append("• Score Corporal (BCS): $bcs/9 ($bcsCategory)\n")
        sb.append("• RER (Taxa Metabólica Basal): %.0f kcal/dia\n".format(rer))
        sb.append("• DER (Necessidade Calórica Diária): %.0f kcal/dia\n\n".format(der))

        sb.append("🍽️ PLANOS NUTRICIONAIS ATIVOS\n")
        if (dietPlans.isEmpty()) {
            sb.append("• Nenhum plano registado.\n")
        } else {
            for (p in dietPlans) {
                sb.append("• ${p.foodName}: %.0f g/dia (%.0f kcal/100g)\n".format(p.dailyPortionGrams, p.kcalPer100g))
            }
        }

        sb.append("\n💊 MEDICAÇÃO ATIVA\n")
        val activeMeds = medications.filter { it.isActive }
        if (activeMeds.isEmpty()) {
            sb.append("• Sem medicações ativas registadas.\n")
        } else {
            for (m in activeMeds) {
                sb.append("• ${m.name} (${m.dosage}) - ${m.frequency}\n")
            }
        }

        sb.append("\n💉 VACINAÇÃO\n")
        if (vaccineLogs.isEmpty()) {
            sb.append("• Sem vacinas registadas.\n")
        } else {
            for (v in vaccineLogs.sortedByDescending { it.applicationDate }) {
                val due = v.nextDueDate?.let { " | Próxima: ${dateFormat.format(it)}" } ?: ""
                sb.append("• ${v.vaccineName} (${dateFormat.format(v.applicationDate)})$due\n")
            }
        }

        sb.append("\n🛡️ DESPARASITAÇÃO\n")
        if (dewormingLogs.isEmpty()) {
            sb.append("• Sem desparasitações registadas.\n")
        } else {
            for (d in dewormingLogs.sortedByDescending { it.applicationDate }) {
                val due = d.nextDueDate?.let { " | Próxima: ${dateFormat.format(it)}" } ?: ""
                sb.append("• ${d.productName} [${d.type.displayName(lang)}] (${dateFormat.format(d.applicationDate)})$due\n")
            }
        }

        if (!notes.isNullOrEmpty()) {
            sb.append("\n📝 NOTAS CLÍNICAS\n$notes\n")
        }
        if (!microchipNumber.isNullOrEmpty()) {
            sb.append("\n🏷️ MICROCHIP: $microchipNumber\n")
        }
        if (allergies.isNotEmpty()) {
            sb.append("\n⚠️ ALERGIAS: ${allergies.joinToString(", ")}\n")
        }
        if (chronicConditions.isNotEmpty()) {
            sb.append("\n🩺 CONDIÇÕES CRÓNICAS: ${chronicConditions.joinToString(", ")}\n")
        }

        sb.append("\n-------------------------------------------------\nGerado por VitalPet — Suporte Clínico Nativo Android")
        return sb.toString()
    }
}
