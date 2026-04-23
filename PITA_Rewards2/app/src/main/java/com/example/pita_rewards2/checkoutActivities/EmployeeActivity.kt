package com.example.pita_rewards2.checkoutActivities

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pita_rewards2.databinding.ActivityEmployeeBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import android.view.LayoutInflater
import android.widget.Button
import com.example.pita_rewards2.mainActivities.MainActivity
import com.example.pita_rewards2.R
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView



class EmployeeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmployeeBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var employeeContainer: LinearLayout
    private lateinit var pointsTextView: TextView
    private lateinit var subtractPointsButton: Button

    lateinit var navigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()

        // Initialize the container for the orders
        employeeContainer = findViewById(R.id.employeeContainer)
        // Initialize TextView and Button
        pointsTextView = findViewById(R.id.pointsTextView)
        subtractPointsButton = findViewById(R.id.subtractPointsButton)

        // Retrieve the userId from the Intent
        val userId = intent.getStringExtra("userId")

        if (userId == null) {
            Toast.makeText(this, "User ID missing", Toast.LENGTH_SHORT).show()
            return
        }

        //Reference to the user's points in the database
        val pointsRef = database.getReference("users").child(userId).child("points")

        //TODO set delta to point value of deal !!!!
        subtractPointsButton.setOnClickListener {
            //Decrease points by 406 using ServerValue.increment()
            pointsRef.setValue(ServerValue.increment(-2))
                .addOnSuccessListener {
                    Log.d("FirebaseDebug", "Points decremented successfully!")
                    Toast.makeText(this, "Points decreased by 406!", Toast.LENGTH_SHORT).show()

                    //Update the points text in the UI
                    pointsRef.get().addOnSuccessListener { snapshot ->
                        val updatedPoints = snapshot.value as? Long ?: 0L
                        pointsTextView.text = "Points: $updatedPoints"
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("FirebaseError", "Failed to update points: ${e.message}", e)
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
        //Call the method to display orders after Firebase initialization
        displayOrders()

        binding.homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
            finish()
        }
        navigation = findViewById(R.id.bottom_navigation)
        navigation.selectedItemId = R.id.orders

        navigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.unavailability -> {
                    val intent = Intent(this, Unavailable::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.putExtra("userId", userId)
                    startActivity(intent)

                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun displayOrders() {
        employeeContainer.removeAllViews()

        val drinkCustomizations = MainActivity.customizations.toList()
        val inflater = LayoutInflater.from(this)

        for ((index, order) in drinkCustomizations.withIndex()) {
            val itemView = inflater.inflate(R.layout.viewholder_employee, employeeContainer, false)

            val drinkNameText = itemView.findViewById<TextView>(R.id.drinkNameEmployee)
            drinkNameText.text = order.drink

            val nameLabel = itemView.findViewById<TextView>(R.id.customerNameText)
            nameLabel.text = order.customerName

            val locationText = itemView.findViewById<TextView>(R.id.locationText)
            locationText.text = "${order.location}"


            val orderItems = itemView.findViewById<TextView>(R.id.orderItemsEmployee)
            val detailsList = listOfNotNull(
                order.size.takeIf { it.isNotEmpty() }?.let { "Size: $it" },
                order.temp.takeIf { it.isNotEmpty() }?.let { "Temp: $it" }, // Added Temperature
                order.milk.takeIf { it.isNotEmpty() && it != "None" }?.let { "Milk: $it" },
                order.sweetness.takeIf { it.isNotEmpty() && it != "100%" }?.let { "Sweetness: $it" },
                order.extraDetails.takeIf { it.isNotEmpty() }
            )
            orderItems.text = detailsList.joinToString("\n")

            val removeBtn = itemView.findViewById<TextView>(R.id.doneButton)

            // Remove an item when clicked
            removeBtn.setOnClickListener {
                MainActivity.customizations.remove(order)
                employeeContainer.removeView(itemView)
                if (MainActivity.customizations.isEmpty()) {
                    MainActivity.isOrderSubmitted = false
                    MainActivity.order.clear()
                }
            }

            employeeContainer.addView(itemView)
        }
    }
}