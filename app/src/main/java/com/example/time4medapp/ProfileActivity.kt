package com.example.time4medapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class ProfileActivity : AppCompatActivity() {

    private lateinit var editName: EditText
    private lateinit var editEmail: EditText
    private lateinit var profileImage: ImageView
    private lateinit var btnSave: Button
    private lateinit var btnLogout: Button
    private lateinit var btnDeleteAccount: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        editName = findViewById(R.id.edit_name)
        editEmail = findViewById(R.id.edit_email)
        profileImage = findViewById(R.id.profile_image)
        btnSave = findViewById(R.id.btn_save_profile)
        btnLogout = findViewById(R.id.btn_logout)
        btnDeleteAccount = findViewById(R.id.btn_delete_account)

        loadFirebaseUser()
        loadProfile()

        btnSave.setOnClickListener {
            saveProfile()
            ToastUtils.showCustomToast(this, "Profile Saved Successfully!")
            finish()
        }

        btnLogout.setOnClickListener {

            FirebaseAuth.getInstance().signOut()

            val sharedPreferences = getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
            sharedPreferences.edit().clear().apply()

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(this, gso)

            googleSignInClient.signOut().addOnCompleteListener {

                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)

                ToastUtils.showCustomToast(this, "Logged Out")
            }
        }

        btnDeleteAccount.setOnClickListener {

            val user = FirebaseAuth.getInstance().currentUser

            user?.delete()?.addOnCompleteListener {

                if (it.isSuccessful) {

                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()

                    ToastUtils.showCustomToast(this, "Account Deleted")

                } else {

                    ToastUtils.showCustomToast(this, "Failed to delete account")

                }

            }
        }
    }

    private fun loadFirebaseUser() {

        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {

            user.displayName?.let {
                editName.setText(it)
            }

            user.email?.let {
                editEmail.setText(it)
            }

            user.photoUrl?.let {
                Glide.with(this)
                    .load(it)
                    .into(profileImage)
            }
        }
    }

    private fun saveProfile() {

        val name = editName.text.toString()
        val email = editEmail.text.toString()

        val sharedPreferences = getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("name", name)
        editor.putString("email", email)
        editor.apply()
    }

    private fun loadProfile() {

        val sharedPreferences = getSharedPreferences("UserProfile", Context.MODE_PRIVATE)

        val savedName = sharedPreferences.getString("name", "")
        val savedEmail = sharedPreferences.getString("email", "")

        if (!savedName.isNullOrEmpty()) {
            editName.setText(savedName)
        }

        if (!savedEmail.isNullOrEmpty()) {
            editEmail.setText(savedEmail)
        }
    }

    private fun showCustomToast(message: String) {

        val inflater: LayoutInflater = layoutInflater
        val layout = inflater.inflate(R.layout.custom_toast, null)

        val text = layout.findViewById<TextView>(R.id.toast_text)
        text.text = message

        val toast = Toast(applicationContext)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.show()
    }
}