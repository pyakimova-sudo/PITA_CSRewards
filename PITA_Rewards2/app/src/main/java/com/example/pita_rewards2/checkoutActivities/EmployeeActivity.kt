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
import android.view.LayoutInflater
import android.widget.Button
import com.example.pita_rewards2.mainActivities.MainActivity
import com.example.pita_rewards2.R
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.pita_rewards2.databinding.ViewholderEmployeeBinding
import android.Manifest
import com.google.android.material.bottomnavigation.BottomNavigationView


//TODO global updating drink timer(for estimate) STRETCH
class EmployeeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmployeeBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var employeeContainer: LinearLayout
    private lateinit var pointsTextView: TextView
    //private lateinit var subtractPointsButton: Button

    lateinit var navigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()

        val totalQuantity = intent.getIntExtra("totalQuantity", 0)

        // Initialize the container for the orders
        employeeContainer = findViewById(R.id.employeeContainer)
        pointsTextView = findViewById(R.id.pointsTextView)
        //subtractPointsButton = findViewById(R.id.subtractPointsButton)

        //UserId check
        val userId = intent.getStringExtra("userId")
        if (userId == null) {
            Toast.makeText(this, "User ID missing", Toast.LENGTH_SHORT).show()
            return
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
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

        //User points in database
        val pointsRef = database.getReference("users").child(userId).child("points")

        //Test button for others use
        /*subtractPointsButton.setOnClickListener {
            //Decrease points by 406 using ServerValue.increment()
            pointsRef.setValue(ServerValue.increment(-2))
                .addOnSuccessListener {
                    Log.d("FirebaseDebug", "Points decremented successfully!")
                    Toast.makeText(this, "Points decreased by 2!", Toast.LENGTH_SHORT).show()

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
        } */
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

    //Display Active orders after Firebase initialization
    private fun displayOrders() {
        val ordersRef = database.getReference("ActiveOrders")

        ordersRef.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                //Clear container to prevent duplicates
                employeeContainer.removeAllViews()

                if (!snapshot.exists()) {
                    return
                }

                for (orderSnapshot in snapshot.children) {
                    val orderKey = orderSnapshot.key ?: continue

                    val itemBinding = ViewholderEmployeeBinding.inflate(
                        LayoutInflater.from(this@EmployeeActivity),
                        employeeContainer,
                        false
                    )

                    val drink = orderSnapshot.child("drink").value?.toString() ?: ""
                    val customerName = orderSnapshot.child("customerName").value?.toString() ?: "No Name"
                    val targetUserId = orderSnapshot.child("userId").value?.toString() ?: ""
                    val location = orderSnapshot.child("location").value?.toString() ?: "Unknown"
                    val size = orderSnapshot.child("size").value?.toString() ?: ""
                    val temp = orderSnapshot.child("temp").value?.toString() ?: ""
                    val milk = orderSnapshot.child("milk").value?.toString() ?: ""
                    val sweetness = orderSnapshot.child("sweetness").value?.toString() ?: ""
                    val extra = orderSnapshot.child("extraDetails").value?.toString() ?: ""

                    itemBinding.root.findViewById<TextView>(R.id.locationText)?.text = location
                    //val orderItems = itemView.findViewById<TextView>(R.id.orderItemsEmployee)
                    val detailsList = listOfNotNull(
                        "$location",
                        size.takeIf { it.isNotEmpty() }?.let { "Size: $it" },
                        temp.takeIf { it.isNotEmpty() }?.let { "Temp: $it" },
                        milk.takeIf { it.isNotEmpty() && it != "None" }?.let { "Milk: $it" },
                        sweetness.takeIf { it.isNotEmpty() }?.let { "Sweetness: $it" },
                        extra.takeIf { it.isNotEmpty() }?.let { "Note: $it" }
                    )

                    itemBinding.drinkNameEmployee.text = drink
                    itemBinding.customerNameText.text = customerName
                    itemBinding.orderItemsEmployee.text = detailsList.joinToString("\n")

                    itemBinding.doneButton.setOnClickListener {
                        if (targetUserId.isNotEmpty()) {
                            sendNotification(targetUserId, customerName)
                        }
                        ordersRef.child(orderKey).removeValue()
                    }
                    employeeContainer.addView(itemBinding.root)
                }
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                Log.e("FirebaseError", error.message)
            }
        })
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED) {
            manager.notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }
    }
    /*
    //New estimated time for when Order is manually marked done
    private fun recalculateTimer() {

        val ordersRef = FirebaseDatabase.getInstance().getReference("ActiveOrders")

        ordersRef.get().addOnSuccessListener { snapshot ->
            var totalQuantity = 0L

            for (order in snapshot.children) {
                totalQuantity += order.child("quantity")
                    .getValue(Long::class.java) ?: 0L
            }

            val estimatedMinutes = totalQuantity * 2

            val resultIntent = Intent().apply {
                putExtra("estimatedMinutes", estimatedMinutes)
            }

            setResult(RESULT_OK, resultIntent)
        }
    }
     */
