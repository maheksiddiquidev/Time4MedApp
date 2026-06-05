package com.example.time4medapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_table")
data class HistoryEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val medicineName: String,

    val dosage: String,

    // Full scheduled date & time (example: 2026-03-01 08:00 AM)
    val scheduledDateTime: String,

    // When user actually marked taken/missed
    val actualDateTime: String?,

    // Taken / Missed
    val status: String,

    // Optional notes
    val notes: String? = null
)