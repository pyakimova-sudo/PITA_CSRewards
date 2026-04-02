package com.example.pita_rewards2

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase

class ChangePassword : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_change_password)

        // Handle edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.account)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase
        database = FirebaseDatabase.getInstance()

        // Correct EditText IDs from layout
        val passwordEditText = findViewById<EditText>(R.id.password)
        val confirmEditText = findViewById<EditText>(R.id.pass1)
        val saveButton = findViewById<Button>(R.id.save_button)

        // Get userId from Intent
        val userId = intent.getStringExtra("userId")
        if (userId.isNullOrEmpty()) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        saveButton.setOnClickListener {
            val newPassword = passwordEditText.text.toString().trim()
            val confirmPassword = confirmEditText.text.toString().trim()

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please enter both password fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Reference to the password field only
            val passwordRef = database.getReference("users").child(userId).child("password")

            Log.d("FirebaseDebug", "Updating /users/$userId/password to $newPassword")

            passwordRef.setValue(newPassword)
                .addOnSuccessListener {
                    Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show()
                    passwordEditText.text.clear()
                    confirmEditText.text.clear()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("FirebaseError", e.toString())
                }
        }
    }
}