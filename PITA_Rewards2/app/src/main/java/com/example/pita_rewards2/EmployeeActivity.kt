package com.example.pita_rewards2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.example.pita_rewards2.databinding.ActivityEmployeeBinding

class EmployeeActivity : ComponentActivity() {
    private lateinit var binding: ActivityEmployeeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

    }
}