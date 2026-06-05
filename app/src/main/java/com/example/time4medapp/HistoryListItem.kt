package com.example.time4medapp

sealed class HistoryListItem {

    data class DateItem(
        val date: String
    ) : HistoryListItem()

    data class MedicineItem(
        val name: String,
        val dosage: String,
        val scheduledTime: String,
        val actualTime: String,
        val status: String   // "Taken", "Missed"
    ) : HistoryListItem()
}