package com.inforeis.vitalpet.sync

import com.inforeis.vitalpet.models.DewormingLog
import com.inforeis.vitalpet.models.DietPlan
import com.inforeis.vitalpet.models.EvolutionPhoto
import com.inforeis.vitalpet.models.FastingLog
import com.inforeis.vitalpet.models.MedicalVisit
import com.inforeis.vitalpet.models.Medication
import com.inforeis.vitalpet.models.Pet
import com.inforeis.vitalpet.models.SymptomLog
import com.inforeis.vitalpet.models.VaccineLog
import com.inforeis.vitalpet.models.WalkSession
import com.inforeis.vitalpet.models.WeightLog

data class SyncPayload(
    val deviceId: String,
    val deviceName: String,
    val platform: String, // "windows", "android", "ios"
    val timestamp: Long = System.currentTimeMillis(),
    val appVersion: String = "1.0",
    val authToken: String = "",
    val pets: List<Pet>
)

data class SyncResponse(
    val success: Boolean,
    val message: String,
    val addedPets: Int,
    val updatedPets: Int,
    val totalPets: Int,
    val serverTimestamp: Long = System.currentTimeMillis(),
    val pets: List<Pet> = emptyList()
)

data class MergeResult(
    val mergedPets: List<Pet>,
    val addedCount: Int,
    val updatedCount: Int
)

object PetSyncEngine {

    /**
     * Mescla as listas de animais de dois dispositivos (Desktop e Mobile).
     * Não apaga dados; combina registos clínicos, vacinas, pesos e fotos sem duplicar.
     */
    fun merge(localPets: List<Pet>, incomingPets: List<Pet>): MergeResult {
        val resultMap = LinkedHashMap<String, Pet>()
        var added = 0
        var updated = 0

        for (pet in localPets) {
            resultMap[pet.id] = pet
        }

        for (incoming in incomingPets) {
            val existing = resultMap[incoming.id]
            if (existing == null) {
                resultMap[incoming.id] = incoming
                added++
            } else {
                val mergedPet = mergeSinglePet(existing, incoming)
                resultMap[incoming.id] = mergedPet
                updated++
            }
        }

        return MergeResult(
            mergedPets = resultMap.values.toList(),
            addedCount = added,
            updatedCount = updated
        )
    }

    private fun mergeSinglePet(existing: Pet, incoming: Pet): Pet {
        val mergedWeights = mergeWeightLogs(existing.weightLogs, incoming.weightLogs)
        val mergedVaccines = mergeVaccineLogs(existing.vaccineLogs, incoming.vaccineLogs)
        val mergedDeworming = mergeDewormingLogs(existing.dewormingLogs, incoming.dewormingLogs)
        val mergedVisits = mergeMedicalVisits(existing.medicalVisits, incoming.medicalVisits)
        val mergedMeds = mergeMedications(existing.medications, incoming.medications)
        val mergedSymptoms = mergeSymptomLogs(existing.symptomLogs, incoming.symptomLogs)
        val mergedFasting = mergeFastingLogs(existing.fastingLogs, incoming.fastingLogs)
        val mergedWalks = mergeWalkSessions(existing.walkSessions, incoming.walkSessions)
        val mergedDiets = mergeDietPlans(existing.dietPlans, incoming.dietPlans)
        val mergedPhotos = mergeEvolutionPhotos(existing.evolutionPhotos, incoming.evolutionPhotos)

        val latestWeight = mergedWeights.maxByOrNull { it.date.time }?.weightKg ?: incoming.currentWeightKg

        return existing.copy(
            name = incoming.name.ifBlank { existing.name },
            species = incoming.species,
            breed = incoming.breed.ifBlank { existing.breed },
            currentWeightKg = latestWeight,
            targetWeightKg = incoming.targetWeightKg ?: existing.targetWeightKg,
            bcs = if (incoming.bcs in 1..9) incoming.bcs else existing.bcs,
            neuterStatus = incoming.neuterStatus,
            activityLevel = incoming.activityLevel,
            notes = incoming.notes ?: existing.notes,
            microchipNumber = incoming.microchipNumber ?: existing.microchipNumber,
            passportNumber = incoming.passportNumber ?: existing.passportNumber,
            insurancePolicy = incoming.insurancePolicy ?: existing.insurancePolicy,
            clinicContact = incoming.clinicContact ?: existing.clinicContact,
            allergies = (existing.allergies + incoming.allergies).distinct(),
            chronicConditions = (existing.chronicConditions + incoming.chronicConditions).distinct(),
            photoUri = incoming.photoUri ?: existing.photoUri,
            weightLogs = mergedWeights.toMutableList(),
            dietPlans = mergedDiets.toMutableList(),
            medications = mergedMeds.toMutableList(),
            vaccineLogs = mergedVaccines.toMutableList(),
            dewormingLogs = mergedDeworming.toMutableList(),
            medicalVisits = mergedVisits.toMutableList(),
            symptomLogs = mergedSymptoms.toMutableList(),
            fastingLogs = mergedFasting.toMutableList(),
            walkSessions = mergedWalks.toMutableList(),
            evolutionPhotos = mergedPhotos.toMutableList()
        )
    }

