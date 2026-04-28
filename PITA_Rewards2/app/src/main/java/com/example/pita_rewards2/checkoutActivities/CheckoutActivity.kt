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
            //Get name for order
            val name = nameBox.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show()
            return@setOnClickListener
        }
            //Store drinks in Active orders(Employee page)
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
                    "temp" to item.temp,
                    "milk" to item.milk,
                    "sweetness" to item.sweetness,
                    "price" to item.price,
                    "quantity" to item.quantity,
                    "extraDetails" to item.extraDetails,
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

            //Calculates points based on price
            val newPoints = total

            Log.d("FirebaseDebug", "User ID: $userId")
            Log.d("FirebaseDebug", "Points path: /users/$userId/points")
            Log.d("FirebaseDebug", "New points to be set: $newPoints")

            //Reference to the user's points in Firebase
            val pointsRef = database
                .getReference("users")
                .child(userId)
                .child("points")

            //Increment the points in database
            pointsRef.setValue(ServerValue.increment(newPoints.toLong()))
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
                val duration = 120000L * totalQuantity
                val endTime = System.currentTimeMillis() + duration
                prefs.edit().putLong("drink_timer_end", endTime)
                    .putBoolean("is_order_active", true)
                    .apply()
            }

//Go to employee activity after order to see list
            val intent = Intent(this, EmployeeActivity::class.java)
            intent.putExtra("userId", userId)
            intent.putExtra("customerName", name)
            startActivity(intent)
            finish()
        }
    }
}
