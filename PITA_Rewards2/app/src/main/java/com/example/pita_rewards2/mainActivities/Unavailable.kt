package com.example.pita_rewards2.mainActivities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pita_rewards2.checkoutActivities.EmployeeActivity
import com.example.pita_rewards2.R

class Unavailable : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unavailable)

        //Latte
        val latteCheckbox = findViewById<CheckBox>(R.id.latteCheckbox)
        latteCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "Latte has been disabled", Toast.LENGTH_SHORT).show()
                DisabledButtons.setDisabled("Latte", isChecked)
            } else {
                Toast.makeText(this, "Latte has been undisabled", Toast.LENGTH_SHORT).show()
            }
        }
        latteCheckbox.isChecked = DisabledButtons.isDisabled("Latte")

        //Smoothie
        val smoothieCheckbox = findViewById<CheckBox>(R.id.smoothieCheckbox)
        smoothieCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "Smoothie has been disabled", Toast.LENGTH_SHORT).show()
                DisabledButtons.setDisabled("Smoothie", isChecked)
            } else {
                Toast.makeText(this, "Smoothie has been undisabled", Toast.LENGTH_SHORT).show()
            }
        }
        smoothieCheckbox.isChecked = DisabledButtons.isDisabled("Smoothie")

        //Matcha
        val matchaCheckbox = findViewById<CheckBox>(R.id.matchaCheckbox)
        matchaCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "Matcha has been disabled", Toast.LENGTH_SHORT).show()
                DisabledButtons.setDisabled("Matcha", isChecked)
            } else {
                Toast.makeText(this, "Matcha has been undisabled", Toast.LENGTH_SHORT).show()
            }
        }
        matchaCheckbox.isChecked = DisabledButtons.isDisabled("Matcha")

        //Cold Brew
        val coldBrewCheckbox = findViewById<CheckBox>(R.id.coldBrewCheckbox)
        coldBrewCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "Cold Brew has been disabled", Toast.LENGTH_SHORT).show()
                DisabledButtons.setDisabled("Cold Brew", isChecked)
            } else {
                Toast.makeText(this, "Cold Brew has been undisabled", Toast.LENGTH_SHORT).show()
            }
        }
        coldBrewCheckbox.isChecked = DisabledButtons.isDisabled("Cold Brew")

        //Lemonade
        val lemonadeCheckbox = findViewById<CheckBox>(R.id.lemonadeCheckbox)
        lemonadeCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "Lemonade has been disabled", Toast.LENGTH_SHORT).show()
                DisabledButtons.setDisabled("Lemonade", isChecked)
            } else {
                Toast.makeText(this, "Lemonade has been undisabled", Toast.LENGTH_SHORT).show()
            }
        }
        lemonadeCheckbox.isChecked = DisabledButtons.isDisabled("Lemonade")

        //Tea
        val teaCheckbox = findViewById<CheckBox>(R.id.teaCheckbox)
        teaCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "Tea has been disabled", Toast.LENGTH_SHORT).show()
                DisabledButtons.setDisabled("Tea", isChecked)
            } else {
                Toast.makeText(this, "Tea has been undisabled", Toast.LENGTH_SHORT).show()
            }
        }
        teaCheckbox.isChecked = DisabledButtons.isDisabled("Tea")

        //Hot Chocolate
        val hotChocolateCheckbox = findViewById<CheckBox>(R.id.hotChocolateCheckbox)
        hotChocolateCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "Hot Chocolate has been disabled", Toast.LENGTH_SHORT).show()
                DisabledButtons.setDisabled("Hot Chocolate", isChecked)
            } else {
                Toast.makeText(this, "Hot Chocolate has been undisabled", Toast.LENGTH_SHORT).show()
            }
        }
        hotChocolateCheckbox.isChecked = DisabledButtons.isDisabled("Hot Chocolate")

        //Milk
        val milkCheckbox = findViewById<CheckBox>(R.id.milkCheckbox)
        milkCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "Milk has been disabled", Toast.LENGTH_SHORT).show()
                DisabledButtons.setDisabled("Milk", isChecked)
            } else {
                Toast.makeText(this, "Milk has been undisabled", Toast.LENGTH_SHORT).show()
            }
        }
        milkCheckbox.isChecked = DisabledButtons.isDisabled("Milk")

        val userId = intent.getStringExtra("userId")

        val backButton = findViewById<Button>(R.id.back)
        backButton.setOnClickListener {
            startActivity(Intent(this, EmployeeActivity::class.java))
            intent.putExtra("userId", userId)
            finish()
        }

        val menuButton = findViewById<Button>(R.id.menu)
        menuButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}