package com.inforeis.vitalpet.models

import com.inforeis.vitalpet.services.LanguageManager.localized
import java.util.Date
import java.util.UUID

enum class WalkIntensity(val rawValue: String, val metFactor: Double) {
    GENTLE("Lenta / Passeio Higiénico", 2.5),
    MODERATE("Moderada / Caminhada Normal", 3.5),
    BRISK("Intensa / Corrida ou Brincadeira", 6.0);

    val displayName: String
        get() = rawValue.localized()
}

data class WalkSession(
    val id: String = UUID.randomUUID().toString(),
    val date: Date = Date(),
    val durationSeconds: Int,
    val distanceMeters: Double? = null,
    val caloriesBurned: Double = 0.0,
    val intensity: WalkIntensity = WalkIntensity.MODERATE,
    val notes: String? = null
) {
    val formattedDuration: String
        get() {
            val mins = durationSeconds / 60
            val secs = durationSeconds % 60
            return if (mins >= 60) {
                val hours = mins / 60
                val remMins = mins % 60
                "${hours}h ${remMins.toString().padStart(2, '0')}m"
            } else {
                "${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')} min"
            }
        }

    companion object {
        fun calculateCalories(weightKg: Double, durationSeconds: Int, intensity: WalkIntensity): Double {
            val durationMinutes = durationSeconds / 60.0
            val kcalPerMinute = (intensity.metFactor * 3.5 * maxOf(1.0, weightKg)) / 200.0
            return maxOf(0.0, kcalPerMinute * durationMinutes)
        }
    }
}
