package com.example.pita_rewards2

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.example.pita_rewards2.databinding.ActivityMainBinding
import com.google.firebase.database.*
import android.widget.Spinner
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.Button
import android.widget.TextView

class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var navigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        //Fluid button mapping for all Drink_Menu items
        /*
        TODO conditionally divide drinks by type
         to better organize menu screen(aestetics)*/
        val buttonContainer = binding.drinkButtonContainer
        Drink_Menu.defaultDrinks.forEach { drink ->
            val button = Button(this).apply {
                text = drink.name
                //TODO:UI things here(picture of drink)
                //Could maybe add picture to data class for
                //repeated use??
                textSize = 18f
                setOnClickListener {
                    val intent = Intent(this@MainActivity, Drink_Customization::class.java)
                    intent.putExtra("selected_drink", drink)
                    startActivity(intent)
                }
            }
            buttonContainer.addView(button)
        }
        /*
        //Button for latte customization
        val coffeeButton: Button? = findViewById(R.id.squareButton)
        coffeeButton?.setOnClickListener {
            val latte = Drink_Menu.defaultDrinks.first { it.name == "Latte" }
            // Launch DrinkCustomization
            val intent = Intent(this, Drink_Customization::class.java)
            //Places data from latte into Drink_Customization
            intent.putExtra("selected_drink", latte)
            startActivity(intent)
        }
*/
        val userRef = FirebaseDatabase.getInstance().getReference("users")
        //extract userID after login
        val userId = intent.getStringExtra("userId")
        val userText: TextView = findViewById(R.id.user)
        //If valid user adds first name to welcome
        if (userId != null) {
            userRef.child(userId).get().addOnSuccessListener { snapshot ->
                val user = snapshot.getValue(UserData::class.java)
                userText.text = "Welcome ${user?.firstName}"
            }
        }

        val spinner: Spinner = findViewById(R.id.location_dropdown)
        ArrayAdapter.createFromResource(
            this, R.array.locations, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        navigation = findViewById(R.id.bottom_navigation)

        navigation.selectedItemId = R.id.home

        navigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.account -> {
                    val intent = Intent(this, Account::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)

                    finish()
                    true
                }

                R.id.basket -> {
                    val intent = Intent(this, BasketActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)

                    finish()
                    true
                }

                else -> false
            }
        }
    }
}

val drinksRef = FirebaseDatabase.getInstance().getReference("drinks")
// Function to add or update a user
fun addItem(drink: Drink_Menu) {
    fun addItem(drink: Drink_Menu): String? {
        val drinkId = drink.id.ifEmpty { drinksRef.push().key } ?: return null
        drink.id = drinkId

        drinksRef.child(drinkId).setValue(drink)
        return drinkId
    }
}

fun removeDrink(userId: String) {
    drinksRef.child(userId).removeValue()
}