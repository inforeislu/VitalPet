package com.inforeis.vitalpet.models

import java.util.Date
import java.util.UUID

data class DietPlan(
    val id: String = UUID.randomUUID().toString(),
    val foodName: String,
    val kcalPer100g: Double,
    val targetDailyKcal: Double,
    val isActive: Boolean = true,
    val createdAt: Date = Date()
) {
    val dailyPortionGrams: Double
        get() = if (kcalPer100g > 0) (targetDailyKcal / kcalPer100g) * 100.0 else 0.0
}
