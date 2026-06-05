package com.example.time4medapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var navigationView: NavigationView
    private lateinit var headerName: TextView
    private lateinit var headerEmail: TextView
    private lateinit var headerImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()


        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val menuButton = findViewById<ImageButton>(R.id.btn_menu)
        navigationView = findViewById(R.id.navigation_view)

        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val headerView = navigationView.getHeaderView(0)
        headerName = headerView.findViewById(R.id.header_user_name)
        headerEmail = headerView.findViewById(R.id.header_user_email)
        headerImage = headerView.findViewById(R.id.header_profile_image)

        loadDrawerHeader()

        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }

                else -> false
            }
        }

        val fabAddMedication = findViewById<FloatingActionButton>(R.id.fab_add_medication)
        fabAddMedication.setOnClickListener {
            val intent = Intent(this, AddMedicationActivity::class.java)
            startActivity(intent)
        }

        val fabAiChat = findViewById<ImageButton>(R.id.fab_ai_chat)
        fabAiChat.setOnClickListener {
            val intent = Intent(this, AIChatActivity::class.java)
            startActivity(intent)
        }

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.navigation_home -> true

                R.id.navigation_history -> {
                    val intent = Intent(this, HistoryActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }

    private fun loadDrawerHeader() {

        val sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE)

        val savedName = sharedPreferences.getString("name", "User Name")
        val savedEmail = sharedPreferences.getString("email", "user@email.com")

        headerName.text = savedName
        headerEmail.text = savedEmail

        val user = FirebaseAuth.getInstance().currentUser

        if (user != null && user.photoUrl != null) {
            Glide.with(this)
                .load(user.photoUrl)
                .into(headerImage)
        }

        val tvWelcome = findViewById<TextView>(R.id.text_welcome)
        tvWelcome.text = "Welcome $savedName! Tap the + button to add medication."
    }

    override fun onResume() {
        super.onResume()
        loadDrawerHeader()
    }

    override fun onBackPressed() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}