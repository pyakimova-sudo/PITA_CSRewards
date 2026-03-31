package com.example.pita_rewards2

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.app.Activity
import android.util.DisplayMetrics
import android.view.ViewGroup
import androidx.core.view.WindowCompat.enableEdgeToEdge
import android.widget.Button

class TimerPopUp : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer_pop_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.timer)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

}