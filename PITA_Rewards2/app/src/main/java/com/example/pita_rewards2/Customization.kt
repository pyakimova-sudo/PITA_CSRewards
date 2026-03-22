package com.example.pita_rewards2

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class Customization : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drink_customization)

        // Spinner setup (Milk options)
        val milkSpinner = findViewById<Spinner>(R.id.milkSpinner)
        val milkOptions = arrayOf("Whole", "Oat", "Almond", "Soy")
        val milkAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, milkOptions)
        milkSpinner.adapter = milkAdapter

        // Spinner setup (Size options)
        val sizeSpinner = findViewById<Spinner>(R.id.sizeSpinner)
        val sizeOptions = arrayOf("Small", "Medium", "Large")
        val sizeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sizeOptions)
        sizeSpinner.adapter = sizeAdapter

        val sweetnessSpinner = findViewById<Spinner>(R.id.sweetnessLevelSpinner)
        val sweetnessLevelOptions = arrayOf("normal", "half-sweet", "death")
        val sweetnessLevelAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sweetnessLevelOptions)
        sweetnessSpinner.adapter = sweetnessLevelAdapter

        // Button click to save custom drink
        val submitButton = findViewById<Button>(R.id.submitDrink)

        submitButton.setOnClickListener {
            val name = findViewById<EditText>(R.id.drinkNameInput).text.toString()

            // Get selected size from Spinner
            val selectedSize = sizeSpinner.selectedItem.toString()

            // Get selected milk type from Spinner
            val selectedMilk = milkSpinner.selectedItem.toString()

            // Get sweetness level from SeekBar
            val sweetness = sweetnessSpinner.selectedItem.toString()

            // Combine all information into one string
            val result = "$name | $selectedSize | $selectedMilk | Sweetness: $sweetness "

            // Show result in a Toast
            Toast.makeText(this, result, Toast.LENGTH_LONG).show()

            // Return customized drink to MainActivity
            val intent = Intent()
            intent.putExtra("drink", result)
            setResult(RESULT_OK, intent)

            finish()
        }
    }
}