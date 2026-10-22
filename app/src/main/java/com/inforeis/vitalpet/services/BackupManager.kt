package com.inforeis.vitalpet.services

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.inforeis.vitalpet.models.Pet
import java.io.File
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DataIntegrityReport(
    val totalPets: Int,
    val totalWeightLogs: Int,
    val totalDietPlans: Int,
    val totalMedications: Int,
    val totalVaccineLogs: Int,
    val totalDewormingLogs: Int,
    val totalMedicalVisits: Int,
    val totalSymptomLogs: Int,
    val totalFastingLogs: Int,
    val totalWalkSessions: Int,
    val totalClinicalRecords: Int,
    val databaseSizeBytes: Long,
    val databaseSizeFormatted: String,
    val lastBackupFormatted: String,
    val issuesFound: List<String>,
    val isHealthy: Boolean,
    val integrityScore: Int
)

data class BackupPayload(
    val app: String = "VitalPet",
    val version: String = "1.0",
    val exportTimestamp: Long = System.currentTimeMillis(),
    val exportDate: String,
    val totalPets: Int,
    val pets: List<Pet>
)

object BackupManager {
    private const val PREFS_BACKUP = "vitalpet_backup_meta"
    private const val KEY_LAST_BACKUP = "last_backup_timestamp"
    val gson: Gson = VitalPetJson.gson

    fun performIntegrityCheck(context: Context): DataIntegrityReport {
        val pets = PetRepository.pets.value
        val issues = mutableListOf<String>()

        var totalWeight = 0
        var totalDiet = 0
        var totalMeds = 0
        var totalVaccines = 0
        var totalDeworming = 0
        var totalVisits = 0
        var totalSymptoms = 0
        var totalFasting = 0
        var totalWalks = 0

        for (pet in pets) {
            if (pet.name.isBlank()) {
                issues.add("Animal com ID ${pet.id.take(6)} sem nome definido.")
            }
            if (pet.currentWeightKg <= 0.0) {
                issues.add("Animal ${pet.name} tem peso inválido (${pet.currentWeightKg} kg).")
            }
            if (pet.bcs !in 1..9) {
                issues.add("Animal ${pet.name} tem BCS fora da escala 1-9 (${pet.bcs}).")
            }
            totalWeight += pet.weightLogs.size
            totalDiet += pet.dietPlans.size
            totalMeds += pet.medications.size
            totalVaccines += pet.vaccineLogs.size
            totalDeworming += pet.dewormingLogs.size
            totalVisits += pet.medicalVisits.size
            totalSymptoms += pet.symptomLogs.size
            totalFasting += pet.fastingLogs.size
            totalWalks += pet.walkSessions.size
        }

        val totalRecords = totalWeight + totalDiet + totalMeds + totalVaccines + totalDeworming +
                totalVisits + totalSymptoms + totalFasting + totalWalks

        val repoPrefs = context.getSharedPreferences("vitalpet_repo_prefs", Context.MODE_PRIVATE)
        val jsonSize = repoPrefs.getString("stored_pets", "")?.toByteArray(Charsets.UTF_8)?.size ?: 0
        val dbSizeBytes = jsonSize.toLong()

        val dbSizeFormatted = when {
            dbSizeBytes >= 1024 * 1024 -> String.format(Locale.US, "%.2f MB", dbSizeBytes / (1024f * 1024f))
            dbSizeBytes >= 1024 -> String.format(Locale.US, "%.1f KB", dbSizeBytes / 1024f)
            else -> "$dbSizeBytes B"
        }

        val backupPrefs = context.getSharedPreferences(PREFS_BACKUP, Context.MODE_PRIVATE)
        val lastTimestamp = backupPrefs.getLong(KEY_LAST_BACKUP, 0L)
        val lastBackupFormatted = if (lastTimestamp > 0L) {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            sdf.format(Date(lastTimestamp))
        } else {
            "Nunca realizada"
        }

        val isHealthy = issues.isEmpty()
        val integrityScore = if (isHealthy) 100 else maxOf(0, 100 - (issues.size * 15))

        return DataIntegrityReport(
            totalPets = pets.size,
            totalWeightLogs = totalWeight,
            totalDietPlans = totalDiet,
            totalMedications = totalMeds,
            totalVaccineLogs = totalVaccines,
            totalDewormingLogs = totalDeworming,
            totalMedicalVisits = totalVisits,
            totalSymptomLogs = totalSymptoms,
            totalFastingLogs = totalFasting,
            totalWalkSessions = totalWalks,
            totalClinicalRecords = totalRecords,
            databaseSizeBytes = dbSizeBytes,
            databaseSizeFormatted = dbSizeFormatted,
            lastBackupFormatted = lastBackupFormatted,
            issuesFound = issues,
            isHealthy = isHealthy,
            integrityScore = integrityScore
        )
    }

