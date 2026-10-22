package com.inforeis.vitalpet.models

import java.util.Date
import java.util.UUID

data class WeightLog(
    val id: String = UUID.randomUUID().toString(),
    val weightKg: Double,
    val date: Date = Date(),
    val bcs: Int? = null,
    val notes: String? = null
)
