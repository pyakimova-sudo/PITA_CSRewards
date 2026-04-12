package com.example.pita_rewards2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.example.pita_rewards2.databinding.ActivityEmployeeBinding

class EmployeeActivity : ComponentActivity() {
    private lateinit var binding: ActivityEmployeeBinding

    private lateinit var unavailableButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee)
        binding = ActivityEmployeeBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        unavailableButton = findViewById(R.id.unavailable)
        unavailableButton.setOnClickListener {
            val intent = Intent(this, Unavailable::class.java)
            startActivity(intent)
        }

    }
}