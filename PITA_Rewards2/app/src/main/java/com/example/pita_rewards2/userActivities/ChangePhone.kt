package com.example.pita_rewards2.userActivities

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pita_rewards2.R
import com.google.firebase.database.FirebaseDatabase

class ChangePhone : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update_phone)

        // Handle edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.account)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase
        database = FirebaseDatabase.getInstance()

        // Correct EditText IDs from layout
        val phoneEditText = findViewById<EditText>(R.id.phone)
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
            val newPhone = phoneEditText.text.toString().trim()
            val confirmPhone= confirmEditText.text.toString().trim()


            if (newPhone != confirmPhone) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Reference to the password field only
            val phoneRef = database.getReference("users").child(userId).child("password")

            Log.d("FirebaseDebug", "Updating /users/$userId/password to $newPhone")

            phoneRef.setValue(newPhone)
                .addOnSuccessListener {
                    Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show()
                    phoneEditText.text.clear()
                    confirmEditText.text.clear()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("FirebaseError", e.toString())
                }
        }
    }
}
/*
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


class ChangePhone : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update_phone)

        // Handle edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.account)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase
        database = FirebaseDatabase.getInstance()

        // Correct EditText IDs from layout
        val phoneEditText = findViewById<EditText>(R.id.phone)
        val saveButton = findViewById<Button>(R.id.save_button)

        // Get userId from Intent
        val userId = intent.getStringExtra("userId")
        if (userId.isNullOrEmpty()) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        saveButton.setOnClickListener {
            val newPhone = phoneEditText.text.toString().trim()

            if (newPhone.isEmpty()) {
                Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (userId.isNullOrEmpty()) {
                Toast.makeText(this, "User ID missing", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val phoneRef = database.getReference("users").child(userId).child("phone")
            Log.d("FirebaseDebug", "Attempting to update /users/$userId/phone to $newPhone")

            phoneRef.setValue(newPhone)
                .addOnSuccessListener {
                    Toast.makeText(this, "Phone updated successfully!", Toast.LENGTH_SHORT).show()
                    Log.d("FirebaseDebug", "Phone updated successfully in Firebase.")
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error updating phone: ${e.message}", Toast.LENGTH_LONG)
                        .show()
                    Log.e("FirebaseError", e.toString())
                }
        }
    }
}
*/
