package com.inforeis.vitalpet.models

import java.util.Date
import java.util.UUID

data class MedicalVisit(
    val id: String = UUID.randomUUID().toString(),
    val date: Date = Date(),
    val clinicName: String,
    val veterinarianName: String? = null,
    val reason: String,
    val diagnosis: String? = null,
    val notes: String? = null,
    val cost: Double? = null
)
