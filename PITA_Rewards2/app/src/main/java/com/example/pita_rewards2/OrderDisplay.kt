package com.example.pita_rewards2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.LinearLayout
import android.view.LayoutInflater

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
                MainActivity.customizations.remove(order)
                employeeContainer.removeView(itemView)

            }
            employeeContainer.addView(itemView)
        }
    }
}