    private fun mergeWeightLogs(listA: List<WeightLog>, listB: List<WeightLog>): List<WeightLog> {
        val map = LinkedHashMap<String, WeightLog>()
        for (w in listA + listB) {
            val key = "${w.date.time / 86400000}_${w.weightKg}"
            map[key] = w
        }
        return map.values.sortedBy { it.date }
    }

    private fun mergeVaccineLogs(listA: List<VaccineLog>, listB: List<VaccineLog>): List<VaccineLog> {
        val map = LinkedHashMap<String, VaccineLog>()
        for (v in listA + listB) {
            val key = "${v.vaccineName}_${v.applicationDate.time / 86400000}"
            map[key] = v
        }
        return map.values.sortedByDescending { it.applicationDate }
    }

    private fun mergeDewormingLogs(listA: List<DewormingLog>, listB: List<DewormingLog>): List<DewormingLog> {
        val map = LinkedHashMap<String, DewormingLog>()
        for (d in listA + listB) {
            val key = "${d.productName}_${d.applicationDate.time / 86400000}"
            map[key] = d
        }
        return map.values.sortedByDescending { it.applicationDate }
    }

    private fun mergeMedicalVisits(listA: List<MedicalVisit>, listB: List<MedicalVisit>): List<MedicalVisit> {
        val map = LinkedHashMap<String, MedicalVisit>()
        for (v in listA + listB) {
            val key = "${v.reason}_${v.date.time / 86400000}"
            map[key] = v
        }
        return map.values.sortedByDescending { it.date }
    }

    private fun mergeMedications(listA: List<Medication>, listB: List<Medication>): List<Medication> {
        val map = LinkedHashMap<String, Medication>()
        for (m in listA + listB) {
            val key = "${m.name}_${m.startDate.time / 86400000}"
            map[key] = m
        }
        return map.values.toList()
    }

    private fun mergeSymptomLogs(listA: List<SymptomLog>, listB: List<SymptomLog>): List<SymptomLog> {
        val map = LinkedHashMap<String, SymptomLog>()
        for (s in listA + listB) {
            val key = "${s.fecalScore}_${s.date.time / 3600000}"
            map[key] = s
        }
        return map.values.sortedByDescending { it.date }
    }

    private fun mergeFastingLogs(listA: List<FastingLog>, listB: List<FastingLog>): List<FastingLog> {
        val map = LinkedHashMap<String, FastingLog>()
        for (f in listA + listB) {
            val key = "${f.procedure.name}_${f.procedureDate.time / 3600000}"
            map[key] = f
        }
        return map.values.sortedByDescending { it.procedureDate }
    }

    private fun mergeWalkSessions(listA: List<WalkSession>, listB: List<WalkSession>): List<WalkSession> {
        val map = LinkedHashMap<String, WalkSession>()
        for (w in listA + listB) {
            val key = "${w.date.time / 60000}_${w.durationSeconds}"
            map[key] = w
        }
        return map.values.sortedByDescending { it.date }
    }

    private fun mergeDietPlans(listA: List<DietPlan>, listB: List<DietPlan>): List<DietPlan> {
        val map = LinkedHashMap<String, DietPlan>()
        for (d in listA + listB) {
            val key = "${d.foodName}_${d.targetDailyKcal}"
            map[key] = d
        }
        return map.values.toList()
    }

    private fun mergeEvolutionPhotos(listA: List<EvolutionPhoto>, listB: List<EvolutionPhoto>): List<EvolutionPhoto> {
        val map = LinkedHashMap<String, EvolutionPhoto>()
        for (p in listA + listB) {
            val key = "${p.date.time / 86400000}_${p.category.name}"
            map[key] = p
        }
        return map.values.sortedByDescending { it.date }
    }
}
