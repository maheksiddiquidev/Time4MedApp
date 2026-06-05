package com.example.time4medapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()

        Handler(Looper.getMainLooper()).postDelayed({

            val sharedPref = getSharedPreferences("onboarding", MODE_PRIVATE)
            val firstTime = sharedPref.getBoolean("firstTime", true)

            val user = FirebaseAuth.getInstance().currentUser

            if (firstTime) {

                // Always show onboarding first
                startActivity(Intent(this, OnboardingActivity::class.java))

            } else {

                // After onboarding, check login
                if (user == null) {
                    startActivity(Intent(this, LoginActivity::class.java))
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                }

            }

            finish()

        }, 3000)
    }
}