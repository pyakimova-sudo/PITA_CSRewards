package com.example.pita_rewards2

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button

class Checkout : AppCompatActivity() {
    private lateinit var submitButton : Button
    val orderList : MutableList<String> = mutableListOf()

    val order = Queue("1","2",orderList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_checkout)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        submitButton = findViewById(R.id.submit_button)
        submitButton.setOnClickListener {
            startActivity(Intent(this, OrderDisplay::class.java))
            finish()
        }
    }

    fun addtoOrder(item:String) {
        orderList.add(item)
    }
}