    fun exportBackup(context: Context): File? {
        return try {
            val pets = PetRepository.pets.value
            val sdf = SimpleDateFormat("yyyy-MM-dd_HHmmss", Locale.US)
            val dateStr = sdf.format(Date())
            val isoDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date())

            val payload = BackupPayload(
                exportDate = isoDate,
                totalPets = pets.size,
                pets = pets
            )

            val json = gson.toJson(payload)

            val backupDir = File(context.cacheDir, "backups")
            if (!backupDir.exists()) backupDir.mkdirs()

            val file = File(backupDir, "vitalpet_backup_$dateStr.json")
            file.writeText(json, Charsets.UTF_8)

            // Record timestamp
            val backupPrefs = context.getSharedPreferences(PREFS_BACKUP, Context.MODE_PRIVATE)
            backupPrefs.edit().putLong(KEY_LAST_BACKUP, System.currentTimeMillis()).apply()

            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun shareBackup(context: Context): Boolean {
        val file = exportBackup(context) ?: return false
        return try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Cópia de Segurança VitalPet")
                putExtra(Intent.EXTRA_TEXT, "Ficheiro de backup com dados clínicos e nutricionais dos animais.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(intent, "Partilhar Cópia de Segurança VitalPet")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun importBackup(context: Context, uri: Uri): Result<Int> {
        return try {
            val json = context.contentResolver.openInputStream(uri)?.use { stream ->
                InputStreamReader(stream, Charsets.UTF_8).readText()
            } ?: return Result.failure(Exception("Não foi possível ler o ficheiro."))

            val importedPets = parsePetsFromJson(json)

            if (importedPets.isEmpty()) {
                return Result.failure(Exception("Ficheiro vazio ou formato inválido."))
            }

            for (p in importedPets) {
                val sanitized = sanitizePet(p)
                val existing = PetRepository.getPetById(sanitized.id)
                if (existing != null) {
                    PetRepository.updatePet(sanitized)
                } else {
                    PetRepository.addPet(sanitized)
                }
            }

            Result.success(importedPets.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun parsePetsFromJson(json: String): List<Pet> {
        return try {
            val element = com.google.gson.JsonParser.parseString(json)
            if (element.isJsonObject) {
                val obj = element.asJsonObject
                val array = when {
                    obj.has("pets") && obj.get("pets").isJsonArray -> obj.getAsJsonArray("pets")
                    obj.has("animals") && obj.get("animals").isJsonArray -> obj.getAsJsonArray("animals")
                    obj.has("records") && obj.get("records").isJsonArray -> obj.getAsJsonArray("records")
                    else -> null
                }
                if (array != null) {
                    val type = object : TypeToken<List<Pet>>() {}.type
                    val list: List<Pet> = gson.fromJson(array, type) ?: emptyList()
                    list.map { sanitizePet(it) }
                } else {
                    emptyList()
                }
            } else if (element.isJsonArray) {
                val type = object : TypeToken<List<Pet>>() {}.type
                val list: List<Pet> = gson.fromJson(element.asJsonArray, type) ?: emptyList()
                list.map { sanitizePet(it) }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun sanitizePet(p: Pet): Pet {
        return p.copy(
            weightLogs = (p.weightLogs ?: mutableListOf()).toMutableList(),
            dietPlans = (p.dietPlans ?: mutableListOf()).toMutableList(),
            medications = (p.medications ?: mutableListOf()).toMutableList(),
            vaccineLogs = (p.vaccineLogs ?: mutableListOf()).toMutableList(),
            dewormingLogs = (p.dewormingLogs ?: mutableListOf()).toMutableList(),
            medicalVisits = (p.medicalVisits ?: mutableListOf()).toMutableList(),
            symptomLogs = (p.symptomLogs ?: mutableListOf()).toMutableList(),
            fastingLogs = (p.fastingLogs ?: mutableListOf()).toMutableList(),
            walkSessions = (p.walkSessions ?: mutableListOf()).toMutableList(),
            evolutionPhotos = (p.evolutionPhotos ?: mutableListOf()).toMutableList(),
            allergies = p.allergies ?: emptyList(),
            chronicConditions = p.chronicConditions ?: emptyList()
        )
    }
}
