package com.example.pita_rewards2.checkoutActivities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
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
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.pita_rewards2.mainActivities.Unavailable

//TODO timer need to just keep track of number of orders

//TODO make database for orders so multiple can go into
//TODO same employee page

//TODO point values for varried drinks
class EmployeeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmployeeBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var employeeContainer: LinearLayout
    private lateinit var pointsTextView: TextView
    private lateinit var subtractPointsButton: Button
    private lateinit var unavailableButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()

        unavailableButton = findViewById(R.id.unavailable)
        unavailableButton.setOnClickListener {
            val intent = Intent(this, Unavailable::class.java)
            startActivity(intent)
        }

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

        //Notifications
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "order_channel",
                "Order Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
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
        displayOrders(userId)
    }

    private fun displayOrders(userId: String) {
        employeeContainer.removeAllViews()

        val drinkCustomizations = MainActivity.customizations
        val inflater = LayoutInflater.from(this)

        for (order in drinkCustomizations) {
            val itemView = inflater.inflate(R.layout.viewholder_employee, employeeContainer, false)

            val drinkNameText = itemView.findViewById<TextView>(R.id.drinkNameEmployee)
            drinkNameText.text = order.drink

            val nameLabel = itemView.findViewById<TextView>(R.id.customerNameText)
            nameLabel.text = order.customerName

            val orderItems = itemView.findViewById<TextView>(R.id.orderItemsEmployee)
            val detailsList = listOfNotNull(
                order.size.takeIf { it.isNotEmpty() }?.let { "Size: $it" },
                order.milk.takeIf { it.isNotEmpty() && it != "None" }?.let { "Milk: $it" },
                order.sweetness.takeIf { it.isNotEmpty() }?.let { "Sweetness: $it" }
            )
            orderItems.text = detailsList.joinToString("\n")

            val removeBtn = itemView.findViewById<TextView>(R.id.doneButton)

            // Remove an item when clicked
            removeBtn.setOnClickListener {
                Log.d("DEBUG", "Clicked done for ${order.customerName}")

                sendNotification(userId, order.customerName)

                MainActivity.customizations.remove(order)
                employeeContainer.removeView(itemView)
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("userId", userId)
                startActivity(intent)
            }

            employeeContainer.addView(itemView)
        }
    }
    //Notification setup
    private fun sendNotification(userId: String, customerName: String) {

        val channelId = "order_channel"

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("userId", userId)

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Order Ready")
            .setContentText("$customerName, your drink is ready!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val manager = NotificationManagerCompat.from(this)
        manager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}