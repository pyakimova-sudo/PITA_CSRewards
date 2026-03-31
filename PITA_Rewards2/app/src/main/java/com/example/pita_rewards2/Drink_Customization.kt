package com.example.pita_rewards2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.Serializable

//TODO:Add conditional for drink types(smoothies don't get milk/sweet)
//NEED to call drink type values and update menu accordingly
//TODO: send back to main after adding to basket
class Drink_Customization : AppCompatActivity() {
    private val drinkData = mutableListOf<String>()
    private var selectedDrink: Drink_Menu? = null  // class-level variable
    private var nameOfDrink: String = ""//intent.getStringExtra("drink")

    private var finalPrice: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drink_customization)

        val resultText = findViewById<TextView>(R.id.resultText)
        val titleText = findViewById<TextView>(R.id.title)
        val submitButton = findViewById<Button>(R.id.submitButton)

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

        //Submit order
        submitButton.setOnClickListener {
            val intent = Intent(this@Drink_Customization, MainActivity::class.java)
            val size = drinkData.find { it.startsWith("Size:") }?.substringAfter(": ") ?: ""
            val drink = selectedDrink?.name ?: ""
            nameOfDrink = selectedDrink?.name.orEmpty()
            val milk = drinkData.find { it.startsWith("Milk:") }?.substringAfter(": ") ?: ""
            val sweetness = drinkData.find { it.startsWith("Sweetness:") }?.substringAfter(": ") ?: ""
            MainActivity.order.add(nameOfDrink)
            MainActivity.customizations.add(ItemCustomization(nameOfDrink, size, milk, sweetness))
            Toast.makeText(this, "$nameOfDrink has been added to cart", Toast.LENGTH_SHORT).show()


            // Pass all 4–5 values
            intent.putExtra("drink", drink)
            intent.putExtra("size", size)
            intent.putExtra("milk", milk)
            intent.putExtra("sweetness", sweetness)
            intent.putExtra("final_price", finalPrice)

            startActivity(intent)
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

        //Adjusting price by drink size
        //TODO update price for other
        val basePrice = selectedDrink?.price ?: 0
        var finalPrice = basePrice

        when (size) {
            "Medium" -> finalPrice += 1
            "Large" -> finalPrice += 2
        }

        val resultString = "$$finalPrice $size $drink" +
                (if (milk.isNotEmpty() && milk != "None") " with $milk milk " else "") +
                (if (sweetness.isNotEmpty()) " and $sweetness sweetness" else "")
        resultText.text = resultString
        Toast.makeText(this, resultString, Toast.LENGTH_SHORT).show()
    }
}

data class ItemCustomization(
    val drink: String,
    val size: String = "",
    val milk: String = "",
    val sweetness: String = ""
) : Serializable