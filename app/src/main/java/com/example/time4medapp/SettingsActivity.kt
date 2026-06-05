package com.example.time4medapp

import android.content.Context
import android.os.Bundle
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var switchSound: Switch
    private lateinit var switchVibration: Switch
    private lateinit var switchDarkMode: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        switchSound = findViewById(R.id.switch_sound)
        switchVibration = findViewById(R.id.switch_vibration)
        switchDarkMode = findViewById(R.id.switch_darkmode)

        val sharedPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)

        switchSound.isChecked = sharedPreferences.getBoolean("sound", true)
        switchVibration.isChecked = sharedPreferences.getBoolean("vibration", true)
        switchDarkMode.isChecked = sharedPreferences.getBoolean("darkmode", false)

        switchSound.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("sound", isChecked).apply()
        }

        switchVibration.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("vibration", isChecked).apply()
        }

        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("darkmode", isChecked).apply()
        }
    }
}