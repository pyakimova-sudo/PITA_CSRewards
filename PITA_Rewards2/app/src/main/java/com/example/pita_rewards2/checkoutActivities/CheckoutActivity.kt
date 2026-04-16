package com.example.pita_rewards2.checkoutActivities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.content.Intent
import android.util.Log
import android.widget.TextView
import com.example.pita_rewards2.R
import com.example.pita_rewards2.MainActivity

import com.google.firebase.database.FirebaseDatabase
import com.example.pita_rewards2.databinding.ActivityCheckoutBinding
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.ServerValue

class CheckoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var database: FirebaseDatabase
    var inQueue: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_checkout)

        val submitButton = findViewById<Button>(R.id.submitButton)
        val nameBox = findViewById<EditText>(R.id.nameBox)

        val totalText = findViewById<TextView>(R.id.checkoutTotal)
        val total = MainActivity.customizations.sumOf { it.price }

        // Display the total price
        totalText.text = "$$total"

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance()

        val userId = intent.getStringExtra("userId")
        if (userId == null) {
            Toast.makeText(this, "User ID missing", Toast.LENGTH_SHORT).show()
            return
        }

        // Set the click listener for the submit button
        submitButton.setOnClickListener {
            val name = nameBox.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Update customer name for each customization item
            MainActivity.customizations.forEach { it.customerName = name }

            // Submit the order to the queue manager
            val add = QueueManager.submitOrder(name, MainActivity.order)
            inQueue += 1
            Toast.makeText(
                this,
                "Thank you for your order, ${add.customerName}!",
                Toast.LENGTH_LONG
            ).show()

            // Calculate the points based on the total price
            val newPoints = total

            Log.d("FirebaseDebug", "User ID: $userId")
            Log.d("FirebaseDebug", "Points path: /users/$userId/points")
            Log.d("FirebaseDebug", "New points to be set: $newPoints")

            // Reference to the user's points in Firebase
            val pointsRef = database
                .getReference("users")
                .child(userId)
                .child("points")

            // Increment the points by the total price (no need to retrieve current value)
            pointsRef.setValue(ServerValue.increment(newPoints.toDouble()))
                .addOnSuccessListener {
                    Log.d("FirebaseDebug", "Points incremented successfully!")
                    Toast.makeText(this, "Points updated successfully!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.e("FirebaseError", "Failed to update points: ${e.message}", e)
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }

            // Go to employee page
            val intent = Intent(this, EmployeeActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }
    }
}