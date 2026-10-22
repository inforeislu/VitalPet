package com.inforeis.vitalpet.models

import java.util.Date
import java.util.UUID

data class VaccineLog(
    val id: String = UUID.randomUUID().toString(),
    val vaccineName: String,
    val applicationDate: Date = Date(),
    val nextDueDate: Date? = null,
    val batchNumber: String? = null,
    val notes: String? = null
) {
    val isOverdue: Boolean
        get() = nextDueDate != null && nextDueDate.before(Date())
}
