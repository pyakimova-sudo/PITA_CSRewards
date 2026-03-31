package com.example.pita_rewards2

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
import android.widget.Toast
import android.widget.LinearLayout
import android.view.LayoutInflater

class OrderDisplay : AppCompatActivity() {
    private lateinit var orderContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_display)

        orderContainer = findViewById(R.id.orderContainer)
        displayOrders()
    }
    private fun displayOrders() {
        val drinkCustomizations = MainActivity.customizations

        val inflater = LayoutInflater.from(this)

        for (order in drinkCustomizations) {
            val itemView = inflater.inflate(R.layout.item_display, orderContainer, false)

            val drinkNameText = itemView.findViewById<TextView>(R.id.drinkNameText)
            drinkNameText.text = order.drink

            val sizeText = itemView.findViewById<TextView>(R.id.sizeText)
            sizeText.text = order.size

            val milkText = itemView.findViewById<TextView>(R.id.milkText)
            milkText.text = order.milk

            val sweetnessText = itemView.findViewById<TextView>(R.id.sweetnessText)
            sweetnessText.text = order.sweetness

            orderContainer.addView(itemView)
        }
    }

}