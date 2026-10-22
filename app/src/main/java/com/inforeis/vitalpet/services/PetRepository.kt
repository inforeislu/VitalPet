package com.inforeis.vitalpet.services

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.inforeis.vitalpet.models.Pet
import com.inforeis.vitalpet.models.SampleData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object PetRepository {
    private const val PREFS_NAME = "vitalpet_repo_prefs"
    private const val KEY_PETS = "stored_pets"

    private val _pets = MutableStateFlow<List<Pet>>(emptyList())
    val pets: StateFlow<List<Pet>> = _pets.asStateFlow()

    private var prefs: SharedPreferences? = null
    private var appContext: Context? = null
    private val gson = VitalPetJson.gson

    fun initialize(context: Context) {
        appContext = context.applicationContext
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs?.getString(KEY_PETS, null)
        if (!json.isNullOrEmpty()) {
            try {
                val type = object : TypeToken<List<Pet>>() {}.type
                val loaded: List<Pet> = gson.fromJson(json, type)
                _pets.value = loaded
            } catch (e: Exception) {
                _pets.value = emptyList()
                save()
            }
        } else {
            _pets.value = emptyList()
            save()
        }
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

    fun clearAllData() {
        _pets.value = emptyList()
        save()
    }

    fun getPetById(id: String): Pet? = _pets.value.find { it.id == id }

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
            false
        }
    }

    private fun save() {
        val json = gson.toJson(_pets.value)
        prefs?.edit()?.putString(KEY_PETS, json)?.apply()
        appContext?.let { PetQuickWidgetProvider.notifyDataChanged(it) }
    }
}
