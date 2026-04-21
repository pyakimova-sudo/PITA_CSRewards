package com.example.pita_rewards2.checkoutActivities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pita_rewards2.R
import com.example.pita_rewards2.mainActivities.DisabledButtons
import com.example.pita_rewards2.mainActivities.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView


class Unavailable : AppCompatActivity() {
    lateinit var navigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unavailable)

        val userId = intent.getStringExtra("userId")

        //Latte
        val latteCheckbox = findViewById<CheckBox>(R.id.latteCheckbox)
        latteCheckbox.setOnCheckedChangeListener { _, isChecked ->
            DisabledButtons.setDisabled("Latte", isChecked)
            if (isChecked) {
                Toast.makeText(this, "Latte has been disabled", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Latte has been undisabled", Toast.LENGTH_SHORT).show()
            }
        }
        latteCheckbox.isChecked = DisabledButtons.isDisabled("Latte")

        //Smoothie
        val smoothieCheckbox = findViewById<CheckBox>(R.id.smoothieCheckbox)
        smoothieCheckbox.setOnCheckedChangeListener { _, isChecked ->
            DisabledButtons.setDisabled("Smoothie", isChecked)
            if (isChecked) {
                Toast.makeText(this, "Smoothie has been disabled", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Smoothie has been undisabled", Toast.LENGTH_SHORT).show()
            }
        }
        smoothieCheckbox.isChecked = DisabledButtons.isDisabled("Smoothie")

        //Matcha
        val matchaCheckbox = findViewById<CheckBox>(R.id.matchaCheckbox)
        matchaCheckbox.setOnCheckedChangeListener { _, isChecked ->
            DisabledButtons.setDisabled("Matcha", isChecked)
            if (isChecked) {
                Toast.makeText(this, "Matcha has been disabled", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Matcha has been undisabled", Toast.LENGTH_SHORT).show()
            }
        }
        matchaCheckbox.isChecked = DisabledButtons.isDisabled("Matcha")

        //Cold Brew
        val coldBrewCheckbox = findViewById<CheckBox>(R.id.coldBrewCheckbox)
        coldBrewCheckbox.setOnCheckedChangeListener { _, isChecked ->
            DisabledButtons.setDisabled("Cold Brew", isChecked)
            if (isChecked) {
                Toast.makeText(this, "Cold Brew has been disabled", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Cold Brew has been undisabled", Toast.LENGTH_SHORT).show()
            }
        }
        coldBrewCheckbox.isChecked = DisabledButtons.isDisabled("Cold Brew")

        //Lemonade
        val lemonadeCheckbox = findViewById<CheckBox>(R.id.lemonadeCheckbox)
        lemonadeCheckbox.setOnCheckedChangeListener { _, isChecked ->
            DisabledButtons.setDisabled("Lemonade", isChecked)
            if (isChecked) {
                Toast.makeText(this, "Lemonade has been disabled", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Lemonade has been undisabled", Toast.LENGTH_SHORT).show()
            }
        }
        lemonadeCheckbox.isChecked = DisabledButtons.isDisabled("Lemonade")

        //Tea
        val teaCheckbox = findViewById<CheckBox>(R.id.teaCheckbox)
        teaCheckbox.setOnCheckedChangeListener { _, isChecked ->
            DisabledButtons.setDisabled("Tea", isChecked)
            if (isChecked) {
                Toast.makeText(this, "Tea has been disabled", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Tea has been undisabled", Toast.LENGTH_SHORT).show()
            }
        }
        teaCheckbox.isChecked = DisabledButtons.isDisabled("Tea")

        //Hot Chocolate
        val hotChocolateCheckbox = findViewById<CheckBox>(R.id.hotChocolateCheckbox)
        hotChocolateCheckbox.setOnCheckedChangeListener { _, isChecked ->
            DisabledButtons.setDisabled("Hot Chocolate", isChecked)
            if (isChecked) {
                Toast.makeText(this, "Hot Chocolate has been disabled", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Hot Chocolate has been undisabled", Toast.LENGTH_SHORT).show()
            }
        }
        hotChocolateCheckbox.isChecked = DisabledButtons.isDisabled("Hot Chocolate")

        //Milk
        val milkCheckbox = findViewById<CheckBox>(R.id.milkCheckbox)
        milkCheckbox.setOnCheckedChangeListener { _, isChecked ->
            DisabledButtons.setDisabled("Milk", isChecked)
            if (isChecked) {
                Toast.makeText(this, "Milk has been disabled", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Milk has been undisabled", Toast.LENGTH_SHORT).show()
            }
        }
        milkCheckbox.isChecked = DisabledButtons.isDisabled("Milk")

        val menuButton = findViewById<Button>(R.id.menu)
        menuButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        navigation = findViewById(R.id.bottom_navigation)
        navigation.selectedItemId = R.id.unavailability

        navigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.orders -> {
                    val intent = Intent(this, EmployeeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.putExtra("userId", userId)
                    startActivity(intent)

                    finish()
                    true
                }
                else -> false
            }
        }
    }
}