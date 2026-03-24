package com.example.pita_rewards2

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import com.example.pita_rewards2.databinding.ActivityCheckoutBinding

class CheckoutActivity : AppCompatActivity(){
    private lateinit var binding: ActivityCheckoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}