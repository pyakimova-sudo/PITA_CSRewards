package com.example.pita_rewards2.checkoutActivities

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import com.example.pita_rewards2.databinding.ActivityCheckoutBinding
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.content.Intent
import android.widget.TextView
import com.example.pita_rewards2.R
import com.example.pita_rewards2.mainActivities.MainActivity


class CheckoutActivity : AppCompatActivity(){

    private lateinit var binding: ActivityCheckoutBinding
    var inQueue: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_checkout)
        val submitButton = findViewById<Button>(R.id.submitButton)
        val nameBox = findViewById<EditText>(R.id.nameBox)

        val totalText = findViewById<TextView>(R.id.checkoutTotal)
        val total = MainActivity.customizations.sumOf { it.price }

        totalText.text = "$$total"

        submitButton.setOnClickListener {
            val name = nameBox.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            MainActivity.customizations.forEach { it.customerName = name }

            val add = QueueManager.submitOrder(name, MainActivity.order)
            inQueue += 1 //${add.customerName}
            Toast.makeText(this, "Thank you for your order, ${add.customerName}!", Toast.LENGTH_LONG).show()
            val intent = Intent(this, OrderDisplay::class.java)
            startActivity(intent)
        }
    }
}