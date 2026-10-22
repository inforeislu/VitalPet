package com.inforeis.vitalpet.services

import com.google.gson.reflect.TypeToken
import com.inforeis.vitalpet.models.Pet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

object DesktopPetRepository {
    private val gson = VitalPetJson.gson

    private val storageDir: File by lazy {
        val os = System.getProperty("os.name").lowercase()
        val dir = when {
            os.contains("win") -> {
                val appData = System.getenv("APPDATA") ?: System.getProperty("user.home")
                File(appData, "VitalPet")
            }
            os.contains("mac") -> {
                File(System.getProperty("user.home"), "Library/Application Support/VitalPet")
            }
            else -> {
                File(System.getProperty("user.home"), ".vitalpet")
            }
        }
        if (!dir.exists()) {
            dir.mkdirs()
        }
        dir
    }

    private val dataFile: File by lazy {
        File(storageDir, "vitalpet_pets.json")
    }

    private val _pets = MutableStateFlow<List<Pet>>(emptyList())
    val pets: StateFlow<List<Pet>> = _pets.asStateFlow()

    fun initialize() {
        if (dataFile.exists()) {
            try {
                val json = dataFile.readText(Charsets.UTF_8)
                if (json.isNotBlank()) {
                    val type = object : TypeToken<List<Pet>>() {}.type
                    val loaded: List<Pet> = gson.fromJson(json, type)
                    _pets.value = loaded
                    return
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        _pets.value = emptyList()
        save()
    }

    fun addPet(pet: Pet) {
        val updated = _pets.value.toMutableList()
        updated.add(pet)
        _pets.value = updated
        save()
    }

    fun updatePet(pet: Pet) {
        val updated = _pets.value.toMutableList()
        val index = updated.indexOfFirst { it.id == pet.id }
        if (index != -1) {
            updated[index] = pet
            _pets.value = updated
            save()
        }
    }

    fun deletePet(petId: String) {
        val updated = _pets.value.filterNot { it.id == petId }
        _pets.value = updated
        save()
    }

    fun replaceAllPets(newPets: List<Pet>) {
        _pets.value = newPets
        save()
    }

    fun exportJson(): String = gson.toJson(_pets.value)

    fun importJson(json: String): Boolean {
        return try {
            val type = object : TypeToken<List<Pet>>() {}.type
            val imported: List<Pet> = gson.fromJson(json, type)
            if (imported.isNotEmpty()) {
                _pets.value = imported
                save()
                true
            } else false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun save() {
        try {
            val json = gson.toJson(_pets.value)
            dataFile.writeText(json, Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
