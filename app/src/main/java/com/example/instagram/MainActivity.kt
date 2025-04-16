package com.example.instagram

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Handler(Looper.getMainLooper()).postDelayed({
            val auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser

            if (currentUser != null) {
                // User is already logged in → go to HomeActivity
                startActivity(Intent(this, HomeActivity::class.java))
            } else {
                // User is not logged in → go to Login Page
                startActivity(Intent(this, loginpage::class.java))
            }
            finish()
        }, 2000)
    }
}
