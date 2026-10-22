package com.inforeis.vitalpet.services

import com.inforeis.vitalpet.models.Species
import kotlin.math.pow

data class BarfDistribution(
    val totalDailyGrams: Double,
    val muscleMeatGrams: Double,
    val rawMeatyBonesGrams: Double,
    val organGrams: Double,
    val liverGrams: Double,
    val vegetableGrams: Double,
    val fruitGrams: Double
)

data class FoodLabelAnalysis(
    val crudeProtein: Double,
    val crudeFat: Double,
    val crudeFiber: Double,
    val moisture: Double,
    val crudeAsh: Double,
    val dryMatterPercent: Double,
    val dryMatterProtein: Double,
    val dryMatterFat: Double,
    val dryMatterFiber: Double,
    val dryMatterCarbs: Double, // NFE
    val calculatedKcalPer100g: Double
)

data class FluidRequirement(
    val maintenanceMlPerDay: Double,
    val dehydrationDeficitMl: Double,
    val ongoingLossesMl: Double,
    val totalFirst24HoursMl: Double,
    val hourlyInfusionRateMl: Double
)

object ClinicalCalculatorService {
    /**
     * Calcula distribuição BARF (Bones and Raw Food)
     * Para cães: 2% a 3% do peso corporal
     * 50% carne muscular, 17% ossos carnudos, 10% vísceras (5% fígado), 15% vegetais, 5% fruta
     * Para gatos: carnívoros estritos (mais proteína e vísceras)
     */
    fun calculateBarf(weightKg: Double, percentOfWeight: Double = 2.5, isCat: Boolean = false): BarfDistribution {
        val totalDaily = weightKg * 1000.0 * (percentOfWeight / 100.0)
        return if (isCat) {
            val muscle = totalDaily * 0.65
            val bones = totalDaily * 0.15
            val liver = totalDaily * 0.05
            val otherOrgans = totalDaily * 0.10
            val veg = totalDaily * 0.05
            BarfDistribution(totalDaily, muscle, bones, otherOrgans, liver, veg, 0.0)
        } else {
            val muscle = totalDaily * 0.50
            val bones = totalDaily * 0.17
            val liver = totalDaily * 0.05
            val otherOrgans = totalDaily * 0.08
            val veg = totalDaily * 0.15
            val fruit = totalDaily * 0.05
            BarfDistribution(totalDaily, muscle, bones, otherOrgans, liver, veg, fruit)
        }
    }

    /**
     * Analisador de Rótulo de Ração: Matéria Seca (DM) e NFE (Carboidratos Ocultos)
     */
    fun analyzeFoodLabel(
        crudeProtein: Double,
        crudeFat: Double,
        crudeFiber: Double,
        moisture: Double,
        crudeAsh: Double = 7.5
    ): FoodLabelAnalysis {
        val dryMatter = maxOf(1.0, 100.0 - moisture)
        val factor = 100.0 / dryMatter

        // NFE (Extrato Não Nitrogenado / Carboidratos) no rótulo tal como vendido
        val carbsAsFed = maxOf(0.0, 100.0 - (crudeProtein + crudeFat + crudeFiber + moisture + crudeAsh))
        val dmCarbs = carbsAsFed * factor
        val dmProtein = crudeProtein * factor
        val dmFat = crudeFat * factor
        val dmFiber = crudeFiber * factor

        // Fatores de Atwater Modificados para pets: Proteína: 3.5 kcal/g, Gordura: 8.5 kcal/g, Carboidrato: 3.5 kcal/g
        val kcalPer100g = (crudeProtein * 3.5) + (crudeFat * 8.5) + (carbsAsFed * 3.5)

        return FoodLabelAnalysis(
            crudeProtein, crudeFat, crudeFiber, moisture, crudeAsh,
            dryMatter, dmProtein, dmFat, dmFiber, dmCarbs, kcalPer100g
        )
    }

    /**
     * Fluidoterapia: Manutenção + Défice de Desidratação + Perdas Contínuas
     */
    fun calculateFluids(
        weightKg: Double,
        dehydrationPercent: Double, // ex: 5.0 para 5%
        ongoingLossesMl: Double = 0.0,
        species: Species = Species.DOG
    ): FluidRequirement {
        // Manutenção estándar WSAVA: Cão 60 ml/kg/dia, Gato 45 ml/kg/dia
        val maintenanceFactor = if (species == Species.CAT) 45.0 else 60.0
        val maintenance = weightKg * maintenanceFactor
        val deficit = (dehydrationPercent / 100.0) * weightKg * 1000.0
        val total24h = maintenance + deficit + ongoingLossesMl
        val hourlyRate = total24h / 24.0

        return FluidRequirement(maintenance, deficit, ongoingLossesMl, total24h, hourlyRate)
    }

    /**
     * Cálculo de Dosagem Farmacológica: Dose (mg) = Peso (kg) * Dose Recomendada (mg/kg)
     */
    fun calculateMedicationDose(
        weightKg: Double,
        doseMgPerKg: Double,
        concentrationMgPerMl: Double? = null,
        tabletStrengthMg: Double? = null
    ): Pair<Double, String> {
        val totalDoseMg = weightKg * doseMgPerKg
        val explanation = StringBuilder("Dose calculada: %.2f mg".format(totalDoseMg))

        if (concentrationMgPerMl != null && concentrationMgPerMl > 0) {
            val volumeMl = totalDoseMg / concentrationMgPerMl
            explanation.append(" (%.2f mL da suspensão oral)".format(volumeMl))
        } else if (tabletStrengthMg != null && tabletStrengthMg > 0) {
            val tablets = totalDoseMg / tabletStrengthMg
            explanation.append(" (~%.2f comprimido(s) de %.0f mg)".format(tablets, tabletStrengthMg))
        }

        return Pair(totalDoseMg, explanation.toString())
    }
}
