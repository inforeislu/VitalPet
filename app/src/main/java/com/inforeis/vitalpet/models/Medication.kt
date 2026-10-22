package com.inforeis.vitalpet.models

import java.util.Date
import java.util.UUID

data class Medication(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val dosage: String,
    val frequency: String,
    val isActive: Boolean = true,
    val startDate: Date = Date(),
    val notes: String? = null
)
