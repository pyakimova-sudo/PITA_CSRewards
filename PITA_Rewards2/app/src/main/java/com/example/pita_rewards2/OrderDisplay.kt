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
        val drinkCustomizations = MainActivity.customizations

        val inflater = LayoutInflater.from(this)

        for (order in drinkCustomizations) {
            val itemView = inflater.inflate(R.layout.viewholder_basket, employeeContainer, false)

            val drinkNameText = itemView.findViewById<TextView>(R.id.drinkNameText)
            drinkNameText.text = order.drink

            val orderItems = itemView.findViewById<TextView>(R.id.orderItems)
            val detailsList = listOfNotNull(
                order.size.takeIf { it.isNotEmpty() }?.let { "Size: $it" },
                order.milk.takeIf { it.isNotEmpty() && it != "None" }?.let { "Milk: $it" },
                order.sweetness.takeIf { it.isNotEmpty() }?.let { "Sweetness: $it" }
            )
            orderItems.text = detailsList.joinToString("\n")

            employeeContainer.addView(itemView)
        }
    }

}