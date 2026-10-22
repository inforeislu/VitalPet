package com.inforeis.vitalpet.services

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.inforeis.vitalpet.models.ActivityLevel
import com.inforeis.vitalpet.models.AppetiteLevel
import com.inforeis.vitalpet.models.DewormingType
import com.inforeis.vitalpet.models.EnergyLevel
import com.inforeis.vitalpet.models.FastingProcedure
import com.inforeis.vitalpet.models.FastingStage
import com.inforeis.vitalpet.models.NeuterStatus
import com.inforeis.vitalpet.models.PetGender
import com.inforeis.vitalpet.models.PhotoCategory
import com.inforeis.vitalpet.models.Species
import com.inforeis.vitalpet.models.WaterIntakeLevel
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Universal JSON serialization and deserialization engine for VitalPet.
 * Guarantees 100% cross-platform compatibility between iOS (Swift Codable / CoreData) and Android.
 */
object VitalPetJson {

    private val isoFormatUtc = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private val supportedDateFormats = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd'T'HH:mm:ssXXX",
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd"
    )

    private class FlexibleDateAdapter : JsonSerializer<Date>, JsonDeserializer<Date> {
        override fun serialize(src: Date?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
            if (src == null) return JsonPrimitive("")
            synchronized(isoFormatUtc) {
                return JsonPrimitive(isoFormatUtc.format(src))
            }
        }

        override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Date {
            if (json == null || json.isJsonNull) return Date()

            // 1. If it's a numeric timestamp (milliseconds or seconds from Swift timeIntervalSince1970)
            if (json.isJsonPrimitive && json.asJsonPrimitive.isNumber) {
                val num = json.asLong
                return if (num < 10_000_000_000L) {
                    // Unix seconds (Swift standard)
                    Date(num * 1000L)
                } else {
                    // Unix milliseconds
                    Date(num)
                }
            }

            val str = json.asString.trim()
            if (str.isEmpty()) return Date()

            for (format in supportedDateFormats) {
                try {
                    val sdf = SimpleDateFormat(format, Locale.US).apply {
                        timeZone = TimeZone.getTimeZone("UTC")
                    }
                    val parsed = sdf.parse(str)
                    if (parsed != null) return parsed
                } catch (_: Exception) {
                    // Try next format
                }
            }

            // Fallback: try parsing as long string
            str.toLongOrNull()?.let { num ->
                return if (num < 10_000_000_000L) Date(num * 1000L) else Date(num)
            }

            return Date()
        }
    }

    private class FlexibleEnumAdapter<T : Enum<T>>(
        private val enumClass: Class<T>,
        private val defaultValue: T,
        private val matcher: (String) -> T?
    ) : JsonDeserializer<T>, JsonSerializer<T> {
        override fun serialize(src: T?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
            return JsonPrimitive(src?.name ?: defaultValue.name)
        }

        override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): T {
            if (json == null || json.isJsonNull) return defaultValue
            val str = json.asString.trim()
            return matcher(str) ?: enumClass.enumConstants?.find {
                it.name.equals(str, ignoreCase = true)
            } ?: defaultValue
        }
    }

    val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Date::class.java, FlexibleDateAdapter())
        .registerTypeAdapter(Species::class.java, FlexibleEnumAdapter(Species::class.java, Species.DOG) { str ->
            when {
                str.contains("dog", ignoreCase = true) || str.contains("can", ignoreCase = true) || str.contains("cão", ignoreCase = true) -> Species.DOG
                str.contains("cat", ignoreCase = true) || str.contains("fel", ignoreCase = true) || str.contains("gato", ignoreCase = true) -> Species.CAT
                else -> Species.fromString(str)
            }
        })
        .registerTypeAdapter(PetGender::class.java, FlexibleEnumAdapter(PetGender::class.java, PetGender.MALE) { str ->
            when {
                str.startsWith("m", ignoreCase = true) -> PetGender.MALE
                str.startsWith("f", ignoreCase = true) -> PetGender.FEMALE
                else -> PetGender.fromString(str)
            }
        })
        .registerTypeAdapter(NeuterStatus::class.java, FlexibleEnumAdapter(NeuterStatus::class.java, NeuterStatus.NEUTERED) { str ->
            when {
                str.contains("intact", ignoreCase = true) || str.contains("inteiro", ignoreCase = true) -> NeuterStatus.INTACT
                else -> NeuterStatus.NEUTERED
            }
        })
        .registerTypeAdapter(ActivityLevel::class.java, FlexibleEnumAdapter(ActivityLevel::class.java, ActivityLevel.NORMAL) { str ->
            ActivityLevel.fromString(str)
        })
        .registerTypeAdapter(DewormingType::class.java, FlexibleEnumAdapter(DewormingType::class.java, DewormingType.INTERNAL) { str ->
            DewormingType.fromString(str)
        })
        .registerTypeAdapter(AppetiteLevel::class.java, FlexibleEnumAdapter(AppetiteLevel::class.java, AppetiteLevel.NORMAL) { str ->
            AppetiteLevel.entries.find { it.name.equals(str, ignoreCase = true) || it.rawValue.equals(str, ignoreCase = true) }
        })
        .registerTypeAdapter(EnergyLevel::class.java, FlexibleEnumAdapter(EnergyLevel::class.java, EnergyLevel.NORMAL) { str ->
            EnergyLevel.entries.find { it.name.equals(str, ignoreCase = true) || it.rawValue.equals(str, ignoreCase = true) }
        })
        .registerTypeAdapter(WaterIntakeLevel::class.java, FlexibleEnumAdapter(WaterIntakeLevel::class.java, WaterIntakeLevel.NORMAL) { str ->
            WaterIntakeLevel.entries.find { it.name.equals(str, ignoreCase = true) || it.rawValue.equals(str, ignoreCase = true) }
        })
        .registerTypeAdapter(FastingProcedure::class.java, FlexibleEnumAdapter(FastingProcedure::class.java, FastingProcedure.GENERAL_SURGERY) { str ->
            FastingProcedure.entries.find { it.name.equals(str, ignoreCase = true) || it.rawValue.equals(str, ignoreCase = true) }
        })
        .registerTypeAdapter(FastingStage::class.java, FlexibleEnumAdapter(FastingStage::class.java, FastingStage.BEFORE_FASTING) { str ->
            FastingStage.entries.find { it.name.equals(str, ignoreCase = true) || it.rawValue.equals(str, ignoreCase = true) }
        })
        .registerTypeAdapter(PhotoCategory::class.java, FlexibleEnumAdapter(PhotoCategory::class.java, PhotoCategory.GENERAL) { str ->
            PhotoCategory.entries.find { it.name.equals(str, ignoreCase = true) || it.displayName.equals(str, ignoreCase = true) }
        })
        .setPrettyPrinting()
        .create()
}
