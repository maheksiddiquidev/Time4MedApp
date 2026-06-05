package com.example.time4medapp

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AlarmActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
        )

        setContentView(R.layout.activity_alarm)

        val btnDismiss = findViewById<Button>(R.id.btnDismiss)

        btnDismiss.setOnClickListener {

            // Stop the AlarmService (which is playing sound)
            val serviceIntent = Intent(this, AlarmService::class.java)
            stopService(serviceIntent)

            finish()
        }
    }
}