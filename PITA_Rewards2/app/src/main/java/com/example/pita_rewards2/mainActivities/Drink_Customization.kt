package com.example.pita_rewards2.mainActivities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.text.TextLayoutResult
import androidx.core.content.IntentCompat
import com.example.pita_rewards2.R
import com.example.pita_rewards2.checkoutActivities.BasketActivity
import java.io.Serializable
import com.example.pita_rewards2.mainActivities.Drink_Menu
import kotlin.String

class Drink_Customization : AppCompatActivity() {
    private var userId: String? = null
    private val drinkData = mutableListOf<String>()
    private var selectedDrink: Drink_Menu? = null
    private var nameOfDrink: String = ""
    private var finalPrice: Double = 0.0
    private val selectedFruits = mutableListOf<String>()
    private val selectedAddons = mutableListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        val userId = intent.getStringExtra("userId")
        val points = intent.getStringExtra("points")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drink_customization)
        enableEdgeToEdge()

        val hot = findViewById<CheckBox>(R.id.hotOption)
        val iced = findViewById<CheckBox>(R.id.icedOption)

        hot.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) iced.isChecked = false
        }

        iced.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) hot.isChecked = false
        }

        findViewById<Button>(R.id.cancel_order)?.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            intent.putExtra("userId", userId)
            intent.putExtra("points", points)
            startActivity(intent)
            finish()
        }

        val resultText = findViewById<TextView>(R.id.resultText)
        val titleText = findViewById<TextView>(R.id.title)
        val submitButton = findViewById<Button>(R.id.submitButton)

        //Drink Size
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
            titleText.text = drink.name
            finalPrice = drink.price.toDouble()

            when (drink.name) {
                "Smoothie" -> Smoothie()
                "Matcha" -> Matcha()
                "Cold Brew" -> Cold_Brew()
                "Latte" -> Latte()
                "Mocha" -> Mocha()
                "Americano" -> Americano()
                "Lemonade" -> Lemonade()
                "Hot Chocolate" -> Hot_Chocolate()
                "Tea" -> Tea()
            }
        }

        submitButton?.setOnClickListener {
            //Confirm if smoothie is a smoothie
            if (selectedDrink?.name == "Smoothie") {
                if (!validateSmoothie()) {
                    Toast.makeText(this, "Please complete all required smoothie selections", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            } else if (selectedDrink?.name == "Matcha" || selectedDrink?.name == "Latte" || selectedDrink?.name == "Mocha" || selectedDrink?.name == "Cold Brew") {
                if (!validateCoffee()) {
                    Toast.makeText(this, "Please complete all required matcha selections", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (!validateHotIced()){
                    Toast.makeText(this, "Select a hot or iced option.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            } else if (selectedDrink?.name == "Americano") {
                if (!validateAmericano()) {
                    Toast.makeText(this, "Please complete all required americano selections", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            } else if (selectedDrink?.name == "Lemonade") {
                if (!validateLemon()) {
                    Toast.makeText(this, "Please complete all required lemonade selections", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            } else if (selectedDrink?.name == "Hot Chocolate") {
                if (!validateHotChoc()) {
                Toast.makeText(this, "Please complete all required hot chocolate selections", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (selectedDrink?.name == "Tea") {
                    if (!validateTea()) {
                        Toast.makeText(this, "Please complete all required tea selections", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                }
        }


            // Retrieve customization data
            val size = drinkData.find { it.startsWith("Size:") }?.substringAfter(": ") ?: "Medium"
            val milk = drinkData.find { it.startsWith("Milk:") }?.substringAfter(": ") ?: ""
            val sweetness = drinkData.find { it.startsWith("Sweetness:") }?.substringAfter(": ") ?: ""
            val image = selectedDrink?.image ?: R.drawable.drink_medium

            val detailsBuilder = StringBuilder()

            // If it's a Smoothie, add fruits, additions, and liquid data
            if (selectedDrink?.name == "Smoothie") {
                detailsBuilder.append("Fruits: ").append(selectedFruits.joinToString(", "))
                val additions = drinkData.filter { it.startsWith("Addition:") }.map { it.substringAfter(": ") }
                val liquid = drinkData.find { it.startsWith("Liquid:") }?.substringAfter(": ") ?: ""
                if (additions.isNotEmpty()) detailsBuilder.append("\nAdditions: ").append(additions.joinToString(", "))
                if (liquid.isNotEmpty()) detailsBuilder.append("\nLiquid: ").append(liquid)
            } else if (selectedDrink?.name == "Matcha"|| selectedDrink?.name == "Cold Brew") {
                val flavorSelected = drinkData.find { it.startsWith("Flavor:") }?.substringAfter(": ") ?: ""
                if (flavorSelected.isNotEmpty()) detailsBuilder.append("Flavor: ").append(flavorSelected)
            }
            val temp = when {
                findViewById<CheckBox>(R.id.hotOption)?.isChecked == true -> "Hot"
                findViewById<CheckBox>(R.id.icedOption)?.isChecked == true -> "Iced"
                else -> ""
            }
            // Add customization to the MainActivity order list
            val displayName = selectedDrink?.name ?: "Drink"
            val newOrder = ItemCustomization(
                drink = displayName,
                size = size,
                milk = milk,
                sweetness = sweetness,
                price = finalPrice,
                imageResourceId = image,
                temp = temp,
                extraDetails = detailsBuilder.toString(),
                quantity = 1
            )
            //Duplicate drink marker
            val existingOrder = MainActivity.customizations.find {
                it.drink == newOrder.drink &&
                        it.size == newOrder.size &&
                        it.milk == newOrder.milk &&
                        it.sweetness == newOrder.sweetness &&
                        it.temp == newOrder.temp &&
                        it.extraDetails == newOrder.extraDetails
            }
            if (existingOrder != null) {
                existingOrder.quantity += 1
            } else {
                MainActivity.customizations.add(newOrder)
            }
            Toast.makeText(this, "$nameOfDrink has been added to cart", Toast.LENGTH_SHORT).show()

            val basketIntent = Intent(this@Drink_Customization, BasketActivity::class.java)
            basketIntent.putExtra("userId", userId)
            startActivity(basketIntent)

            finish()

            //If Smoothie, add fruits, additions, and liquid data
            if (selectedDrink?.name == "Smoothie") {
                val fruits = drinkData.filter { it.startsWith("Fruit:") }.map { it.substringAfter(": ") }
                val additions = drinkData.filter { it.startsWith("Addition:") }.map { it.substringAfter(": ") }
                val liquid = drinkData.find { it.startsWith("Liquid:") }?.substringAfter(": ") ?: ""

                basketIntent.putExtra("fruits", ArrayList(fruits))
                basketIntent.putExtra("additions", ArrayList(additions))
                basketIntent.putExtra("liquid", liquid)

            } else if (selectedDrink?.name == "Matcha") {
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

            } else if (selectedDrink?.name == "Cold Brew") {
                val milkSelected = drinkData.find { it.startsWith("Milk:") }?.substringAfter(": ") ?: ""
                val flavorSelected = drinkData.find { it.startsWith("Flavor:") }?.substringAfter(": ") ?: ""
                val sweetSelected = drinkData.find { it.startsWith("Sweetness:") }?.substringAfter(": ") ?: ""
                val hot = findViewById<CheckBox>(R.id.hotOption).isChecked
                val iced = findViewById<CheckBox>(R.id.icedOption).isChecked
            // Show a toast with the updated order
            Toast.makeText(this, "$displayName has been added to cart", Toast.LENGTH_SHORT).show()

            // Retrieve userId from the current Intent (if available)
            val basketIntent = Intent(this@Drink_Customization, BasketActivity::class.java)
            basketIntent.putExtra("userId", userId)
                basketIntent.putExtra("milk", milkSelected)
                basketIntent.putExtra("flavor", flavorSelected)
                basketIntent.putExtra("sweetness", sweetSelected)
                basketIntent.putExtra("hot", hot)
                basketIntent.putExtra("iced", iced)

            } else if (selectedDrink?.name == "Latte") {
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

            } else if (selectedDrink?.name == "Mocha") {
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

            } else if (selectedDrink?.name == "Americano") {
                val hot = findViewById<CheckBox>(R.id.hotOption).isChecked
                val iced = findViewById<CheckBox>(R.id.icedOption).isChecked

                basketIntent.putExtra("hot", hot)
                basketIntent.putExtra("iced", iced)

            } else if (selectedDrink?.name == "Lemonade") {
                val flavorSelected = drinkData.find { it.startsWith("Flavor:") }?.substringAfter(": ") ?: ""

                basketIntent.putExtra("flavor", flavorSelected)

            } else if (selectedDrink?.name == "Hot Chocolate") {
                val milkSelected = drinkData.find { it.startsWith("Milk:") }?.substringAfter(": ") ?: ""
                val flavorSelected = drinkData.find { it.startsWith("Flavor:") }?.substringAfter(": ") ?: ""
                val sweetSelected = drinkData.find { it.startsWith("Sweetness:") }?.substringAfter(": ") ?: ""
                val chocolateSelected = drinkData.find {it.startsWith("Chocolate:")}?.substringAfter(": ")?: ""

                basketIntent.putExtra("milk", milkSelected)
                basketIntent.putExtra("flavor", flavorSelected)
                basketIntent.putExtra("sweetness", sweetSelected)
                basketIntent.putExtra("chocolate", chocolateSelected)

            } else if (selectedDrink?.name == "Tea") {
                val milkSelected = drinkData.find { it.startsWith("Milk:") }?.substringAfter(": ") ?: ""
                val teaSelected = drinkData.find { it.startsWith("Tea:") }?.substringAfter(": ") ?: ""

                basketIntent.putExtra("milk", milkSelected)
                basketIntent.putExtra("tea", teaSelected)

            }

            //Pass Basic drink info
            basketIntent.putExtra("drink", displayName)
            basketIntent.putExtra("size", size)
            basketIntent.putExtra("final_price", finalPrice)

            //Retrieve userId
            val userId = intent.getStringExtra("userId")
            if (userId != null) {
                basketIntent.putExtra("userId", userId)
            }

            //Start the BasketActivity then MainActivity with userId passed along
            startActivity(basketIntent)

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


        //Custom toast to display customized drink info
        val size = drinkData.find { it.startsWith("Size:") }?.substringAfter(": ") .orEmpty()
        val drink = selectedDrink?.name.orEmpty()
        val milk = drinkData.find { it.startsWith("Milk:") }?.substringAfter(": ") .orEmpty()
        val sweetness = drinkData.find { it.startsWith("Sweetness:") }?.substringAfter(": ") .orEmpty()
        val flavor = drinkData.find {it.startsWith("Flavor:")}?.substringAfter(": ") .orEmpty()
        val lemonFlavor = drinkData.find{it.startsWith("Lemonade Flavor:")}?.substringAfter(": ")?: ""
        val chocOption = drinkData.find{it.startsWith("Chocolate:")}?.substringAfter(": ")?: ""
        val tea = drinkData.find{it.startsWith("Tea:")}?.substringAfter(": ")?: ""
        val fruits = drinkData.find { it.startsWith("Fruit:") }?.substringAfter(": ")?.split(", ") ?: emptyList()
        val additions = drinkData.filter { it.startsWith("Addition:") }.map { it.substringAfter(": ") }
        val liquid = drinkData.find { it.startsWith("Liquid:") }?.substringAfter(": ") ?: ""


        //Adjusting price by drink size
        val basePrice = selectedDrink?.price?.toDouble() ?: 0.0
        finalPrice = basePrice
        when (size) {
            "Medium" -> finalPrice += 1.0
            "Large" -> finalPrice += 2.0
        }

        val orderString = StringBuilder("$$finalPrice $size $drink")

        if (fruits.isNotEmpty()) orderString.append(" (${fruits.joinToString(", ")}")
        if (additions.isNotEmpty()) orderString.append(" ${additions.joinToString(", ")}")
        if (liquid.isNotEmpty()) orderString.append(" $liquid)")
        if (milk.isNotEmpty()) orderString.append(" $milk")
        if (sweetness.isNotEmpty()) orderString.append(" $sweetness sweetness")
        if (flavor.isNotEmpty()) orderString.append(" $flavor")
        if (lemonFlavor.isNotEmpty()) orderString.append(" $lemonFlavor")
        if (chocOption.isNotEmpty()) orderString.append(" $chocOption")
        if (tea.isNotEmpty()) orderString.append(" $tea")

        findViewById<TextView>(R.id.resultText).text = orderString.toString()

    }
//Functions for each drink type
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
        findViewById<LinearLayout>(R.id.coffee_options)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.HotIcedLayout)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.lemonade_flavor)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.hotChoc)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.tea_layout)?.visibility = View.GONE
    }

    private fun Matcha() {
        findViewById<LinearLayout>(R.id.smoothie_options)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.lemonade_flavor)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.hotChoc)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.tea_layout)?.visibility = View.GONE

        val milkOptions = listOf("Whole Milk","Skimmed Milk","Almond Milk","Oat Milk")
        val milkLayout = findViewById<LinearLayout>(R.id.milkOptions)
        milkLayout?.removeAllViews()
        milkOptions.forEach { milk ->
            val btn = Button(this)
            btn.text = milk
            btn.setOnClickListener { updateDrinkData("Milk", milk) }
            milkLayout.addView(btn)
        }

        val flavors = listOf("No Flavor","Lavender","Vanilla","Honey","Cinnamon","Caramel","Toasted Marshmallow","Raspberry")
        val flavorLayout = findViewById<LinearLayout>(R.id.flavorOptions)
        flavorLayout.removeAllViews()
        flavors.forEach { flavor ->
            val btn = Button(this)
            btn.text = flavor
            btn.setOnClickListener { updateDrinkData("Flavor", flavor) }
            flavorLayout.addView(btn)
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
        findViewById<LinearLayout>(R.id.coffee_options)?.visibility = View.VISIBLE

        findViewById<LinearLayout>(R.id.fruitLayout)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.additionLayout)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.liquidLayout)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.milkOptions)?.visibility = View.GONE
    }
    private fun Cold_Brew() {
        findViewById<LinearLayout>(R.id.smoothie_options)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.lemonade_flavor)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.hotChoc)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.tea_layout)?.visibility = View.GONE

        val milkOptions = listOf("None","Whole Milk","Skimmed Milk","Almond Milk","Oat Milk")
        val milkLayout = findViewById<LinearLayout>(R.id.milkOptions)

        milkLayout.removeAllViews()
        milkOptions.forEach { milk ->
        val btn = Button(this)
        btn.text = milk
        btn.setOnClickListener { updateDrinkData("Milk", milk) }
        milkLayout.addView(btn)
        }

        val flavors = listOf("No Flavor","Lavender","Vanilla","Honey","Cinnamon","Caramel","Toasted Marshmallow","Raspberry")
        val flavorLayout = findViewById<LinearLayout>(R.id.flavorOptions)
        flavorLayout.removeAllViews()
        flavors.forEach { flavor ->
            val btn = Button(this)
            btn.text = flavor
            btn.setOnClickListener { updateDrinkData("Flavor", flavor) }
            flavorLayout.addView(btn)
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
    }

    private fun Latte(){
        findViewById<LinearLayout>(R.id.smoothie_options)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.lemonade_flavor)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.hotChoc)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.tea_layout)?.visibility = View.GONE

        val milkOptions = listOf("Whole Milk","Skimmed Milk","Almond Milk","Oat Milk")
        val milkLayout = findViewById<LinearLayout>(R.id.milkOptions)

        milkLayout.removeAllViews()
        milkOptions.forEach { milk ->
            val btn = Button(this)
            btn.text = milk
            btn.setOnClickListener { updateDrinkData("Milk", milk) }
            milkLayout.addView(btn)
        }

        val flavors = listOf("No Flavor","Lavender","Vanilla","Honey","Cinnamon","Caramel","Toasted Marshmallow","Raspberry")
        val flavorLayout = findViewById<LinearLayout>(R.id.flavorOptions)
        flavorLayout.removeAllViews()
        flavors.forEach { flavor ->
            val btn = Button(this)
            btn.text = flavor
            btn.setOnClickListener { updateDrinkData("Flavor", flavor) }
            flavorLayout.addView(btn)
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

    }

    private fun Mocha(){

        findViewById<LinearLayout>(R.id.smoothie_options)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.lemonade_flavor)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.hotChoc)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.tea_layout)?.visibility = View.GONE

        val milkOptions = listOf("Whole Milk","Skimmed Milk","Almond Milk","Oat Milk")
        val milkLayout = findViewById<LinearLayout>(R.id.milkOptions)

        milkLayout.removeAllViews()
        milkOptions.forEach { milk ->
            val btn = Button(this)
            btn.text = milk
            btn.setOnClickListener { updateDrinkData("Milk", milk) }
            milkLayout.addView(btn)
        }

        val flavors = listOf("No Flavor","Lavender","Vanilla","Honey","Cinnamon","Caramel","Toasted Marshmallow","Raspberry")
        val flavorLayout = findViewById<LinearLayout>(R.id.flavorOptions)
        flavorLayout.removeAllViews()
        flavors.forEach { flavor ->
            val btn = Button(this)
            btn.text = flavor
            btn.setOnClickListener { updateDrinkData("Flavor", flavor) }
            flavorLayout.addView(btn)
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

    }

    private fun Americano(){
        findViewById<LinearLayout>(R.id.smoothie_options)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.coffee_options)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.hotChoc)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.lemonade_flavor)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.tea_layout)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.HotIcedLayout)?.visibility = View.VISIBLE

    }

    private fun Lemonade(){

        findViewById<LinearLayout>(R.id.smoothie_options)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.coffee_options)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.HotIcedLayout)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.hotChoc)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.tea_layout)?.visibility = View.GONE

        val flavorOptions = listOf("No Flavor", "Lavender", "Raspberry", "Honey", "Strawberry", "Blackberry", "Blueberry", "Pomegranate")
        val flavorLayout = findViewById<LinearLayout>(R.id.lemonadeFlavors)
        flavorLayout.removeAllViews()
        flavorOptions.forEach { flavor ->
            val btn = Button(this)
            btn.text = flavor
            btn.setOnClickListener { updateDrinkData("Flavor", flavor) }
            flavorLayout.addView(btn)
        }

    }

    private fun Hot_Chocolate(){

        findViewById<LinearLayout>(R.id.smoothie_options)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.HotIcedLayout)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.lemonade_flavor)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.tea_layout)?.visibility = View.GONE

        val chocolateOptions = listOf("Milk", "White", "Dark")
        val chocolateLayout = findViewById<LinearLayout>(R.id.choc_options)
        chocolateLayout.removeAllViews()
        chocolateOptions.forEach { flavor ->
            val btn = Button(this)
            btn.text = flavor
            btn.setOnClickListener { updateDrinkData("Flavor", flavor) }
            chocolateLayout.addView(btn)
        }

        val milkOptions = listOf("Whole Milk","Skimmed Milk","Almond Milk","Oat Milk")
        val milkLayout = findViewById<LinearLayout>(R.id.milkOptions)

        milkLayout.removeAllViews()
        milkOptions.forEach { milk ->
            val btn = Button(this)
            btn.text = milk
            btn.setOnClickListener { updateDrinkData("Milk", milk) }
            milkLayout.addView(btn)
        }

        val flavors = listOf("No Flavor","Lavender","Vanilla","Honey","Cinnamon","Caramel","Toasted Marshmallow","Raspberry")
        val flavorLayout = findViewById<LinearLayout>(R.id.flavorOptions)
        flavorLayout.removeAllViews()
        flavors.forEach { flavor ->
            val btn = Button(this)
            btn.text = flavor
            btn.setOnClickListener { updateDrinkData("Flavor", flavor) }
            flavorLayout.addView(btn)
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
    }

    private fun Tea(){

        findViewById<LinearLayout>(R.id.smoothie_options)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.HotIcedLayout)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.lemonade_flavor)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.hotChoc)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.coffee_options)?.visibility = View.GONE


        val milkOptions = listOf("None","Whole Milk","Skimmed Milk","Almond Milk","Oat Milk")
        val milkLayout = findViewById<LinearLayout>(R.id.milk_options2)

        milkLayout.removeAllViews()
        milkOptions.forEach { milk ->
            val btn = Button(this)
            btn.text = milk
            btn.setOnClickListener { updateDrinkData("Milk", milk) }
            milkLayout.addView(btn)
        }

        val teaOptions = listOf("Green Tea", "Earl Grey", "Peppermint Tea", "Ginger Tea", "Chamomile Tea", "Hibiscus Tea", "White Tea")
        val teaLayout = findViewById<LinearLayout>(R.id.tea)
        teaLayout.removeAllViews()
        teaOptions.forEach { tea ->
            val btn = Button(this)
            btn.text = tea
            btn.setOnClickListener { updateDrinkData("Tea", tea) }
            teaLayout.addView(btn)
        }

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
        val flavor = drinkData.find {it.startsWith("Flavor:")}?.substringAfter(": ") ?: ""
        val lemonFlavor = drinkData.find{it.startsWith("Lemonade Flavor:")}?.substringAfter(": ")?: ""
        val chocOption = drinkData.find{it.startsWith("Chocolate:")}?.substringAfter(": ")?: ""
        val tea = drinkData.find{it.startsWith("Tea:")}?.substringAfter(": ")?: ""
        val fruits = drinkData.find { it.startsWith("Fruit:") }?.substringAfter(": ")?.split(", ") ?: emptyList()
        val additions = drinkData.filter { it.startsWith("Addition:") }.map { it.substringAfter(": ") }
        val liquid = drinkData.find { it.startsWith("Liquid:") }?.substringAfter(": ") ?: ""
        val drinkName = selectedDrink?.name ?: ""

        var price = selectedDrink?.price?.toDouble() ?: 0.0
        when(size) {
            "Medium" -> price += 1
            "Large" -> price += 2
        }

        finalPrice = price

        val orderString = StringBuilder("$$finalPrice $size $drinkName")
        if (fruits.isNotEmpty()) orderString.append(" (${fruits.joinToString(", ")}")
        if (additions.isNotEmpty()) orderString.append(" ${additions.joinToString(", ")}")
        if (liquid.isNotEmpty()) orderString.append(" $liquid)")
        if (milk.isNotEmpty()) orderString.append(" $milk")
        if (sweetness.isNotEmpty()) orderString.append(" $sweetness sweetness")
        if (flavor.isNotEmpty()) orderString.append(" $flavor")
        if (lemonFlavor.isNotEmpty()) orderString.append(" $lemonFlavor")
        if (chocOption.isNotEmpty()) orderString.append(" $chocOption")
        if (tea.isNotEmpty()) orderString.append(" $tea")

        findViewById<TextView>(R.id.resultText).text = orderString.toString()

    }

    private fun validateSmoothie(): Boolean {
        val sizeSelected = drinkData.find { it.startsWith("Size:") }?.substringAfter(": ")
        val liquidSelected = drinkData.find { it.startsWith("Liquid:") }?.substringAfter(": ")
        if (sizeSelected.isNullOrEmpty() || selectedFruits.size < 2 ||liquidSelected.isNullOrEmpty()) {
            Toast.makeText(this, "Please select a size, at least 2 fruits, and 1 liquid.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun validateCoffee(): Boolean {
        val milkSelected = drinkData.find { it.startsWith("Milk:") }?.substringAfter(": ")
        val sizeSelected = drinkData.find { it.startsWith("Size:") }?.substringAfter(": ")
        val sweetnessSelected = drinkData.find { it.startsWith("Sweetness:") }?.substringAfter(": ")
        val flavorSelected = drinkData.find { it.startsWith("Flavor:") }?.substringAfter(": ")

        if (milkSelected.isNullOrEmpty() || sizeSelected.isNullOrEmpty() || sweetnessSelected.isNullOrEmpty() || flavorSelected.isNullOrEmpty()) {
            Toast.makeText(this, "Please select a size, milk option, sweetness level, and flavor.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun validateHotIced():Boolean {
        val hot = findViewById<CheckBox>(R.id.hotOption)
        val iced = findViewById<CheckBox>(R.id.icedOption)

        if (!iced.isChecked && !hot.isChecked){
            Toast.makeText(this, "Please selected a hot or iced option", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun validateAmericano(): Boolean {
        val sizeSelected = drinkData.find { it.startsWith("Size:") }?.substringAfter(": ")
        val hot = findViewById<CheckBox>(R.id.hotOption)
        val iced = findViewById<CheckBox>(R.id.icedOption)

        if (iced.isChecked){
            hot.isEnabled = false
        } else if (hot.isChecked){
            iced.isEnabled = false
        }

        if (sizeSelected.isNullOrEmpty()) {
            if (!iced.isChecked || !hot.isChecked){
                Toast.makeText(this, "Please selected a hot or iced option", Toast.LENGTH_SHORT).show()
            }
            Toast.makeText(this, "Please select a size.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun validateLemon(): Boolean {
        val sizeSelected = drinkData.find { it.startsWith("Size:") }?.substringAfter(": ")
        val flavorSelected = drinkData.find { it.startsWith("Lemonade Flavor:") }?.substringAfter(": ")

        if (sizeSelected.isNullOrEmpty() || flavorSelected.isNullOrEmpty()) {
            Toast.makeText(this, "Please select a size and a flavor.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun validateHotChoc(): Boolean {
        val sizeSelected = drinkData.find { it.startsWith("Size:") }?.substringAfter(": ")
        val flavorSelected = drinkData.find { it.startsWith("Flavor:") }?.substringAfter(": ")
        val milkSelected = drinkData.find { it.startsWith("Milk:") }?.substringAfter(": ")
        val sweetnessSelected = drinkData.find { it.startsWith("Sweetness:") }?.substringAfter(": ")

        if (sizeSelected.isNullOrEmpty() || flavorSelected.isNullOrEmpty() || milkSelected.isNullOrEmpty() || sweetnessSelected.isNullOrEmpty()) {
            Toast.makeText(this, "Please select a size, milk, flavor, and choose sweetness level.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun validateTea(): Boolean {
        val sizeSelected = drinkData.find { it.startsWith("Size:") }?.substringAfter(": ")
        val teaSelected = drinkData.find { it.startsWith("Tea:") }?.substringAfter(": ")

        if (sizeSelected.isNullOrEmpty() || teaSelected.isNullOrEmpty()) {
            Toast.makeText(this, "Please select a size and a tea.", Toast.LENGTH_SHORT).show()
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
    val flavor: String = "",
    val price: Double = 0.0,
    var customerName: String = "",
    var location: String = "",
    val imageResourceId: Int,
    val temp: String = "",
    val extraDetails: String = "",
    var quantity: Int = 1
) : Serializable