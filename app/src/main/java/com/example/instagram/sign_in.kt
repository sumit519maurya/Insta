package com.example.instagram

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class sign_in : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signUpButton: TextView
    private lateinit var goToLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        nameEditText = findViewById(R.id.name)
        emailEditText = findViewById(R.id.email)
        phoneEditText = findViewById(R.id.phone)
        passwordEditText = findViewById(R.id.password)
        signUpButton = findViewById(R.id.signInBtn)
        goToLogin = findViewById(R.id.goToSignUp)

        signUpButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create user in Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { authResult ->
                    val uid = authResult.user?.uid

                    if (uid != null) {
                        val user = hashMapOf(
                            "userId" to uid,
                            "name" to name,
                            "email" to email,
                            "phone" to phone
                        )

                        // Save user data in Firestore under document with UID
                        db.collection("users").document(uid).set(user)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Signup successful", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, loginpage::class.java))
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Firestore save failed: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Signup failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        goToLogin.setOnClickListener {
            startActivity(Intent(this, loginpage::class.java))
            finish()
        }
    }
}
