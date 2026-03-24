package com.example.pita_rewards2

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pita_rewards2.databinding.ActivityBasketBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class BasketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBasketBinding
    lateinit var navigation : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBasketBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

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
}