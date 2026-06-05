package com.example.time4medapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.time4medapp.databinding.ActivityOnboardingBinding
import kotlin.math.abs

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var adapter: OnboardingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val items = listOf(
            OnboardingItem(
                R.raw.reminder_animation,
                "Medicine Reminders",
                "Set reminders so you never miss your medicines."
            ),
            OnboardingItem(
                R.raw.history_animation,
                "Medication History",
                "Track your past medication activity easily."
            ),
            OnboardingItem(
                R.raw.ai_animation,
                "AI Medication Assistant",
                "Ask medicine related questions to your AI assistant."
            )
        )

        adapter = OnboardingAdapter(items)
        binding.viewPager.adapter = adapter

        // PRELOAD PAGES (smooth swipe)
        binding.viewPager.offscreenPageLimit = 3

        // PREMIUM PAGE ANIMATION
        binding.viewPager.setPageTransformer { page, position ->

            val absPos = abs(position)

            page.apply {

                // Fade effect
                alpha = 0.5f + (1 - absPos)

                // Zoom effect
                val scale = 0.85f + (1 - absPos) * 0.15f
                scaleX = scale
                scaleY = scale

                // Parallax slide
                translationX = -position * width * 0.2f
            }
        }

        setupDots(items.size)

        binding.viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                updateDots(position)

                if (position == items.size - 1) {
                    binding.btnNext.text = "Get Started"
                } else {
                    binding.btnNext.text = "Next"
                }
            }
        })

        binding.btnNext.setOnClickListener {

            if (binding.viewPager.currentItem + 1 < items.size) {
                binding.viewPager.currentItem += 1
            } else {
                finishOnboarding()
            }
        }

        binding.txtSkip.setOnClickListener {
            finishOnboarding()
        }
    }

    private fun finishOnboarding() {

        val sharedPref = getSharedPreferences("onboarding", MODE_PRIVATE)
        sharedPref.edit().putBoolean("firstTime", false).apply()

        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun setupDots(count: Int) {

        binding.dotsLayout.removeAllViews()

        for (i in 0 until count) {

            val dot = ImageView(this)

            dot.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.dot_inactive
                )
            )

            val params = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            )

            params.setMargins(8, 0, 8, 0)

            binding.dotsLayout.addView(dot, params)
        }

        updateDots(0)
    }

    private fun updateDots(position: Int) {

        val childCount = binding.dotsLayout.childCount

        for (i in 0 until childCount) {

            val imageView = binding.dotsLayout.getChildAt(i) as ImageView

            if (i == position) {

                imageView.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.dot_active)
                )

                imageView.animate()
                    .scaleX(1.4f)
                    .scaleY(1.4f)
                    .setDuration(200)
                    .start()

            } else {

                imageView.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.dot_inactive)
                )

                imageView.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(200)
                    .start()
            }
        }
    }
}