package com.example.pita_rewards2

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

//TODO:Add conditional for drink types(smoothies don't get milk/sweet)
//NEED to call drink type values and update menu accordingly
//TODO:a add multiple button
class Drink_Customization : AppCompatActivity() {
    private val drinkData = mutableListOf<String>()
    private var selectedDrink: Drink_Menu? = null  // class-level variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drink_customization)

        val resultText = findViewById<TextView>(R.id.resultText)
        val titleText = findViewById<TextView>(R.id.title)

        //????
        selectedDrink = intent.getSerializableExtra("selected_drink") as? Drink_Menu

        //Header+data for drink info list
        selectedDrink?.let {
            drinkData.add(it.name)
            titleText.text = "Customize Your ${it.name}"
            /*TODO: should there be default values
            *  else force size choice before order*/
            resultText.text = it.name
        }

        //Drink sizes
        val sizeButtons = listOf(
            findViewById<Button>(R.id.sizeSmall) to "Small",
            findViewById<Button>(R.id.sizeMedium) to "Medium",
            findViewById<Button>(R.id.sizeLarge) to "Large"
        )
        sizeButtons.forEach { (button, value) ->
            button.setOnClickListener {
                updateSelection("Size", value, resultText)
            }
        }

        //Milk Options
        val milkButtons = listOf(
            findViewById<Button>(R.id.milkNone) to "None",
            findViewById<Button>(R.id.milkRegular) to "Regular",
            findViewById<Button>(R.id.milkSoy) to "Soy"
        )
        milkButtons.forEach { (button, value) ->
            button.setOnClickListener {
                updateSelection("Milk", value, resultText)
            }
        }

        //Sweetness options
        val sweetButtons = listOf(
            findViewById<Button>(R.id.sweetLow) to "Low",
            findViewById<Button>(R.id.sweetMedium) to "Medium",
            findViewById<Button>(R.id.sweetHigh) to "High"
        )
        sweetButtons.forEach { (button, value) ->
            button.setOnClickListener {
                updateSelection("Sweetness", value, resultText)
            }
        }
    }

    private fun updateSelection(category: String, value: String, resultText: TextView) {
        val existingIndex = drinkData.indexOfFirst { it.startsWith("$category:") }
        val entry = "$category: $value"
        if (existingIndex >= 0) {
            drinkData[existingIndex] = entry
        } else {
            drinkData.add(entry)
        }
//TODO:was made for testing will switch to intent for basket
        //Custom toast to display customized drink info
        val size = drinkData.find { it.startsWith("Size:") }?.substringAfter(": ") .orEmpty()
        val drink = selectedDrink?.name.orEmpty()
        val milk = drinkData.find { it.startsWith("Milk:") }?.substringAfter(": ") .orEmpty()
        val sweetness = drinkData.find { it.startsWith("Sweetness:") }?.substringAfter(": ") .orEmpty()
        val resultString = "$size $drink" +
                (if (milk.isNotEmpty() && milk != "None") " with $milk milk" else "") +
                (if (sweetness.isNotEmpty()) " and $sweetness sweetness" else "")
        resultText.text = resultString
        Toast.makeText(this, resultString, Toast.LENGTH_SHORT).show()
    }
}