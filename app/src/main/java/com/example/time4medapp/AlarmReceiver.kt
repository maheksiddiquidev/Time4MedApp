package com.example.time4medapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {

        val serviceIntent = Intent(context, AlarmService::class.java)

        serviceIntent.putExtra(
            "medicineName",
            intent?.getStringExtra("medicineName")
        )

        serviceIntent.putExtra(
            "time",
            intent?.getStringExtra("time")
        )

        serviceIntent.putExtra(
            "medicineType",
            intent?.getStringExtra("medicineType")
        )

        serviceIntent.putExtra(
            "historyId",
            intent?.getIntExtra("historyId", -1) ?: -1
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }
}