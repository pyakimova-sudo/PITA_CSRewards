package com.example.pita_rewards2.checkoutActivities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.LinearLayout
import android.view.LayoutInflater
import com.example.pita_rewards2.R
import com.example.pita_rewards2.mainActivities.MainActivity

class OrderDisplay : AppCompatActivity() {
    private lateinit var employeeContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee)

        employeeContainer = findViewById(R.id.employeeContainer)
        displayOrders()
    }
    private fun displayOrders() {
        employeeContainer.removeAllViews()

        val drinkCustomizations = MainActivity.customizations
        val inflater = LayoutInflater.from(this)

        for ((index, order) in drinkCustomizations.withIndex()) {
            val itemView = inflater.inflate(R.layout.viewholder_employee, employeeContainer, false)

            // Set Drink Name
            val drinkNameText = itemView.findViewById<TextView>(R.id.drinkNameEmployee)
            drinkNameText.text = order.drink

            // Set Customer Name
            val nameLabel = itemView.findViewById<TextView>(R.id.customerNameText)
            nameLabel.text = order.customerName

            // Set Location Name
            val locationLabel = itemView.findViewById<TextView>(R.id.locationText)
            locationLabel.text = order.location

            // Set Order Details
            val orderItems = itemView.findViewById<TextView>(R.id.orderItemsEmployee)
            val detailsList = listOfNotNull(
                order.size.takeIf { it.isNotEmpty() }?.let { "Size: $it" },
                order.milk.takeIf { it.isNotEmpty() && it != "None" }?.let { "Milk: $it" },
                order.sweetness.takeIf { it.isNotEmpty() }?.let { "Sweetness: $it" }
            )
            orderItems.text = detailsList.joinToString("\n")

            // Remove Button Logic
            val removeBtn = itemView.findViewById<TextView>(R.id.doneButton)
            removeBtn.setOnClickListener {
                MainActivity.customizations.remove(order)
                employeeContainer.removeView(itemView)

            }
            employeeContainer.addView(itemView)
        }
    }
}