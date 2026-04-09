package com.example.pita_rewards2.mainActivities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import com.example.pita_rewards2.R
import java.io.Serializable

class Drink_Customization : AppCompatActivity() {
    private val drinkData = mutableListOf<String>()
    private var selectedDrink: Drink_Menu? = null
    private var nameOfDrink: String = ""//intent.getStringExtra("drink")
    //private var milkChosen: String = (intent.getStringExtra("milk")).toString()

    private var finalPrice: Int = 0
    private val selectedFruits = mutableListOf<String>()
    private val selectedAddons = mutableListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drink_customization)
        enableEdgeToEdge()


        findViewById<Button>(R.id.cancel_order)?.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val resultText = findViewById<TextView>(R.id.resultText)
        val titleText = findViewById<TextView>(R.id.title)
        val submitButton = findViewById<Button>(R.id.submitButton)

        // Milk spinner
        val milk_spinner: Spinner = findViewById(R.id.choose_milk)
        milk_spinner?.let { spinner ->
            ArrayAdapter.createFromResource(
                this, R.array.milks, android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                milk_spinner.adapter = adapter
            }
            milk_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedMilk = parent?.getItemAtPosition(position).toString()
                    updateSelection("Milk", selectedMilk, resultText)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        // Sweetness Spinner
        val sweetness_spinner: Spinner = findViewById(R.id.choose_sweetness)
        sweetness_spinner?.let { spinner ->
            ArrayAdapter.createFromResource(
                this, R.array.sweetness, android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                sweetness_spinner.adapter = adapter
            }
            sweetness_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedSweetness = parent?.getItemAtPosition(position).toString()
                    updateSelection("Sweetness", selectedSweetness, resultText)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        // Size Buttons
        val sizeButtons = listOf(
            findViewById<ImageButton>(R.id.sizeSmall) to "Small",
            findViewById<ImageButton>(R.id.sizeMedium) to "Medium",
            findViewById<ImageButton>(R.id.sizeLarge) to "Large"
        )
        sizeButtons.forEach { (button, value) ->
            button.setOnClickListener {
                updateSelection("Size", value, resultText)
            }
        }

        selectedDrink = IntentCompat.getSerializableExtra(intent, "selected_drink", Drink_Menu::class.java)

        selectedDrink?.let { drink ->
            titleText.text = "${drink.name}"
            resultText.text = drink.name

            when (drink.Drink_Type) {
                "Smoothie" -> Smoothie()
                "Matcha" -> Matcha()
                "Cold Brew" -> Cold_Brew()
                "Regular" -> Regular()
            }
        }

        submitButton?.setOnClickListener {
            // Check if it's a Smoothie and validate selections
            if (selectedDrink?.Drink_Type == "Smoothie") {
                if (!validateSmoothie() || !validateSmoothie()) {
                    Toast.makeText(this, "Please complete all required smoothie selections", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            // Create an Intent to go back to MainActivity (basket intent)
            val basketIntent = Intent(this@Drink_Customization, MainActivity::class.java)

            // Retrieve customization data
            val size = drinkData.find { it.startsWith("Size:") }?.substringAfter(": ") ?: ""
            val drink = selectedDrink?.name ?: ""
            nameOfDrink = selectedDrink?.name.orEmpty()
            val milk = drinkData.find { it.startsWith("Milk:") }?.substringAfter(": ") ?: ""
            val sweetness = drinkData.find { it.startsWith("Sweetness:") }?.substringAfter(": ") ?: ""

            // Add customization to the MainActivity order list
            MainActivity.order.add(nameOfDrink)
            MainActivity.customizations.add(ItemCustomization(nameOfDrink, size, milk, sweetness, price = finalPrice))

            // Show a toast with the updated order
            Toast.makeText(this, "$nameOfDrink has been added to cart", Toast.LENGTH_SHORT).show()

            // If it's a Smoothie, add fruits, additions, and liquid data
            if (selectedDrink?.Drink_Type == "Smoothie") {
                val fruits = drinkData.filter { it.startsWith("Fruit:") }.map { it.substringAfter(": ") }
                val additions = drinkData.filter { it.startsWith("Addition:") }.map { it.substringAfter(": ") }
                val liquid = drinkData.find { it.startsWith("Liquid:") }?.substringAfter(": ") ?: ""
                basketIntent.putExtra("fruits", ArrayList(fruits))
                basketIntent.putExtra("additions", ArrayList(additions))
                basketIntent.putExtra("liquid", liquid)
            } else if (selectedDrink?.Drink_Type == "Matcha") {
                val milkSelected = drinkData.find { it.startsWith("Milk:") }?.substringAfter(": ") ?: ""
                val flavorSelected = drinkData.find { it.startsWith("Flavor:") }?.substringAfter(": ") ?: ""
                val sweetSelected = drinkData.find { it.startsWith("Sweetness:") }?.substringAfter(": ") ?: ""
                val hot = findViewById<CheckBox>(R.id.hotOption).isChecked
                val iced = findViewById<CheckBox>(R.id.icedOption).isChecked

                basketIntent.putExtra("milk", milkSelected)
                basketIntent.putExtra("flavor", flavorSelected)
                basketIntent.putExtra("sweetness", sweetSelected)
                basketIntent.putExtra("hot", hot)
                basketIntent.putExtra("iced", iced)
            } else if (selectedDrink?.Drink_Type == "Cold Brew") {
                val milkSelected = drinkData.find { it.startsWith("Milk:") }?.substringAfter(": ") ?: ""
                val flavorSelected = drinkData.find { it.startsWith("Flavor:") }?.substringAfter(": ") ?: ""
                val sweetSelected = drinkData.find { it.startsWith("Sweetness:") }?.substringAfter(": ") ?: ""
                val hot = findViewById<CheckBox>(R.id.hotOption).isChecked
                val iced = findViewById<CheckBox>(R.id.icedOption).isChecked

                basketIntent.putExtra("milk", milkSelected)
                basketIntent.putExtra("flavor", flavorSelected)
                basketIntent.putExtra("sweetness", sweetSelected)
                basketIntent.putExtra("hot", hot)
                basketIntent.putExtra("iced", iced)
            }

            // Add the basic drink info like name, size, and final price
            basketIntent.putExtra("drink", drink)
            basketIntent.putExtra("size", size)
            basketIntent.putExtra("final_price", finalPrice)

            // Retrieve userId from the current Intent (if available)
            val userId = intent.getStringExtra("userId")
            if (userId != null) {
                basketIntent.putExtra("userId", userId)  // Pass the userId to MainActivity
            }

            // Start the BasketActivity or MainActivity with userId passed along
            startActivity(basketIntent)

            // Redirect back to MainActivity after submitting the order
            val mainIntent = Intent(this@Drink_Customization, MainActivity::class.java)
            mainIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

            // Ensure userId is included when returning to MainActivity
            if (userId != null) {
                mainIntent.putExtra("userId", userId)
            }

            startActivity(mainIntent)

            // Finish the current activity
            finish()
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
        finalPrice = basePrice

        when (size) {
            "Medium" -> finalPrice += 1
            "Large" -> finalPrice += 2
        }

        val resultString = "$$finalPrice $size $drink" +
                (if (milk.isNotEmpty() && milk != "None") " with $milk milk " else "") +
                (if (sweetness.isNotEmpty()) " and $sweetness sweetness" else "")
        resultText.text = resultString
        //Toast.makeText(this, resultString, Toast.LENGTH_SHORT).show()
    }

    private fun Smoothie() {
        val fruits = listOf("Banana","Strawberry","Blueberry","Kiwi","Mango","Pineapple","Raspberry","Pear","Peach")
        val fruitLayout = findViewById<LinearLayout>(R.id.fruitLayout)
        fruitLayout.removeAllViews()
        fruits.forEach { fruit ->
            val btn = Button(this)
            btn.text = fruit
            btn.setOnClickListener {
                if (selectedFruits.contains(fruit)) selectedFruits.remove(fruit)
                else if (selectedFruits.size < 4) selectedFruits.add(fruit)
                else Toast.makeText(this, "You can select up to 4 fruits", Toast.LENGTH_SHORT).show()
                updateDrinkData("Fruit", selectedFruits.joinToString(", "))
            }
            fruitLayout.addView(btn)
        }

        val additions = listOf("Oats","Peanut Butter","Chia Seeds","Protein Powder","Greek Yoghurt","Dairy Free Yoghurt")
        val additionLayout = findViewById<LinearLayout>(R.id.additionLayout)
        additionLayout.removeAllViews()
        additions.forEach { addition ->
            val btn = Button(this)
            btn.text = addition
            btn.setOnClickListener {
                if (selectedAddons.contains(addition)) selectedAddons.remove(addition)
                else if (selectedAddons.size < 2) selectedAddons.add(addition)
                else Toast.makeText(this, "You can select up to 2 additional ingredients", Toast.LENGTH_SHORT).show()
                updateDrinkData("Addition", selectedAddons.joinToString(", "))
            }
            additionLayout.addView(btn)
        }

        val liquids = listOf("Whole Milk","Skimmed Milk","Soy Milk","Coconut Milk","Oat Milk","Water","Apple Juice","Orange Juice")
        val liquidLayout = findViewById<LinearLayout>(R.id.liquidLayout)
        liquidLayout.removeAllViews()
        liquids.forEach { liquid ->
            val btn = Button(this)
            btn.text = liquid
            btn.setOnClickListener { updateDrinkData("Liquid", liquid) }
            liquidLayout.addView(btn)
        }

        findViewById<LinearLayout>(R.id.milkOptions)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.sweetOptions)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.matchaLayout)?.visibility = View.GONE
    }

    private fun Matcha() {
        val milkOptions = listOf("Whole Milk","Skimmed Milk","Almond Milk","Oat Milk")
        //val milkLayout = findViewById<LinearLayout>(R.id.matchaMilkLayout)

       // milkLayout.removeAllViews()
        milkOptions.forEach { milk ->
            val btn = Button(this)
            btn.text = milk
            btn.setOnClickListener { updateDrinkData("Milk", milk) }
           // milkLayout.addView(btn)
        }

        val flavors = listOf("No Flavor","Lavender","Vanilla","Honey","Cinnamon","Caramel","Toasted Marshmallow","Raspberry")
       // val flavorLayout = findViewById<LinearLayout>(R.id.matchaFlavorLayout)
       // flavorLayout.removeAllViews()
        flavors.forEach { flavor ->
            val btn = Button(this)
            btn.text = flavor
            btn.setOnClickListener { updateDrinkData("Flavor", flavor) }
          //  flavorLayout.addView(btn)
        }

        val sweetLevels = listOf("100%","75%","50%","25%","0%")
        val sweetLayout = findViewById<LinearLayout>(R.id.sweetOptions)
        sweetLayout.removeAllViews()
        sweetLevels.forEach { sweet ->
            val btn = Button(this)
            btn.text = sweet
            btn.setOnClickListener { updateDrinkData("Sweetness", sweet) }
            sweetLayout.addView(btn)
        }

        findViewById<CheckBox>(R.id.hotOption)?.visibility = View.VISIBLE
        findViewById<CheckBox>(R.id.icedOption)?.visibility = View.VISIBLE
        findViewById<CheckBox>(R.id.matchaLayout)?.visibility = View.VISIBLE

        findViewById<LinearLayout>(R.id.fruitLayout)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.additionLayout)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.liquidLayout)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.milkOptions)?.visibility = View.GONE
    }
