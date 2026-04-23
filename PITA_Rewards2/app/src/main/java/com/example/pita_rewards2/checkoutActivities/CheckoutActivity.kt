package com.example.pita_rewards2.checkoutActivities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.content.Intent
import android.util.Log
import android.widget.TextView
import com.example.pita_rewards2.R
import com.example.pita_rewards2.mainActivities.MainActivity

import com.google.firebase.database.FirebaseDatabase
import com.example.pita_rewards2.databinding.ActivityCheckoutBinding
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.ServerValue
import com.example.pita_rewards2.userActivities.Account

class CheckoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var database: FirebaseDatabase
    var inQueue: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val submitButton = findViewById<Button>(R.id.submitButton)
        val nameBox = findViewById<EditText>(R.id.nameBox)

        val totalText = findViewById<TextView>(R.id.checkoutTotal)
        val total = intent.getDoubleExtra("finalTotal", 0.0)
        val location = intent.getStringExtra("location") ?: "Unknown"
        totalText.text = String.format("$%.2f", total)

        //Display the total price
        totalText.text = "$$total"

        //Initialize Firebase Database
        database = FirebaseDatabase.getInstance()

        val userId = intent.getStringExtra("userId")
        if (userId == null) {
            Toast.makeText(this, "User ID missing", Toast.LENGTH_SHORT).show()
            return
        }

        //Submit button
        submitButton.setOnClickListener {
            val name = nameBox.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show()
            return@setOnClickListener
        }

            val activeOrdersRef = database.getReference("ActiveOrders")

            //Push each item to Firebase
            MainActivity.customizations.forEach { item ->
                item.customerName = name

                //Push ItemCustomization variables to Firebase
                val orderId = activeOrdersRef.push().key ?: return@forEach
                val orderData = mapOf(
                    "orderId" to orderId,
                    "userId" to userId,
                    "customerName" to name,
                    "drink" to item.drink,
                    "size" to item.size,
                    "milk" to item.milk,
                    "sweetness" to item.sweetness,
                    "price" to item.price,
                    "quantity" to item.quantity,
                    "location" to location
                )
                activeOrdersRef.child(orderId).setValue(orderData)
            }
            MainActivity.isOrderSubmitted = true

            //Submit the order to the queue manager
            val add = QueueManager.submitOrder(name, MainActivity.order)
            inQueue += 1

            Toast.makeText(
                this,
                "Thank you for your order, ${add.customerName}!",
                Toast.LENGTH_LONG
            ).show()

            //TODO custom points???
            //Calculate the points based on the total price
            val newPoints = total / 10

            Log.d("FirebaseDebug", "User ID: $userId")
            Log.d("FirebaseDebug", "Points path: /users/$userId/points")
            Log.d("FirebaseDebug", "New points to be set: $newPoints")

            //Reference to the user's points in Firebase
            val pointsRef = database
                .getReference("users")
                .child(userId)
                .child("points")

            //Increment the points by the total price
            pointsRef.setValue(ServerValue.increment(newPoints.toLong())) // Added .toLong() for safety
                .addOnSuccessListener {
                    Log.d("FirebaseDebug", "Points incremented successfully!")
                    Toast.makeText(this, "Points updated successfully!", Toast.LENGTH_SHORT).show()

                    MainActivity.customizations.clear()
                    MainActivity.order.clear()
                }
                .addOnFailureListener { e ->
                    Log.e("FirebaseError", "Failed to update points: ${e.message}", e)
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }

            val totalQuantity = MainActivity.customizations.sumOf { it.quantity }

            if (totalQuantity > 0) {
                val prefs = getSharedPreferences("PitaPrefs", MODE_PRIVATE)
                //TODO adjusted for testing make time longer
                val duration = 120000L * totalQuantity
                val endTime = System.currentTimeMillis() + duration
                prefs.edit().putLong("drink_timer_end", endTime)
                    .putBoolean("is_order_active", true)//Only lock after order
                    .apply()
            }

// Go to Account
            val intent = Intent(this, Account::class.java)
            intent.putExtra("userId", userId)
            intent.putExtra("customerName", name)
            startActivity(intent)
            finish()
        }
    }
}
