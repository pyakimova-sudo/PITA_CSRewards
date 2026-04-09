package com.example.pita_rewards2

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.CheckBox
import android.widget.Toast

class Unavailable : AppCompatActivity() {
    private lateinit var latteCheckbox : CheckBox
    override fun onCreate(savedInstanceState: Bundle?) {
        //val latteCheckbox = findViewById<CheckBox>(R.id.latteCheckbox).setOnCheckedChangeListener { button, isChecked ->
            //DisabledButtons.setDisabled("Latte", isChecked)
            //Toast.makeText(this, "Latte has been disabled", Toast.LENGTH_SHORT).show()
        //}
        //val hot = findViewById<CheckBox>(R.id.hotOption).isChecked
        //latteCheckbox.isChecked = DisabledButtons.isDisabled("Latte")
        latteCheckbox.setOnCheckedChangeListener { _, isChecked ->
        //   DisabledButtons.setDisabled("Latte", isChecked)
            Toast.makeText(this, "Latte has been disabled", Toast.LENGTH_SHORT).show()
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_unavailable)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}