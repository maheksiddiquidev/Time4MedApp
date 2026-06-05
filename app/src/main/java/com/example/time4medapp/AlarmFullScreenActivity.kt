package com.example.time4medapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AlarmFullScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_full_screen)

        val imgMedicine = findViewById<ImageView>(R.id.imgMedicine)
        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val tvDateTime = findViewById<TextView>(R.id.tvDateTime)
        val tvMedicineType = findViewById<TextView>(R.id.tvMedicineType)
        val btnTake = findViewById<Button>(R.id.btnTake)
        val tvDisclaimer = findViewById<TextView>(R.id.tvDisclaimer)

        val imgSuccess = findViewById<ImageView>(R.id.imgSuccess)
        val tvTakenMessage = findViewById<TextView>(R.id.tvTakenMessage)
        val tvHealthyMessage = findViewById<TextView>(R.id.tvHealthyMessage)

        val medicineName = intent.getStringExtra("medicineName") ?: "Medicine"
        val time = intent.getStringExtra("time") ?: ""
        val medicineType = intent.getStringExtra("medicineType") ?: "Tablet"
        val historyId = intent.getIntExtra("historyId", -1)

        tvTitle.text = "Time to take your - $medicineName"

        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val currentDate = sdf.format(Date())
        tvDateTime.text = "$currentDate - $time"

        tvMedicineType.text = medicineType

        btnTake.setOnClickListener {

            val stopServiceIntent = Intent(this, AlarmService::class.java)
            stopService(stopServiceIntent)

            // UPDATE HISTORY STATUS
            if (historyId != -1) {
                val actualDateTime = SimpleDateFormat(
                    "dd MMM yyyy hh:mm a",
                    Locale.getDefault()
                ).format(Date())

                lifecycleScope.launch {
                    AppDatabase.getDatabase(this@AlarmFullScreenActivity)
                        .historyDao()
                        .updateStatusAndTime(
                            historyId,
                            "Taken",
                            actualDateTime
                        )
                }
            }

            // Hide first screen UI
            imgMedicine.visibility = View.GONE
            tvTitle.visibility = View.GONE
            tvDateTime.visibility = View.GONE
            tvMedicineType.visibility = View.GONE
            btnTake.visibility = View.GONE
            tvDisclaimer.visibility = View.GONE

            // Show success UI
            imgSuccess.visibility = View.VISIBLE
            tvTakenMessage.visibility = View.VISIBLE
            tvHealthyMessage.visibility = View.VISIBLE

            Glide.with(this)
                .asGif()
                .load(R.drawable.success_tick)
                .into(imgSuccess)

            Handler(Looper.getMainLooper()).postDelayed({

                val intent = Intent(this, HistoryActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)

                finish()
            }, 5000)
        }
    }
}