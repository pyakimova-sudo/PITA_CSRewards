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
import android.widget.TextView


class BasketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBasketBinding
    lateinit var navigation : BottomNavigationView

    private lateinit var orderContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBasketBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        orderContainer = findViewById(R.id.orderContainer)
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
    private fun displayOrders() {
        val drinkCustomizations = MainActivity.customizations

        val inflater = LayoutInflater.from(this)

        if (drinkCustomizations.isEmpty()) {
            Toast.makeText(this, "empty", Toast.LENGTH_LONG).show()
        }

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