//Cold Brew
    private fun Cold_Brew() {
        val milkOptions = listOf("Whole Milk","Skimmed Milk","Almond Milk","Oat Milk")
        //val milkLayout = findViewById<LinearLayout>(R.id.matchaMilkLayout)

       // milkLayout.removeAllViews()
        milkOptions.forEach { milk ->
            val btn = Button(this)
            btn.text = milk
            btn.setOnClickListener { updateDrinkData("Milk", milk) }
           // milkLayout.addView(btn)
        }

        val flavors = listOf("No Flavor","Lavender","Vanilla","Honey","Cinnamon","Caramel","Toasted Marshmallow","Raspberry")
        //val flavorLayout = findViewById<LinearLayout>(R.id.matchaFlavorLayout)
       // flavorLayout.removeAllViews()
        flavors.forEach { flavor ->
            val btn = Button(this)
            btn.text = flavor
            btn.setOnClickListener { updateDrinkData("Flavor", flavor) }
         //   flavorLayout.addView(btn)
        }

        val sweetLevels = listOf("100%","75%","50%","25%","0%")
        val sweetLayout = findViewById<LinearLayout>(R.id.sweetOptions)
        sweetLayout.removeAllViews()
        sweetLevels.forEach { sweet ->
            val btn = Button(this)
            btn.text = sweet
            btn.setOnClickListener { updateDrinkData("Sweetness", sweet) }
            sweetLayout.addView(btn)
        }

        findViewById<CheckBox>(R.id.hotOption)?.visibility = View.VISIBLE
        findViewById<CheckBox>(R.id.icedOption)?.visibility = View.VISIBLE

        findViewById<LinearLayout>(R.id.fruitLayout)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.additionLayout)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.liquidLayout)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.milkOptions)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.sweetOptions)?.visibility = View.GONE
    }

    private fun Regular() {
        findViewById<LinearLayout>(R.id.milkOptions)?.visibility = View.VISIBLE
        findViewById<LinearLayout>(R.id.sweetOptions)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.fruitLayout)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.additionLayout)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.liquidLayout)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.matchaLayout)?.visibility = View.GONE
    }

    private fun updateDrinkData(category: String, value: String) {
        drinkData.removeAll { it.startsWith("$category:") }
        drinkData.add("$category: $value")
        showResultOrder()
    }

    private fun showResultOrder() {
        val size = drinkData.find { it.startsWith("Size:") }?.substringAfter(": ") ?: ""
        val milk = drinkData.find { it.startsWith("Milk:") }?.substringAfter(": ") ?: ""
        val sweetness = drinkData.find { it.startsWith("Sweetness:") }?.substringAfter(": ") ?: ""
        val fruits = drinkData.find { it.startsWith("Fruit:") }?.substringAfter(": ")?.split(", ") ?: emptyList()
        val additions = drinkData.filter { it.startsWith("Addition:") }.map { it.substringAfter(": ") }
        val liquid = drinkData.find { it.startsWith("Liquid:") }?.substringAfter(": ") ?: ""
        val drinkName = selectedDrink?.name ?: ""

        var price = selectedDrink?.price ?: 0
        when(size) {
            "Medium" -> price += 1
            "Large" -> price += 2
        }
        finalPrice = price

        val orderString = StringBuilder("$$price $size $drinkName")
        if (fruits.isNotEmpty()) orderString.append(" with ${fruits.joinToString(", ")}")
        if (additions.isNotEmpty()) orderString.append(" + ${additions.joinToString(", ")}")
        if (milk.isNotEmpty()) orderString.append(" with $milk milk")
        if (sweetness.isNotEmpty()) orderString.append(" sweetness")
        if (liquid.isNotEmpty()) orderString.append(" and $liquid")

        findViewById<TextView>(R.id.resultText).text = orderString.toString()
        //Toast.makeText(this, orderString.toString(), Toast.LENGTH_SHORT).show()
    }

    private fun validateSmoothie(): Boolean {
        val liquidSelected = drinkData.find { it.startsWith("Liquid:") }?.substringAfter(": ")
        if (selectedFruits.size < 2 ||liquidSelected.isNullOrEmpty()) {
            Toast.makeText(this, "Please select at least 2 fruits and 1 liquid", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}

data class ItemCustomization(
    val drink: String,
    val size: String = "",
    val milk: String = "",
    val sweetness: String = "",
    val price: Int = 0,
    var customerName: String = "",
    var location: String = ""
) : Serializable