package com.example.time4medapp

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var emailEt: EditText
    private lateinit var passwordEt: EditText
    private lateinit var loginBtn: Button
    private lateinit var signupBtn: Button
    private lateinit var googleBtn: Button
    private lateinit var forgotTv: TextView
    private lateinit var togglePassword: ImageView

    private var passwordVisible = false

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            val data = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {

                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                auth.signInWithCredential(credential)
                    .addOnCompleteListener {

                        if (it.isSuccessful) {

                            ToastUtils.showCustomToast(this, "Google Login Successful")

                            startActivity(Intent(this, MainActivity::class.java))
                            finish()

                        } else {

                            ToastUtils.showCustomToast(this, "Google Login Failed")

                        }

                    }

            } catch (e: Exception) {

                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()

            }

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        if (FirebaseAuth.getInstance().currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        emailEt = findViewById(R.id.emailEt)
        passwordEt = findViewById(R.id.passwordEt)
        loginBtn = findViewById(R.id.loginBtn)
        signupBtn = findViewById(R.id.signupBtn)
        googleBtn = findViewById(R.id.googleLoginBtn)
        forgotTv = findViewById(R.id.forgotTv)
        togglePassword = findViewById(R.id.togglePassword)

        googleBtn.setOnClickListener {

            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)

        }

        togglePassword.setOnClickListener {

            if (passwordVisible) {
                passwordEt.transformationMethod = PasswordTransformationMethod.getInstance()
                passwordVisible = false
            } else {
                passwordEt.transformationMethod = HideReturnsTransformationMethod.getInstance()
                passwordVisible = true
            }

            passwordEt.setSelection(passwordEt.text.length)
        }

        loginBtn.setOnClickListener {

            val email = emailEt.text.toString().trim()
            val password = passwordEt.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                ToastUtils.showCustomToast(this, "Enter email & password")
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {

                    if (it.isSuccessful) {

                        ToastUtils.showCustomToast(this, "Login Successful")

                        startActivity(Intent(this, MainActivity::class.java))
                        finish()

                    } else {

                        val message = when {
                            it.exception?.message?.contains("password") == true -> "Wrong password"
                            it.exception?.message?.contains("no user record") == true -> "Account not found"
                            else -> "Login failed, Create account first"
                        }

                        ToastUtils.showCustomToast(this, message)

                    }
                }
        }

        signupBtn.setOnClickListener {

            val email = emailEt.text.toString().trim()
            val password = passwordEt.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                ToastUtils.showCustomToast(this, "Enter email & password")
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {

                    if (it.isSuccessful) {

                        ToastUtils.showCustomToast(this, "Account Created Successfully")

                    } else {

                        Toast.makeText(this, it.exception?.message, Toast.LENGTH_LONG).show()

                    }
                }
        }

        forgotTv.setOnClickListener {

            val email = emailEt.text.toString().trim()

            if (email.isEmpty()) {
                ToastUtils.showCustomToast(this, "Enter your email first")
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    ToastUtils.showCustomToast(this, "Reset email sent")
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
        }
    }
}