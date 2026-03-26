package com.example.pita_rewards2

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.Button


class Basket : AppCompatActivity() {

    lateinit var navigation : BottomNavigationView
    lateinit var checkoutButton: Button


    var orders: MutableList<String> = MainActivity().order

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_basket)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.basket)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        checkoutButton = findViewById(R.id.checkout_button)
        checkoutButton.setOnClickListener {
            startActivity(Intent(this, Checkout::class.java))
            finish()
        }

        navigation =  findViewById(R.id.bottom_navigation)
        navigation.selectedItemId = R.id.basket

        navigation.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.account -> {
                    startActivity(Intent(this, Account::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}