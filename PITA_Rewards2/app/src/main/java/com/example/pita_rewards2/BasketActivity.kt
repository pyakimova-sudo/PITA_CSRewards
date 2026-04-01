package com.example.pita_rewards2

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pita_rewards2.databinding.ActivityBasketBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.Toast
import android.widget.LinearLayout
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView


class BasketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBasketBinding
    lateinit var navigation : BottomNavigationView

    private lateinit var orderContainer: LinearLayout

    private lateinit var totalText: TextView
    private lateinit var subtotalText: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBasketBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        orderContainer = findViewById(R.id.orderContainer)
        totalText = findViewById(R.id.totalTxt)
        subtotalText = findViewById(R.id.totalFeeTxt)

        displayOrders()

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            insets
        }

        binding.btnCheckout.setOnClickListener {
            val intent = Intent(this, CheckoutActivity::class.java)
            startActivity(intent)
        }

        navigation =  findViewById(R.id.bottom_navigation)
        navigation.selectedItemId = R.id.basket


        navigation.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)

                    finish()
                    true
                }
                R.id.account -> {
                    val intent = Intent(this, Account::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)

                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun calculateTotal() {
        val total = MainActivity.customizations.sumOf { it.price }
        totalText.text = "$$total"
        subtotalText.text = "$$total"

    }
    private fun displayOrders() {
        val drinkCustomizations = MainActivity.customizations

        val inflater = LayoutInflater.from(this)

        if (drinkCustomizations.isEmpty()) {
            Toast.makeText(this, "empty", Toast.LENGTH_LONG).show()
        }

        for ((index, order) in drinkCustomizations.withIndex()) {
            val itemView = inflater.inflate(R.layout.viewholder_basket, orderContainer, false)

            val drinkNameText = itemView.findViewById<TextView>(R.id.drinkNameText)
            drinkNameText.text = order.drink

            val orderItems = itemView.findViewById<TextView>(R.id.orderItems)
            val detailsList = listOfNotNull(
                order.size.takeIf { it.isNotEmpty() }?.let { "Size: $it" },
                order.milk.takeIf { it.isNotEmpty() && it != "None" }?.let { "Milk: $it" },
                order.sweetness.takeIf { it.isNotEmpty() }?.let { "Sweetness: $it" }
            )
            orderItems.text = detailsList.joinToString("\n")

            val totalFee = itemView.findViewById<TextView>(R.id.totalFee)
            totalFee.text = "$${order.price}"

            val removeBtn = itemView.findViewById<ImageView>(R.id.removeItemButton)

            // Remove an item when clicked
            removeBtn.setOnClickListener {
                MainActivity.customizations.remove(order)
                orderContainer.removeView(itemView)
                calculateTotal()

            }
            orderContainer.addView(itemView)

        }
        calculateTotal()
    }
}