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

//TODO: dynamically reformat phone integer into actual phone number format
class ChangePhone : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update_phone)

        // Optional: remove this if your root ID isn't "account"
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.account)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        database = FirebaseDatabase.getInstance()

        //XML FILE FOR R.id.
        val phoneEditText = findViewById<EditText>(R.id.phone)
        val saveButton = findViewById<Button>(R.id.save_button)
/*
        if (phoneEditText.length()<10 || phoneEditText.length()>13){
            Toast.makeText(this, "Give a real phone number", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
*/
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

            val phoneRef = database
                .getReference("users")
                .child(userId)
                .child("phone")

            Log.d("FirebaseDebug", "Updating /users/$userId/phone to $newPhone")

            phoneRef.setValue(newPhone)
                .addOnSuccessListener {
                    Toast.makeText(this, "Phone updated successfully!", Toast.LENGTH_SHORT).show()
                    phoneEditText.text.clear()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("FirebaseError", e.toString())
                }
        }
    }
}